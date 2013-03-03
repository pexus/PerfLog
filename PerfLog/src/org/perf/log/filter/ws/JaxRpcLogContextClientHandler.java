/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/filter/ws/JaxRpcLogContextClientHandler.java 
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

import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.handler.MessageContext;

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


public class JaxRpcLogContextClientHandler extends JaxRpcLogContextHandler 
{
	private final static Logger logger = LoggerFactory.getLogger("JaxRpcLogContextClientHandler");
	private final static PerfLogger plogger = PerfLoggerFactory.getLogger();
	@Override
	public boolean handleRequest(MessageContext context) 
	{

		Throwable throwable = null;
		boolean handleReqRetValue = true;
		boolean txnMonitorStarted = false;
		try {

			logger.debug("handleRequest: createPerfLogContext");

			// create a guid context, this is required to get some
			// contextual information from this JVM before calling the
			// service in the other JVM..
			// The context will be delete in handleResponse or handleFault methods
			// Note: starPerfLogTxnMonitor() will only create a context if no context exist
			// If a context already exists it will increase the reference count by incrementing
			// the filter count.
			
			PerfLogContextHelper.startPerfLogTxnMonitor();
			txnMonitorStarted = true;
			addMessagePropertiesToPerfLogContext(context);
			PerfLogContextHelper.addToTxnList((String)context.getProperty(ContextHandlerConstants.PROPERTY_NAME_JAXRPC_ENDPOINT));
			PerfLogContextHelper.addToTxnList((String)context.getProperty(ContextHandlerConstants.PROPERTY_NAME_JAXRPC_SOAP_ACTION));
			if(LoggerProperties.getInstance().isPerfLogWSCacheSOAPMessage())
				addSOAPMessageStringToPerfLogContextRequestData(
					"JaxRpcClientSOAPRequest", getSOAPMessageString(context));
			PerfLogContext thisThreadPerfLogContext = PerfLogContextHelper.getCurrentThreadPerfLogContextObject();

			PerfLogContextTrackingData perfLogContextTrackingData = new PerfLogContextTrackingData(
					thisThreadPerfLogContext.getGuid(), 
					"" + thisThreadPerfLogContext.getContextCreationTime(),
					thisThreadPerfLogContext.getHostId(),
					thisThreadPerfLogContext.getHostIp(),
					thisThreadPerfLogContext.getJvmCloneId(), 
					""	+ thisThreadPerfLogContext.getJvmDepth(),
					thisThreadPerfLogContext.getRequestSessionId(), 
					thisThreadPerfLogContext.getUserId());
			
			
			boolean retValue = addLogContextToSOAPHeader(context, perfLogContextTrackingData);
			if (retValue == false) {
				logger.error(
						"JaxRpcLogContextClientHandler.handleRequest():Unable to add SOAP Headear to outgoing web service request");
			}
			logger.debug("JaxRpcClientHandler.handleRequest():guid="+ perfLogContextTrackingData.getGuid() + 
							" requestSessionId=" + perfLogContextTrackingData.getSessionId() + 
							" callingHostId=" + perfLogContextTrackingData.getCallingJvmHostId() +
							" callingJVMCloneId=" + perfLogContextTrackingData.getCallingJvmCloneId() +
							" callingJVMDepth="	+ perfLogContextTrackingData.getCallingJvmDepthStr());
			// track elapsed time for this web service...
			context.setProperty(
					ContextHandlerConstants.PROPERTY_NAME_startTime, new Long(System.currentTimeMillis()));
			PerfLogContextHelper.compensateForOutboundJvmCallExceptionIfAny();
			PerfLogContextHelper.setAwatingReturnFromOutboundJvmCall(true);
			handleReqRetValue =  super.handleRequest(context);
			
			return handleReqRetValue;
		}

		catch (JAXRPCException e) {
			logger.error(e.getMessage(), e);
			throw e;

		} catch (Throwable t) {
			throwable = t;
			logger.error(t.getMessage());			
			throw new JAXRPCException(t.getMessage());
		}
		finally {
			if(throwable != null  || handleReqRetValue == false) {
				// if ret_val == false, the request is never made and hence we need to delete the context.
				PerfLogContext guidContext = PerfLogContextHelper.getCurrentThreadPerfLogContextObject();
				long elapsedTime = getElapsedTime(context);
				String exceptionMessage = null;
				if(throwable != null) {
						exceptionMessage = throwable.getMessage();
				}
				else {
					// handleRequest returned false.. handle response will not be called
					 throwable = new Throwable("handleRequest returned false");
					 exceptionMessage = throwable.getMessage();
					
				}
				if(LoggerProperties.getInstance().isPerfLogWSEnabled() &&
						(elapsedTime >= LoggerProperties.getInstance().getPerfLogWSThreshold()))
					logPerfMetrics(context, elapsedTime, guidContext, throwable, exceptionMessage);	
				if(txnMonitorStarted) {
					logger.debug("handleRequest: endPerfLogTxnMonitor");
					PerfLogContextHelper.setAwatingReturnFromOutboundJvmCall(false);
					PerfLogContextHelper.endPerfLogTxnMonitor();
				}
			}
		}
	}

