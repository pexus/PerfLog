/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/filter/ws/JaxWSLogContextServerHandler.java 
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
import javax.xml.ws.handler.MessageContext;
import javax.xml.namespace.QName;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.perf.log.context.PerfLogContext;
import org.perf.log.context.PerfLogContextConstants;
import org.perf.log.context.PerfLogContextHelper;
import org.perf.log.context.PerfLogContextTrackingData;
import org.perf.log.logger.Logger;
import org.perf.log.logger.LoggerFactory;
import org.perf.log.logger.PerfLogData;
import org.perf.log.logger.PerfLogger;
import org.perf.log.logger.PerfLoggerFactory;
import org.perf.log.properties.LoggerProperties;
import org.perf.log.txn.types.PerfLogTxnType;



public class JaxWSLogContextServerHandler extends JaxWSLogContextHandler {

	private final static PerfLogger plogger = PerfLoggerFactory.getLogger();
	private final static Logger logger = LoggerFactory.getLogger("JaxWSLogContextServerHandler");

	@Override
	public boolean handleMessage(SOAPMessageContext soapMsgContext) {
		boolean direction = ((Boolean) soapMsgContext
				.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY))
				.booleanValue();
		if (!direction) {
			// request coming into server side handler
			logger.debug("handleMessage()");

			String soapMessageString = getSOAPMessageString(soapMsgContext);

			PerfLogContextTrackingData perfLogContextTrackingData;

			perfLogContextTrackingData = retrieveLogContextFromSOAPHeader(soapMsgContext);

			// create a thread context
			logger.debug("handleMessage(): guid = "+ perfLogContextTrackingData.getGuid()
					+" sessionId="+perfLogContextTrackingData.getSessionId()
					+" callingJVMHostId="+perfLogContextTrackingData.getCallingJvmHostId()
					+" callingJVMCloneId="+perfLogContextTrackingData.getCallingJvmCloneId()
					+" callingJVMDepth="+perfLogContextTrackingData.getCallingJvmDepthStr()
					+" userId="+perfLogContextTrackingData.getUserId()
					+" createTime="+perfLogContextTrackingData.getCreateTimeInMillisStr());
			logger.debug("handleMessage: createPerfLogContext");
			createPerfLogContextForWSRequest(soapMsgContext, perfLogContextTrackingData);
			addSOAPMessageStringToPerfLogContextRequestData(
					"JaxWSServerSOAPRequest", soapMessageString);
			// track elapsed time for this web service...
			soapMsgContext.put(
					ContextHandlerConstants.PROPERTY_NAME_startTime, new Long(System.currentTimeMillis()));
		} else {
			try {
				logger.debug(
						"handleMessage(): response");
				if(LoggerProperties.getInstance().isPerfLogWSCacheSOAPMessage())
					addSOAPMessageStringToPerfLogContextRequestData(
						"JaxWSServerSOAPResponse", 
						getSOAPMessageString(soapMsgContext));
				
								
			} finally {
				PerfLogContext perfLogContext = PerfLogContextHelper.getCurrentThreadPerfLogContextObject();
				long elapsedTime = getElapsedTime(soapMsgContext);
				if(LoggerProperties.getInstance().isPerfLogWSEnabled() &&
						(elapsedTime >= LoggerProperties.getInstance().getPerfLogWSThreshold()))
						logPerfMetrics(soapMsgContext, elapsedTime, perfLogContext,null,null);
				logger.debug("handleMessage(): deletePerfLogContext");
				PerfLogContextHelper.endPerfLogTxnMonitor();
			}

		}
		return true;
	}

	protected boolean createPerfLogContextForWSRequest(SOAPMessageContext soapMsgContext,
			PerfLogContextTrackingData perfLogContextTrackingData) {
		
		PerfLogContextHelper.startPerfLogTxnMonitor(perfLogContextTrackingData);

		// This context is deleted during out bound flow or in handleFault
		// Note: there may be a chance that the context may not get deleted
		// if there is some exception and the handleResponse is not called
		// However that is ok, because if a new context is created on the same
		// thread and if it finds a zombie context, it will be deleted next time
		// around
		// 

		PerfLogContextHelper.pushInfoContext(
				PerfLogContextConstants.CALLING_JVM_HOST_ID, perfLogContextTrackingData.getCallingJvmHostId());
		PerfLogContextHelper.pushInfoContext(
				PerfLogContextConstants.CALLING_JVM_CLONE_ID, perfLogContextTrackingData.getCallingJvmCloneId());
		// TODO get URL
		QName propQName = (QName) soapMsgContext
				.get(MessageContext.WSDL_INTERFACE);
		if (propQName != null)
			PerfLogContextHelper.pushInfoContext(
					ContextHandlerConstants.PROPERTY_NAME_JAXWS_WSDL_INTERFACE,
					propQName.toString());
		String propValue = getOperationName(soapMsgContext);
		if (propValue != null) {
			PerfLogContextHelper.pushInfoContext(
					ContextHandlerConstants.PROPERTY_NAME_JAXWS_WSDL_OPERATION,
					propValue);
			PerfLogContextHelper.addToTxnList(propValue);
		}
		propQName = (QName) soapMsgContext.get(MessageContext.WSDL_SERVICE);
		if (propQName != null)
			PerfLogContextHelper.pushInfoContext(
					ContextHandlerConstants.PROPERTY_NAME_JAXWS_WSDL_SERVICE,
					propQName.toString());

		propValue = (String) soapMsgContext
				.get(ContextHandlerConstants.PROPERTY_NAME_JAXWS_SERVER_ENDPOINT);
		if (propValue != null) {
			PerfLogContextHelper.pushInfoContext(
					ContextHandlerConstants.PROPERTY_NAME_JAXWS_SERVER_ENDPOINT,
					propValue);
			PerfLogContextHelper.addToTxnList(propValue);
		}

		PerfLogContextHelper.setUserId(perfLogContextTrackingData.getUserId());
		PerfLogContextHelper.setRequestSessionId(perfLogContextTrackingData.getSessionId()); // calling JVM session
															// id if set
		addMessagePropertiesToPerfLogContext(soapMsgContext);

		// add any current filter specific info here.. e.g. from the message
		// data.

		return true;
	}

	private void logPerfMetrics(MessageContext msgContext, long elapsedTime, PerfLogContext perfLogContext, Throwable t, String detailedFaultMessage) {
	
		logger.debug("logPerfMetrics: elapsedTime = " + elapsedTime);
		if (perfLogContext == null) {
			logger.warn("perfLogContext null");
			return;
		}
		PerfLogData pData = new PerfLogData(perfLogContext);
		pData.setTransactionType(PerfLogTxnType.JAX_WS_SERVER_REQUEST);
		pData.setTransactionName(
				(String)msgContext.get(ContextHandlerConstants.PROPERTY_NAME_JAXWS_SERVER_ENDPOINT));
		pData.setSubTransactionName(getOperationName((SOAPMessageContext) msgContext));
		pData.setTransactionTime(elapsedTime);
		pData.setThrowable(t);
		pData.setMessage(detailedFaultMessage);
		pData.setTransactionTime(elapsedTime);
		pData.setGuid(perfLogContext.getGuid());
		pData.setSessionId(perfLogContext.getRequestSessionId());
		pData.setThreadName(perfLogContext.getThreadName());
		pData.setServerName(perfLogContext.getHostId());
		pData.setServerIp(perfLogContext.getHostIp());
		pData.setPerfLogContext(perfLogContext);											
		pData.setCloneName(perfLogContext.getJvmCloneId());
		// set transaction date i.e. start time to the time this filter was created
		pData.setTransactionDate(new Date(perfLogContext.getTxnFilterCreationTime(perfLogContext.getTxnFilterDepth())));
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
		------------------------------------------------------------------------------------------*/
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
		if (propValue != null)
			PerfLogContextHelper.addToRequestDataContext(
					ContextHandlerConstants.PROPERTY_NAME_JAXWS_WSDL_OPERATION,
					propValue);
		propQName = (QName) context.get(MessageContext.WSDL_SERVICE);
		if (propQName != null)
			PerfLogContextHelper.addToRequestDataContext(
					ContextHandlerConstants.PROPERTY_NAME_JAXWS_WSDL_SERVICE,
					propQName.toString());
		
		propValue = (String)context.get(ContextHandlerConstants.PROPERTY_NAME_JAXWS_SERVER_ENDPOINT);
		if(propValue != null)
			PerfLogContextHelper.addToRequestDataContext(
				ContextHandlerConstants.PROPERTY_NAME_JAXWS_SERVER_ENDPOINT, propValue);
		
		Object value = context.get(ContextHandlerConstants.PROPERTY_NAME_JAXWS_WSDL_DESCRIPTION);
		if(value != null)
			PerfLogContextHelper.addToRequestDataContext(
				ContextHandlerConstants.PROPERTY_NAME_JAXWS_WSDL_DESCRIPTION, value.toString());
				
		
	}

	/* (non-Javadoc)
	 * @see javax.xml.ws.handler.Handler#handleFault(javax.xml.ws.handler.MessageContext)
	 */
	@Override
	public boolean handleFault(SOAPMessageContext msgContext) {
		String faultMessage=null;
		try {
			faultMessage = getSOAPMessageString(msgContext);
			if (logger.getDebugEnabled()) {
				logger.debug("handleFault: " + getSOAPMessageString(msgContext));
			}
			if (PerfLogContextHelper.getCurrentThreadPerfLogContextObject() != null) {
				if(LoggerProperties.getInstance().isPerfLogWSCacheSOAPMessage()) {
					PerfLogContextHelper.addToDebugContext(this.getClass().getName()
						+ ":handleFault()", getSOAPMessageString(msgContext));
				}
			}
		} finally {
			PerfLogContext perfLogContext = PerfLogContextHelper.getCurrentThreadPerfLogContextObject();
			long elapsedTime = getElapsedTime(msgContext);
			if(LoggerProperties.getInstance().isPerfLogWSEnabled() &&
					(elapsedTime >= LoggerProperties.getInstance().getPerfLogWSThreshold()))
				logPerfMetrics(msgContext, elapsedTime, perfLogContext, 
					new Exception("JaxWSLogContextServerHandler:handleFault"),
						faultMessage);
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
		if(direction) {
			
		}
		else {
		}
		
	}

	

}
