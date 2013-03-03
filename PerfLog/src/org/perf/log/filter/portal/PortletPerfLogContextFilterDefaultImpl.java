/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/filter/portal/PortletPerfLogContextFilterDefaultImpl.java 
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
package org.perf.log.filter.portal;

import java.util.Date;
import java.util.Enumeration;
import java.util.regex.Pattern;

import javax.portlet.ActionRequest;
import javax.portlet.EventRequest;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.ResourceRequest;

import org.perf.log.context.PerfLogContext;
import org.perf.log.context.PerfLogContextHelper;
import org.perf.log.logger.Logger;
import org.perf.log.logger.LoggerFactory;
import org.perf.log.logger.PerfLogData;
import org.perf.log.logger.PerfLogger;
import org.perf.log.logger.PerfLoggerFactory;
import org.perf.log.properties.LoggerProperties;
import org.perf.log.txn.types.PerfLogTxnType;
import org.perf.log.utils.PortletInfoGetterFactory;


public class PortletPerfLogContextFilterDefaultImpl implements
		PortletPerfLogContextFilter {
	private final static Logger logger = LoggerFactory.getLogger("PortletPerfLogContextFilterDefaultImpl");
	private final static PerfLogger plogger = PerfLoggerFactory.getLogger();
	
	private int MAX_VALUE_SIZE = 64;
	
	private void pushFormParamsToInfoContext(PortletRequest request,
			PortletResponse response, PerfLogContext perfLogContext) {
		Enumeration<String> paramEnum = request.getParameterNames();
		String formName = null;
		String shortFormName = null;

		// The form name is identified from the request parameters where name =
		// value
		while (paramEnum.hasMoreElements()) {
			String paramName = paramEnum.nextElement();
			String paramValue = request.getParameter(paramName);
			if (paramName.equals(paramValue))
				formName = paramName;
		}
		// An example of formName is -
		// viewns_7_OIMTTQV41GG970I2B8KJU40G41_:maxeOutboundMainSearchForm
		// Strip out the WebSphere Portal prefixed identifiers from the name and
		// only get the characters after the :
		if (formName != null) {
			int i;
			shortFormName = formName;
			if ((i = formName.lastIndexOf(':')) > 0) {
				shortFormName = formName.substring(i + 1);
			}
			// else keep the full name
			// Add portletForm to information context
			PerfLogContextHelper.pushInfoContext(
					PortletConstants.PORTLET_FORM_NAME, shortFormName);
		}
		// rescan for paramEnum sorting out name/value pair without the formName
		paramEnum = request.getParameterNames();
		while (paramEnum.hasMoreElements()) {
			String paramName = paramEnum.nextElement();
			String paramValue = request.getParameter(paramName);
			// if paramName or paramValue is null or blank add this info in the
			// request data
			// other wise add it to the info context
			if (formName != null && paramName != null
					&& Pattern.matches(formName + ":.*", paramName)
					&& paramValue != null && !paramValue.equals("")) {

				// a typical form param values is of the form as shown
				// viewns_7_OIMTTQV41GG970I2B8KJU40G47_:form1=viewns_7_OIMTTQV41GG970I2B8KJU40G47_:form1
				// viewns_7_OIMTTQV41GG970I2B8KJU40G47_:form1:createdFromDateInput=03/27/2012
				// viewns_7_OIMTTQV41GG970I2B8KJU40G47_:form1:incidentTypeMenu=10
				// viewns_7_OIMTTQV41GG970I2B8KJU40G47_:form1:searchComplaintsButton=Search
				// To save space on info context, just take the actual
				// name=value data and
				// skip all the preceeding portlet junk..

				int startIndex = paramName.lastIndexOf(':') + 1;
				// safe indexing...
				if (startIndex < 0)
					startIndex = 0;

				int endIndex = paramName.length();
				if (endIndex < 0)
					endIndex = 0;

				paramName = paramName.substring(startIndex, endIndex);
				// do an extra check to ensure the paramName starts with an alphabet
				// certain form params were see to be numeric that doesn't provide any value
				if(paramName !=null &&  Character.isLetter(paramName.charAt(0)))
					PerfLogContextHelper.pushInfoContext(paramName, paramValue, MAX_VALUE_SIZE);
			} else {
				// add to request data..
				PerfLogContextHelper
						.addToRequestDataContext(paramName, paramValue, MAX_VALUE_SIZE);
			}
		}
	}

	@Override
	public void afterPerfLogContextCreation(PortletRequest request,
			PortletResponse response, String phase, PerfLogContext perfLogContext) {

		logger.debug("afterPerfLogContextCreation");
		
		PerfLogContextHelper.setRequestSessionId(request.getRequestedSessionId());
		if(request.getUserPrincipal() != null)
			PerfLogContextHelper.setUserId(request.getUserPrincipal().getName());
		
		pushPortletLogContext(request, response, phase);
		// this is used to identify the transactions used to check in the current context.
		PerfLogContextHelper.addToTxnList(perfLogContext.getValueForInfoContextName(PortletConstants.PORTAL_PAGE_NAME));
		PerfLogContextHelper.addToTxnList(perfLogContext.getValueForInfoContextName(PortletConstants.PORTAL_PORTLET_NAME));
		
		// add form data for action phase
		if(phase.equals(PortletConstants.PORTLET_PHASE_ACTION) ||
				phase.equals(PortletConstants.PORTLET_PHASE_EVENT)) {
			pushFormParamsToInfoContext(request,response,perfLogContext);
		}
		else {
			// add any additional debug context to identify the portlet for
			// diagnosis
			Enumeration<String> paramEnum = request.getParameterNames();
			while (paramEnum.hasMoreElements()) {
				String paramName = paramEnum.nextElement();
				String paramValue = request.getParameter(paramName);
				PerfLogContextHelper.addToRequestDataContext(paramName, paramValue, MAX_VALUE_SIZE);
			}
		}
	}

	@Override
	public void beforePerfLogContextDeletion(PortletRequest request,
			PortletResponse response, String phase, PerfLogContext perfLogContext,
			Throwable t) {
		if(perfLogContext == null || phase == null) return;
		long elapsedTime = perfLogContext.getElapsedTimeFromTxnFilterCreation();
		// log the performance metric using a PerfLogger
		logger.debug("beforePerfLogContextDeletion");
		PerfLogContextHelper.compensateForOutboundJvmCallExceptionIfAny();
		if(LoggerProperties.getInstance().isPerfLogPortletEnabled() &&
				(elapsedTime >= LoggerProperties.getInstance().getPerfLogPortletThreshold()))
			logPerfMetrics(request, response, phase, elapsedTime, perfLogContext, t);
		

	}

private void logPerfMetrics(PortletRequest request, PortletResponse response, String phase, long elapsedTime, PerfLogContext perfLogContext, Throwable t) {
		
		
		if(perfLogContext == null || phase == null)
			return;
		PerfLogData pData = new PerfLogData(perfLogContext);
		// set transaction date i.e. start time to the time this filter was created
		pData.setTransactionDate(new Date(perfLogContext.getTxnFilterCreationTime(perfLogContext.getTxnFilterDepth())));
		if(phase.equals(PortletConstants.PORTLET_PHASE_ACTION))
			pData.setTransactionType(PerfLogTxnType.PORTLET_ACTION_REQUEST);
		else if(phase.equals(PortletConstants.PORTLET_PHASE_EVENT))
			pData.setTransactionType(PerfLogTxnType.PORTLET_EVENT_REQUEST);
		else if(phase.equals(PortletConstants.PORTLET_PHASE_RENDER))
			pData.setTransactionType(PerfLogTxnType.PORTLET_RENDER_REQUEST);
		else if(phase.equals(PortletConstants.PORTLET_PHASE_RESOURCE))
			pData.setTransactionType(PerfLogTxnType.PORTLET_RESOURCE_REQUEST);
		else
			pData.setTransactionType("Unknown");;
		pData.setGuid(perfLogContext.getGuid());
		pData.setSessionId(perfLogContext.getRequestSessionId());
		pData.setThreadName(perfLogContext.getThreadName());
		pData.setPerfLogContext(perfLogContext);// implementation can get additional details
		pData.setMessage(null);
		pData.setServerName(perfLogContext.getHostId());
		pData.setServerIp(perfLogContext.getHostIp());
		pData.setCloneName(perfLogContext.getJvmCloneId());
		pData.setTransactionName(perfLogContext.getValueForInfoContextName(PortletConstants.PORTAL_PAGE_NAME));
		pData.setSubTransactionName(perfLogContext.getValueForInfoContextName(PortletConstants.PORTAL_PORTLET_NAME));
		pData.setUserId(perfLogContext.getUserId());
		pData.setTransactionTime(elapsedTime);
		pData.setThrowable(t);
		plogger.log(pData);
	}

// util function to push portlet specific context information
public void pushPortletLogContext(PortletRequest request,
		PortletResponse response, String phase) {

	PerfLogContextHelper.pushInfoContext(PortletConstants.PORTAL_PAGE_NAME,
			PortletInfoGetterFactory.getPortletInfoGetterImpl().getPageName(request, response));
	PerfLogContextHelper.pushInfoContext(PortletConstants.PORTAL_PORTLET_NAME,
			PortletInfoGetterFactory.getPortletInfoGetterImpl().getName(request, response));
	PerfLogContextHelper.pushInfoContext(PortletConstants.PORTAL_PHASE, phase);
	
	// Push request type data if available
	if(request instanceof ActionRequest) {
		//ActionRequest actionRequest = (ActionRequest)request;
		//PerfLogContextHelper.pushInfoContext("portletActionName", actionRequest.ACTION_NAME);			
	}
	else if(request instanceof EventRequest) {
		EventRequest eventRequest = (EventRequest)request;
		PerfLogContextHelper.pushInfoContext("portletEventName", eventRequest.getEvent().getName());
		PerfLogContextHelper.addToRequestDataContext("portletEventQName", eventRequest.getEvent().getQName().getNamespaceURI());
		
	}
	else if(request instanceof RenderRequest) {
		// RenderRequest renderRequest = (RenderRequest)request;
		// no useful data worth logging yet
	}
	else if(request instanceof ResourceRequest) {
		ResourceRequest resourceRequest = (ResourceRequest)request;
		PerfLogContextHelper.pushInfoContext("portletResourceID", resourceRequest.getResourceID());
	}

}


}
