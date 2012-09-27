/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/filter/servlet/ServletPerfLogContextFilterDefaultImpl.java 
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
package org.perf.log.filter.servlet;

import java.util.Date;
import java.util.Enumeration;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.perf.log.context.PerfLogContext;
import org.perf.log.context.PerfLogContextHelper;
import org.perf.log.logger.PerfLogData;
import org.perf.log.logger.PerfLogger;
import org.perf.log.logger.PerfLoggerFactory;
import org.perf.log.properties.LoggerProperties;
import org.perf.log.txn.types.PerfLogTxnType;


public class ServletPerfLogContextFilterDefaultImpl implements ServletPerfLogContextFilter {
	
	private int MAX_VALUE_SIZE = 64;

	private final static PerfLogger plogger = PerfLoggerFactory.getLogger();
	
	

	@Override
	public void afterPerfLogContextCreation(ServletRequest request,
			ServletResponse response, PerfLogContext perfLogContext) {
		HttpServletRequest httpReq = (HttpServletRequest) request;
		
		if (request instanceof HttpServletRequest
				&& response instanceof HttpServletResponse) {
			perfLogContext.setRequestSessionId(httpReq.getRequestedSessionId());
			if(httpReq.getUserPrincipal()!=null)
				perfLogContext.setUserId(httpReq.getUserPrincipal().getName());
			else
				perfLogContext.setUserId(httpReq.getRemoteUser());

			PerfLogContextHelper.pushInfoContext(ServletConstants.URI, httpReq
					.getRequestURI());
			// add to guid context txn list
			PerfLogContextHelper.addToTxnList(httpReq.getRequestURI());

			if (httpReq.getQueryString() != null) {
				PerfLogContextHelper
						.addToRequestDataContext(ServletConstants.QUERY_STRING,
								httpReq.getQueryString());
				// add to query string to guid context txnlist for transaction identification
				PerfLogContextHelper.addToTxnList(httpReq.getQueryString());

				PerfLogContextHelper.addToRequestDataContext("remoteAddr", httpReq
						.getRemoteAddr());
				PerfLogContextHelper.addToRequestDataContext("remoteHost", httpReq
						.getRemoteHost());
				//--------------------------------------------------------
				// Add request parameters to context	
				@SuppressWarnings("unchecked")
				Enumeration<String> paramEnum = request.getParameterNames();
				while (paramEnum.hasMoreElements()) {
					String paramName = (String) paramEnum.nextElement();
					String paramValue = request.getParameter(paramName);
					// add request parameters names that start with an alphabet only
					// Also limit value size to a maximum size
					// These filters unwanted name value pairs generated by framework or binary data 
					// that just occupy space
					if(paramValue != null
							&& paramName !=null &&  Character.isLetter(paramName.charAt(0))) {
						
						PerfLogContextHelper.pushInfoContext(paramName, paramValue, MAX_VALUE_SIZE);
					}
					else
						if(paramName !=null &&  Character.isLetter(paramName.charAt(0)))
							PerfLogContextHelper.addToRequestDataContext(paramName, paramValue, MAX_VALUE_SIZE);
				}
				//------------------------------
			}
		}

	}

	@Override
	public void beforePerfLogContextDeletion(ServletRequest request,
			ServletResponse response, PerfLogContext perfLogContext, Throwable t) {
		if(perfLogContext == null) return;
		long elapsedTime = perfLogContext.getElapsedTimeFromTxnFilterCreation();
		if(LoggerProperties.getInstance().isPerfLogServletEnabled() &&
				(elapsedTime >= LoggerProperties.getInstance().getPerfLogServletThreshold()))
			logPerfMetrics(request, response, elapsedTime, perfLogContext, t);

	}

	private void logPerfMetrics(ServletRequest request,
			ServletResponse response, long elapsedTime, PerfLogContext perfLogContext, Throwable t) {
	
		
		if (perfLogContext == null)
			return;
		PerfLogData pData = new PerfLogData(perfLogContext);
		pData.setTransactionType(PerfLogTxnType.SERVLET_REQUEST);
		pData.setGuid(perfLogContext.getGuid());
		pData.setSessionId(perfLogContext.getRequestSessionId());
		pData.setThreadName(perfLogContext.getThreadName());
		pData.setPerfLogContext(perfLogContext);// implementation can get additional
											// details
	
		pData.setServerName(perfLogContext.getHostId());
		pData.setServerIp(perfLogContext.getHostIp());
		pData.setCloneName(perfLogContext.getJvmCloneId());
		// set transaction date i.e. start time to the time this filter was created
		pData.setTransactionDate(new Date(perfLogContext.getTxnFilterCreationTime(perfLogContext.getTxnFilterDepth())));
		pData.setTransactionName(perfLogContext
				.getValueForInfoContextName(ServletConstants.URI));
		pData.setSubTransactionName(perfLogContext
				.getValueForInfoContextName(ServletConstants.QUERY_STRING));
		pData.setUserId(perfLogContext.getUserId());
		pData.setTransactionTime(elapsedTime);
		pData.setThrowable(t);
		plogger.log(pData);
	}

}