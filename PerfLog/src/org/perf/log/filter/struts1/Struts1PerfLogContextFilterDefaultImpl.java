/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/filter/struts1/Struts1PerfLogContextFilterDefaultImpl.java 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.perf.log.filter.struts1;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.perf.log.context.PerfLogContext;
import org.perf.log.context.PerfLogContextHelper;
import org.perf.log.logger.Logger;
import org.perf.log.logger.LoggerFactory;
import org.perf.log.logger.PerfLogData;
import org.perf.log.logger.PerfLogger;
import org.perf.log.logger.PerfLoggerFactory;
import org.perf.log.properties.LoggerProperties;
import org.perf.log.txn.types.PerfLogTxnType;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Enumeration;


public class Struts1PerfLogContextFilterDefaultImpl implements
		Struts1PerfLogContextFilter {
	int MAX_PARAM_VALUE_OF_INTEREST = 64; // max value size to store in info context
	private final static Logger logger = LoggerFactory
			.getLogger("Struts1PerfLogContextFilterDefaultImpl");
	private final static PerfLogger plogger = PerfLoggerFactory.getLogger();

	private String getActionName(ActionForm actionForm) {
		@SuppressWarnings("rawtypes")
		Class params[] = {};
		Object paramsObj[] = {};
		Method getActionMethod = null;
		String ignoreExceptionMsg = ":ignored exception (PerfStrutsRequestProcess)";
		try {
			getActionMethod = actionForm.getClass().getDeclaredMethod(
					"getAction", params);
		} catch (SecurityException e) {
			// ignore..
			logger.debug(e.getMessage() + ignoreExceptionMsg);
			return null;
		} catch (NoSuchMethodException e) {
			// ignore
			logger.debug(e.getMessage() + ignoreExceptionMsg);
			return null;
		}
		if (getActionMethod != null) {
			try {
				return (String) getActionMethod.invoke(actionForm, paramsObj);
			} catch (IllegalArgumentException e) {
				logger.debug(e.getMessage() + ignoreExceptionMsg);
				return null;
			} catch (IllegalAccessException e) {
				logger.debug(e.getMessage() + ignoreExceptionMsg);
				return null;
			} catch (InvocationTargetException e) {
				logger.debug(e.getMessage() + ignoreExceptionMsg);
				return null;
			}
		}
		return null;
	}

	@Override
	public void afterPerfLogContextCreation(HttpServletRequest request,
			HttpServletResponse response, Action action, ActionForm actionForm,
			ActionMapping actionMapping, PerfLogContext perfLogContext) {
		String tmpValueStr;
		PerfLogContextHelper.pushInfoContext(Struts1Constants.ACTION_SERVLET, (tmpValueStr = action.getServlet()
				.getServletName()));
		PerfLogContextHelper.addToTxnList(tmpValueStr);
		PerfLogContextHelper.pushInfoContext(Struts1Constants.ACTION_MAPPING_NAME, (tmpValueStr = actionMapping
				.getName()));
		PerfLogContextHelper.addToTxnList(tmpValueStr);
		PerfLogContextHelper.pushInfoContext(Struts1Constants.ACTION_MAPPING_TYPE, (tmpValueStr = actionMapping
				.getType()));
		PerfLogContextHelper.addToTxnList(tmpValueStr);
		PerfLogContextHelper.pushInfoContext(Struts1Constants.ACTION_FORM, (tmpValueStr = actionForm.getClass()
				.getSimpleName()));
		PerfLogContextHelper.addToTxnList(tmpValueStr);
		PerfLogContextHelper.pushInfoContext(Struts1Constants.ACTION, (tmpValueStr = getActionName(actionForm)));
		PerfLogContextHelper.addToTxnList(tmpValueStr);
		
		PerfLogContextHelper.setRequestSessionId(request.getRequestedSessionId());
		if(request.getUserPrincipal()!=null)
			PerfLogContextHelper.setUserId(request.getUserPrincipal().getName());
		
		@SuppressWarnings("unchecked")
		Enumeration<String> paramEnum = request.getParameterNames();
		while (paramEnum.hasMoreElements()) {
			String paramName = (String) paramEnum.nextElement();
			String paramValue = request.getParameter(paramName);
			// check param name starts with a letter to reduce space usage
			// ignore param name starting with a number
			if(paramValue != null
				&& paramName !=null &&  Character.isLetter(paramName.charAt(0)))
				PerfLogContextHelper.pushInfoContext(paramName, paramValue, MAX_PARAM_VALUE_OF_INTEREST);
			else
				if(paramName !=null &&  Character.isLetter(paramName.charAt(0)))
					PerfLogContextHelper.addToRequestDataContext(paramName, paramValue, MAX_PARAM_VALUE_OF_INTEREST);
		}
				

	}

	

	private void logPerfMetrics(HttpServletRequest request,
			HttpServletResponse response, Action action, ActionForm actionForm,
			ActionMapping actionMapping, long elapsedTime, PerfLogContext perfLogContext, Throwable t) {
	
		
		if (perfLogContext == null)
			return;
		
		PerfLogData pData = new PerfLogData(perfLogContext);
		// set transaction date i.e. start time to the time this filter was created
		pData.setTransactionDate(new Date(perfLogContext.getTxnFilterCreationTime(perfLogContext.getTxnFilterDepth())));
		pData.setTransactionType(PerfLogTxnType.STRUTS_ACTION_REQUEST);
		pData.setGuid(perfLogContext.getGuid());
		pData.setSessionId(perfLogContext.getRequestSessionId());
		pData.setThreadName(perfLogContext.getThreadName());
		pData.setPerfLogContext(perfLogContext);// implementation can get additional
											// details
		pData.setServerName(perfLogContext.getHostId());
		pData.setServerIp(perfLogContext.getHostIp());
		pData.setCloneName(perfLogContext.getJvmCloneId());
		pData.setTransactionName(perfLogContext
				.getValueForInfoContextName(Struts1Constants.ACTION_FORM));
		pData.setSubTransactionName(perfLogContext
				.getValueForInfoContextName(Struts1Constants.ACTION));
		pData.setUserId(perfLogContext.getUserId());
		pData.setTransactionTime(elapsedTime);
		pData.setThrowable(t);
		plogger.log(pData);
	}
	
	@Override
	public void beforePerfLogContextDeletion(HttpServletRequest request,
			HttpServletResponse response, Action action, ActionForm actionForm,
			ActionMapping actionMapping, PerfLogContext perfLogContext, Throwable t) {
		
		if(perfLogContext == null) return;
		long elapsedTime = perfLogContext.getElapsedTimeFromTxnFilterCreation();
		if(LoggerProperties.getInstance().isPerfLogStrutsEnabled())
			logPerfMetrics(request, response, action, actionForm, actionMapping, elapsedTime, perfLogContext, t);

	}

}
