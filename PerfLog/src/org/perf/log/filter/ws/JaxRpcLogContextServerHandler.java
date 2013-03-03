/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/filter/ws/JaxRpcLogContextServerHandler.java 
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
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.soap.SOAPMessage;

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



public class JaxRpcLogContextServerHandler extends JaxRpcLogContextHandler {
	private final static Logger logger = LoggerFactory.getLogger("JaxRpcLogContextServerHandler");
	private final static PerfLogger plogger = PerfLoggerFactory.getLogger();

	protected boolean createPerfLogContextForWSRequest(MessageContext context,
			SOAPMessage message,
			PerfLogContextTrackingData perfLogContextTrackingData) {

		logger.debug("handleRequest - createPerfLogContextForWSRequest");
		
		
		PerfLogContextHelper.startPerfLogTxnMonitor(perfLogContextTrackingData);
		
		// This context is deleted when handling the response in handleMessage() or
		// in handleFault() method
		// Note: there may be a chance that the context may not get deleted
		// However that is ok, because if a new context is created on the same
		// thread and if it finds a zombie context, it will be deleted next time around
		// 
		
		PerfLogContextHelper.pushInfoContext(
				PerfLogContextConstants.CALLING_JVM_HOST_ID, perfLogContextTrackingData.getCallingJvmHostId());
		PerfLogContextHelper
				.pushInfoContext(
						PerfLogContextConstants.CALLING_JVM_CLONE_ID,
						perfLogContextTrackingData.getCallingJvmCloneId());
		String tmpValueStr;
		PerfLogContextHelper
				.pushInfoContext(
						ContextHandlerConstants.PROPERTY_NAME_JAXRPC_INBOUND_URL,
						(tmpValueStr = (String) context
								.getProperty(ContextHandlerConstants.PROPERTY_NAME_JAXRPC_INBOUND_URL)));
		PerfLogContextHelper.addToTxnList(tmpValueStr);
		PerfLogContextHelper
				.pushInfoContext(
						ContextHandlerConstants.PROPERTY_NAME_JAXRPC_SOAP_ACTION,
						(tmpValueStr = (String) context
								.getProperty(ContextHandlerConstants.PROPERTY_NAME_JAXRPC_SOAP_ACTION)));
		PerfLogContextHelper.addToTxnList(tmpValueStr);
		PerfLogContextHelper.setUserId(perfLogContextTrackingData.getUserId());
		PerfLogContextHelper.setRequestSessionId(perfLogContextTrackingData.getSessionId()); // calling JVM session id if set
		addMessagePropertiesToPerfLogContext(context);

		// add any current filter specific info here.. e.g. from the message
		// data.

		return true;
	}

	@Override
	public boolean handleRequest(MessageContext context) {

		
		logger.debug(
				"JaxRpcLogContextServerHandler.handleRequest()");
		
		String soapMessageString = getSOAPMessageString(context);
		
		SOAPMessageContext soapContext = (SOAPMessageContext) context;
		SOAPMessage message = soapContext.getMessage();
		
		PerfLogContextTrackingData perfLogContextTrackingData;
		
		perfLogContextTrackingData = retrieveLogContextFromSOAPHeader(context);
		
		
		// create a thread context
		logger.debug( "JaxRpcLogContextServerHandler: guid = "+ perfLogContextTrackingData.getGuid()
				+" sessionId="+perfLogContextTrackingData.getSessionId()
				+" callingJVMHostId="+perfLogContextTrackingData.getCallingJvmHostId()
				+" callingJVMCloneId="+perfLogContextTrackingData.getCallingJvmCloneId()
				+" callingJVMDepth="+perfLogContextTrackingData.getCallingJvmDepthStr()
				+" userId="+perfLogContextTrackingData.getUserId()
				+" createTime="+perfLogContextTrackingData.getCreateTimeInMillisStr());
		createPerfLogContextForWSRequest(context, message, perfLogContextTrackingData);
		if(LoggerProperties.getInstance().isPerfLogWSCacheSOAPMessage())
			addSOAPMessageStringToPerfLogContextRequestData("JaxRpcServerSOAPRequest", soapMessageString);

		// track elapsed time for this web service...
		context.setProperty(ContextHandlerConstants.PROPERTY_NAME_startTime, new Long(System.currentTimeMillis()));
		return super.handleRequest(context);
	}

