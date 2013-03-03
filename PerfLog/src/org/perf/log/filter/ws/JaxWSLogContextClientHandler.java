/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/filter/ws/JaxWSLogContextClientHandler.java 
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
package org.perf.log.filter.ws;

import java.util.Date;
import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.perf.log.context.PerfLogContext;
import org.perf.log.context.PerfLogContextHelper;
import org.perf.log.context.PerfLogContextTrackingData;
import org.perf.log.logger.Logger;
import org.perf.log.logger.LoggerFactory;
import org.perf.log.logger.PerfLogData;
import org.perf.log.logger.PerfLogger;
import org.perf.log.logger.PerfLoggerFactory;
import org.perf.log.properties.LoggerProperties;
import org.perf.log.txn.types.PerfLogTxnType;


public class JaxWSLogContextClientHandler extends JaxWSLogContextHandler {

	private final static PerfLogger plogger = PerfLoggerFactory.getLogger();
	private final static Logger logger = LoggerFactory.getLogger("JaxWSLogContextClientHandler");

	@Override
	public boolean handleMessage(SOAPMessageContext soapMsgContext) {
		boolean direction = ((Boolean) soapMsgContext
	            .get(MessageContext.MESSAGE_OUTBOUND_PROPERTY))
	            .booleanValue();
		if(direction) {
			///-------------------- begin of client side request processing --------------------------
			// This block is called when going out of JVM
			
			try {
	
				logger.debug("handleMessage: createPerfLogContext ");
	
				// create a guid context, this is required to get some
				// contextual information from this JVM before calling the
				// service in the other JVM..
				PerfLogContextHelper.startPerfLogTxnMonitor();
			
				addMessagePropertiesToPerfLogContext(soapMsgContext);
				if(LoggerProperties.getInstance().isPerfLogWSCacheSOAPMessage())
					addSOAPMessageStringToPerfLogContextRequestData(
						"JaxWSClientSOAPRequest", getSOAPMessageString(soapMsgContext));
				
				PerfLogContext thisThreadPerfLogContext = PerfLogContextHelper
						.getCurrentThreadPerfLogContextObject();
	
								
				PerfLogContextTrackingData perfLogContextTrackingData = new PerfLogContextTrackingData(
						thisThreadPerfLogContext.getGuid(), 
						"" + thisThreadPerfLogContext.getContextCreationTime(),
						thisThreadPerfLogContext.getHostId(),
						thisThreadPerfLogContext.getHostIp(),
						thisThreadPerfLogContext.getJvmCloneId(), 
						""	+ thisThreadPerfLogContext.getJvmDepth(),
						thisThreadPerfLogContext.getRequestSessionId(), 
						thisThreadPerfLogContext.getUserId());
				
				boolean retValue = addLogContextToSOAPHeader(soapMsgContext, perfLogContextTrackingData);
				
				if (retValue == false) {
					logger.error(
							"handleRequest():Unable to add SOAP Headear to outgoing web service request");
				}
				logger.debug("handleMessage():guid=" + perfLogContextTrackingData.getGuid() + 
						" requestSessionId=" + perfLogContextTrackingData.getSessionId() + 
						" callingHostId=" + perfLogContextTrackingData.getCallingJvmHostId() +
						" callingJVMCloneId=" + perfLogContextTrackingData.getCallingJvmCloneId() +
						" callingJVMDepth="	+ perfLogContextTrackingData.getCallingJvmDepthStr());
				// track elapsed time for this web service...
				soapMsgContext.put(
						ContextHandlerConstants.PROPERTY_NAME_startTime, new Long(System.currentTimeMillis()));
				PerfLogContextHelper.compensateForOutboundJvmCallExceptionIfAny();
				PerfLogContextHelper.setAwatingReturnFromOutboundJvmCall(true);
				return true;
			}
			finally {
			}
			//-------------------- end of client side request processing --------------------------
		}
		else {
			//-------------------- begin of client side response processing --------------------------
			// this block will execute in when the response is received back from the server
			
			try {
				logger.debug(
						"handleMessage: jax-ws response");
				if(LoggerProperties.getInstance().isPerfLogWSCacheSOAPMessage())
					addSOAPMessageStringToPerfLogContextRequestData(
						"JaxWSClientSOAPResponse",
						getSOAPMessageString(soapMsgContext));
				
				
				
			} finally {
				PerfLogContext perfLogContext = PerfLogContextHelper.getCurrentThreadPerfLogContextObject();
				long elapsedTime = getElapsedTime(soapMsgContext);
				if(LoggerProperties.getInstance().isPerfLogWSEnabled() &&
						(elapsedTime >= LoggerProperties.getInstance().getPerfLogWSThreshold()))
					logPerfMetrics(soapMsgContext, elapsedTime,perfLogContext, null,null);
				
				logger.debug("handleMessage: endPerfLogTxnMonitor");
				PerfLogContextHelper.setAwatingReturnFromOutboundJvmCall(false);
				PerfLogContextHelper.endPerfLogTxnMonitor();
			}
			
			
			//-------------------- end of client side response processing --------------------------
		}
		
		
		
		
		return true;
	}