	@Override
	public boolean handleResponse(MessageContext msgContext) {
	
		boolean retVal;
		try {
			logger.debug("handleResponse");
			if(LoggerProperties.getInstance().isPerfLogWSCacheSOAPMessage())
				addSOAPMessageStringToPerfLogContextRequestData(
					"JaxRpcClientSOAPResponse",	getSOAPMessageString(msgContext));
			retVal = super.handleResponse(msgContext);
		} finally {
			PerfLogContext perfLogContext = PerfLogContextHelper.getCurrentThreadPerfLogContextObject();
			long elapsedTime = getElapsedTime(msgContext);
			if(LoggerProperties.getInstance().isPerfLogWSEnabled() &&
					(elapsedTime >= LoggerProperties.getInstance().getPerfLogWSThreshold()))			
				logPerfMetrics(msgContext, elapsedTime, perfLogContext, null, null);
			PerfLogContextHelper.setAwatingReturnFromOutboundJvmCall(false);
			PerfLogContextHelper.endPerfLogTxnMonitor();
		}
		
	
		return retVal;
	}



	private void logPerfMetrics(MessageContext context, long elapsedTime, PerfLogContext perfLogContext, Throwable t, String detailedFaultMessage) {
	    
		logger.debug("logPerfMetrics: elapsedTime = " + elapsedTime);
		if(perfLogContext == null) return;
		    
		PerfLogData pData = new PerfLogData(perfLogContext);
		pData.setTransactionType(PerfLogTxnType.JAX_RPC_CLIENT_REQUEST);
		pData.setTransactionName((String)context.getProperty(ContextHandlerConstants.PROPERTY_NAME_JAXRPC_ENDPOINT));
		pData.setSubTransactionName((String)context.getProperty(ContextHandlerConstants.PROPERTY_NAME_JAXRPC_SOAP_ACTION));
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

	@Override
	public boolean handleFault(MessageContext msgContext) {
		String faultMessage=null;
		try {
			faultMessage = getSOAPMessageString(msgContext);
			if (logger.getDebugEnabled())
				logger.debug("JaxRpcLogContextHandler:handleFault: "
						+ faultMessage);
			return super.handleFault(msgContext);
		} finally {
			PerfLogContext perfLogContext = PerfLogContextHelper.getCurrentThreadPerfLogContextObject();
			long elapsedTime = getElapsedTime(msgContext);
			if(LoggerProperties.getInstance().isPerfLogWSEnabled() &&
					(elapsedTime >= LoggerProperties.getInstance().getPerfLogWSThreshold()))
			logPerfMetrics(msgContext, elapsedTime, perfLogContext,
					new Exception("JaxRpcLogContextClientHandler:handleFault"), faultMessage);
			logger.debug("handleResponse: endPerfLogTxnMonitor");
			PerfLogContextHelper.setAwatingReturnFromOutboundJvmCall(false);
			PerfLogContextHelper.endPerfLogTxnMonitor();
		}
	}

}
