/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/filter/ws/JaxRpcLogContextHandler.java 
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.rpc.handler.GenericHandler;
import javax.xml.rpc.handler.HandlerInfo;
import javax.xml.rpc.handler.HandlerRegistry;
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.perf.log.context.PerfLogContextHelper;
import org.perf.log.context.PerfLogContextTrackingData;
import org.perf.log.logger.Logger;
import org.perf.log.logger.LoggerFactory;
import org.perf.log.utils.PrettyXML;


public class JaxRpcLogContextHandler extends GenericHandler {

	private final static Logger logger = LoggerFactory.getLogger("JaxRpcLogContextHandler");

	protected HandlerInfo info;
	
	@SuppressWarnings("unchecked")
	/*
	 * This is a utility method to register a handler programmatically
	 */
	public static void registerHandler(HandlerRegistry handlerRegistry, QName serviceName, @SuppressWarnings("rawtypes") Class handlerClass) {
		List<HandlerInfo> handlerChain = handlerRegistry.getHandlerChain(serviceName);
		HandlerInfo handlerInfo = new HandlerInfo();
		handlerInfo.setHandlerClass(handlerClass);
		handlerChain.add(handlerInfo);
	}
	

	protected void addMessagePropertiesToPerfLogContext(MessageContext context) {
		@SuppressWarnings("unchecked")
		Iterator<String> propertyNames = context.getPropertyNames();
		for (Iterator<String> iterator = propertyNames; iterator.hasNext();) {
			String name = iterator.next();

			if (context.getProperty(name) instanceof String) {
				
				// get the endpoint and inbound URL property only for now..
				if (name.equals(ContextHandlerConstants.PROPERTY_NAME_JAXRPC_INBOUND_URL)
						||
					name.equals(ContextHandlerConstants.PROPERTY_NAME_JAXRPC_ENDPOINT)) {
					PerfLogContextHelper.addToRequestDataContext(name,
							(String) context.getProperty(name));
					
				}
			}

		}
	}

	@Override
	public QName[] getHeaders() {

		logger.debug("JaxRpcLogContextHandler:getHeaders()");
		return ContextHandlerConstants.PERF_LOGCONTEXT_TRACKING_DATA_HEADERS;
	}

	@Override
	public void destroy() {
		logger.debug("JaxRpcLogContextHandler:destroy()");
		super.destroy();
	}

	@Override
	public void init(HandlerInfo config) {
		logger.debug("JaxRpcLogContextHandler:init");
		info = config;
		super.init(config);
	}
	
	protected String getSOAPMessageString(MessageContext context) {
		SOAPMessage msg = ((SOAPMessageContext) context).getMessage();

		if (msg != null) {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			try {
				msg.writeTo(bout);
			} catch (SOAPException e) {
				logger.debug(e.getMessage());
				return null;
			} catch (IOException e) {
				logger.debug(e.getMessage());
				return null;
			}
			return PrettyXML.format(bout.toString());
		
		}
		else 
			return null;
	}

   protected void addSOAPMessageStringToPerfLogContextRequestData(String messageType, String soapMessageString) {
			PerfLogContextHelper.addToRequestDataContext(messageType, soapMessageString);
   }

// return value - true if successfully added log context to SOAP header
// return value - false if there was an error
   protected boolean addLogContextToSOAPHeader(MessageContext context,
		PerfLogContextTrackingData perfLogContextTrackingData) {

	logger.debug("addContextToSOAPHeader");

	SOAPMessageContext soapContext = (SOAPMessageContext) context;
	if (perfLogContextTrackingData == null)
		return false;

	SOAPMessage message = soapContext.getMessage();

	// ------------ Set soap headers with current GUID and context

	try {
		SOAPPart part = message.getSOAPPart();
		SOAPEnvelope envelop = part.getEnvelope();
		SOAPHeader header = message.getSOAPHeader();
		if (header == null) {
			header = envelop.addHeader();

		}
		SOAPHeaderElement headerElement = header
				.addHeaderElement(ContextHandlerConstants.PERF_LOGCONTEXT_TRACKING_DATA_HEADER);
		headerElement.addTextNode(perfLogContextTrackingData.toJSON());
		message.saveChanges();
	} catch (SOAPException e) {
		logger.error(e.getMessage(), e);
		return false; // failure
	}

	return true; // success

}
   protected long getElapsedTime(MessageContext context) {
	
	   long endTime = System.currentTimeMillis();
	   Long startTimeLong = (Long) context.getProperty(ContextHandlerConstants.PROPERTY_NAME_startTime);
	   long elapsedTime = 0;
	   if (startTimeLong != null) {
		   elapsedTime = endTime - startTimeLong.longValue();
	   }
	   else {
		   logger.warn("Could not retrieve startTime property from message context.");
	   }
	   return elapsedTime;
   }

	protected PerfLogContextTrackingData retrieveLogContextFromSOAPHeader(
			MessageContext context) {
		PerfLogContextTrackingData perfLogContextTrackingData = new PerfLogContextTrackingData();

		SOAPMessageContext soapContext = (SOAPMessageContext) context;
		SOAPMessage message = soapContext.getMessage();
		SOAPHeader header = null;
		try {
			header = message.getSOAPHeader();
		} catch (SOAPException e) {
			logger.error(e.getMessage(), e);
		}

		if (header != null) {
			Iterator iter = header.examineAllHeaderElements();
			while (iter.hasNext()) {
				SOAPElement element = (SOAPElement) iter.next();
				if (element
						.getElementName()
						.getLocalName()
						.equals(ContextHandlerConstants.PERF_LOGCONTEXT_TRACKING_DATA_ELEMENT)) {
					perfLogContextTrackingData
							.initFromJSONData(element.getValue());
					logger.debug("Found "
							+ ContextHandlerConstants.PERF_LOGCONTEXT_TRACKING_DATA_ELEMENT
							+ " =" + element.getValue());
					element.detachNode();
				}

			}
		} else
			logger.debug("retrieveLogContextFromSOAPHeader: SOAP header = null");

		// if there is an error in getting to SOAP header, this object will
		// contain all attributes set to null
		return perfLogContextTrackingData;

}
	

}