	private void logPerfMetrics(MessageContext context, long elapsedTime, PerfLogContext perfLogContext, Throwable t, String detailedFaultMessage) {
	    
		logger.debug("logPerfMetrics: elapsedTime = " + elapsedTime);
		// It is possible that Context may be null, when web service is invoked by spawned thread
		// and is not a result of a portlet or a servlet request..
		if(perfLogContext == null) return;
		QName propQName;
		PerfLogData pData = new PerfLogData(perfLogContext);
		pData.setTransactionType(PerfLogTxnType.JAX_WS_CLIENT_REQEUST);
		String propValue = (String)context.get(ContextHandlerConstants.PROPERTY_NAME_JAXWS_CLIENT_ENDPOINT);
		pData.setTransactionName(propValue);
		propQName = (QName) context.get(ContextHandlerConstants.PROPERTY_NAME_JAXWS_WSDL_OPERATION);
		if(propQName != null)
			pData.setSubTransactionName(propQName.toString());
		else
			pData.setSubTransactionName(null);
		pData.setTransactionTime(elapsedTime);
		pData.setThrowable(t);
		pData.setMessage(detailedFaultMessage);
		// set transaction date i.e. start time to the time this filter was created
		pData.setTransactionDate(new Date(perfLogContext.getTxnFilterCreationTime(perfLogContext.getTxnFilterDepth())));		
		pData.setGuid(perfLogContext.getGuid());
		pData.setSessionId(perfLogContext.getRequestSessionId());
		pData.setThreadName(perfLogContext.getThreadName());
		pData.setPerfLogContext(perfLogContext);// implementation can get additional details
		pData.setServerName(perfLogContext.getHostId());
		pData.setServerIp(perfLogContext.getHostIp());
		pData.setCloneName(perfLogContext.getJvmCloneId());
		pData.setUserId(perfLogContext.getUserId());
		plogger.log(pData);
	}