	@Override
	public boolean handleResponse(MessageContext msgContext) {
	
		boolean retVal;
		try {
			logger.debug(
					"JaxRpcLogContextServerHandler:handleResponse");
			if(LoggerProperties.getInstance().isPerfLogWSCacheSOAPMessage())
				addSOAPMessageStringToPerfLogContextRequestData(
					"JaxRpcServerSOAPResponse", 
					getSOAPMessageString(msgContext));
			
			retVal = super.handleResponse(msgContext);
			
		} finally {
			PerfLogContext perfLogContext = PerfLogContextHelper.getCurrentThreadPerfLogContextObject();
			long elapsedTime = getElapsedTime(msgContext);
			PerfLogContextHelper.compensateForOutboundJvmCallExceptionIfAny();
			if(LoggerProperties.getInstance().isPerfLogWSEnabled() &&
					(elapsedTime >= LoggerProperties.getInstance().getPerfLogWSThreshold()))	
				logPerfMetrics(msgContext, elapsedTime, perfLogContext, null, null);
			PerfLogContextHelper.endPerfLogTxnMonitor();
		}
		
	
		return retVal;
	}

	private void logPerfMetrics(MessageContext context, long elapsedTime, PerfLogContext perfLogContext, 
			Throwable t, String detailedFaultMessage) {

		logger.debug("logPerfMetrics: elapsedTime = " + elapsedTime);
		
		if (perfLogContext == null) {
			logger.warn("perfLogContext null");
			return;
		}

		PerfLogData pData = new PerfLogData(perfLogContext);
		pData.setTransactionType(PerfLogTxnType.JAX_RPC_SERVER_REQUEST);
		pData.setTransactionName(
				(String) context.getProperty(ContextHandlerConstants.PROPERTY_NAME_JAXRPC_INBOUND_URL));
		pData.setSubTransactionName(
				(String) context.getProperty(ContextHandlerConstants.PROPERTY_NAME_JAXRPC_SOAP_ACTION));
		pData.setTransactionTime(elapsedTime);
		pData.setThrowable(t);
		pData.setMessage(detailedFaultMessage);
		pData.setTransactionTime(elapsedTime);
		pData.setGuid(perfLogContext.getGuid());
		pData.setSessionId(perfLogContext.getRequestSessionId());
		pData.setThreadName(perfLogContext.getThreadName());
		pData.setServerName(perfLogContext.getHostId());
		pData.setPerfLogContext(perfLogContext);											
		pData.setServerIp(perfLogContext.getHostIp());
		pData.setCloneName(perfLogContext.getJvmCloneId());
		// set transaction date i.e. start time to the time this filter was created
		pData.setTransactionDate(new Date(perfLogContext.getTxnFilterCreationTime(perfLogContext.getTxnFilterDepth())));
		pData.setUserId(perfLogContext.getUserId());

		plogger.log(pData);
	}

	@Override
	public boolean handleFault(MessageContext msgContext) {
		String faultMessage=null;
		try {
			 faultMessage = getSOAPMessageString(msgContext);
			if (logger.getDebugEnabled())
				logger.debug("handleFault: " + faultMessage);
			msgContext.setProperty(ContextHandlerConstants.PROPERTY_NAME_FAULT,
					new Boolean(true));
			return super.handleFault(msgContext);
		} finally {
			PerfLogContext perfLogContext = PerfLogContextHelper.getCurrentThreadPerfLogContextObject();
			long elapsedTime = getElapsedTime(msgContext);
			PerfLogContextHelper.compensateForOutboundJvmCallExceptionIfAny();
			if(LoggerProperties.getInstance().isPerfLogWSEnabled() &&
					(elapsedTime >= LoggerProperties.getInstance().getPerfLogWSThreshold()))	
				logPerfMetrics(msgContext, elapsedTime, perfLogContext,
						new Exception("JaxRpcLogContextServerHandler:handleFault"),faultMessage);
			PerfLogContextHelper.endPerfLogTxnMonitor();
		}
	}

}