	protected void addMessagePropertiesToPerfLogContext(SOAPMessageContext context) {
	
		/*-------------------------------------------------------------------------------
		// iterate over all the properties - use for debug and knowing what other name/value
		// could be useful
		Iterator<String> iterator = context.keySet().iterator();
		while(iterator.hasNext()) {
			Object name = (Object)iterator.next();
			Object value = (Object)context.get(name);
			logger.debug("addMessagePropertiesToPerfLogContext: name = "+name + " value = "+value  + " value of type = " 
					+ ((value != null)? value.getClass().getName(): "value null"));
			if(name!= null && value != null && name instanceof String && value instanceof String) {
				PerfLogContextHelper.addToRequestDataContext((String)name,(String)value);
			}
			
			
		}
		/------------------------------------------------------------------------------------------*/
		
		Boolean outboundProperty = (Boolean) context
				.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if (outboundProperty.booleanValue())
			PerfLogContextHelper
					.addToRequestDataContext(
							ContextHandlerConstants.PROPERTY_NAME_JAXWS_MESSAGE_DIRECTION,
							ContextHandlerConstants.PROPERTY_VALUE_JAXWS_MESSAGE_OUTBOUND);
		else
			PerfLogContextHelper
					.addToRequestDataContext(
							ContextHandlerConstants.PROPERTY_NAME_JAXWS_MESSAGE_DIRECTION,
							ContextHandlerConstants.PROPERTY_VALUE_JAXWS_MESSAGE_INBOUND);
		
		String propValue = (String) context.get(ContextHandlerConstants.PROPERTY_NAME_JAXWS_PATHINFO);
		if(propValue!=null)
			PerfLogContextHelper.addToRequestDataContext(
						ContextHandlerConstants.PROPERTY_NAME_JAXWS_PATHINFO,
						propValue);
		propValue = (String) context.get(ContextHandlerConstants.PROPERTY_NAME_JAXWS_QUERY_STRING);
		if(propValue != null)
			PerfLogContextHelper.addToRequestDataContext(
				ContextHandlerConstants.PROPERTY_NAME_JAXWS_QUERY_STRING,
				propValue);
		QName propQName = (QName) context
				.get(ContextHandlerConstants.PROPERTY_NAME_JAXWS_WSDL_INTERFACE);
		if (propQName != null)
			PerfLogContextHelper.addToRequestDataContext(
					ContextHandlerConstants.PROPERTY_NAME_JAXWS_WSDL_INTERFACE,
					propQName.toString());
		propValue = getOperationName(context);
		if (propValue != null) {
			PerfLogContextHelper.addToRequestDataContext(
					ContextHandlerConstants.PROPERTY_NAME_JAXWS_WSDL_OPERATION,
					propValue);
			PerfLogContextHelper.addToTxnList(propValue);
		}
		propQName = (QName) context.get(ContextHandlerConstants.PROPERTY_NAME_JAXWS_WSDL_SERVICE);
		if (propQName != null)
			PerfLogContextHelper.addToRequestDataContext(
					ContextHandlerConstants.PROPERTY_NAME_JAXWS_WSDL_SERVICE,
					propQName.toString());
		
		propValue = (String)context.get(ContextHandlerConstants.PROPERTY_NAME_JAXWS_CLIENT_ENDPOINT);
		if(propValue != null) {
			PerfLogContextHelper.addToRequestDataContext(
				ContextHandlerConstants.PROPERTY_NAME_JAXWS_CLIENT_ENDPOINT, propValue);
			PerfLogContextHelper.addToTxnList(propValue);
		}
		
		Object value = context.get(ContextHandlerConstants.PROPERTY_NAME_JAXWS_WSDL_DESCRIPTION);
		if(value != null)
			PerfLogContextHelper.addToRequestDataContext(
			ContextHandlerConstants.PROPERTY_NAME_JAXWS_WSDL_DESCRIPTION, value.toString());
		
		
		
				
		
	}

	/* (non-Javadoc)
	 * @see javax.xml.ws.handler.Handler#handleFault(javax.xml.ws.handler.MessageContext)
	 */
	@Override
	public boolean handleFault(SOAPMessageContext soapMsgContext) {
		String faultMessage = null;
		try {
			faultMessage = getSOAPMessageString(soapMsgContext);
			if (logger.getDebugEnabled()) {
				logger.debug("handleFault: " + faultMessage);
			}
			if (PerfLogContextHelper.getCurrentThreadPerfLogContextObject() != null
					&&
				(LoggerProperties.getInstance().isPerfLogWSCacheSOAPMessage())
				)
				PerfLogContextHelper.addToDebugContext(this.getClass().getName()
						+ ":handleFault()", faultMessage);
		} finally {
			PerfLogContext perfLogContext = PerfLogContextHelper.getCurrentThreadPerfLogContextObject();
			long elapsedTime = getElapsedTime(soapMsgContext);
			if(LoggerProperties.getInstance().isPerfLogWSEnabled() &&
					(elapsedTime >= LoggerProperties.getInstance().getPerfLogWSThreshold()))
				logPerfMetrics(soapMsgContext, elapsedTime, perfLogContext,  
						new Exception("JaxWSLogContextClientHandler:handleFault"),faultMessage);
			logger.debug("handleFault: jax-ws client - endPerfLogTxnMonitor");
			PerfLogContextHelper.setAwatingReturnFromOutboundJvmCall(false);
			PerfLogContextHelper.endPerfLogTxnMonitor();
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see javax.xml.ws.handler.Handler#close(javax.xml.ws.handler.MessageContext)
	 */
	@Override
	public void close(MessageContext context) {
		boolean direction = ((Boolean) context
	            .get(MessageContext.MESSAGE_OUTBOUND_PROPERTY))
	            .booleanValue();
		logger.debug("close(): direction = " + direction);
				
	}

}
