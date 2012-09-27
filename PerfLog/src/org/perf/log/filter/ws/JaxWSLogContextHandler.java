/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/filter/ws/JaxWSLogContextHandler.java 
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.apache.axis2.jaxws.description.OperationDescription;
import org.apache.axis2.jaxws.handler.MEPContext;
import org.perf.log.context.PerfLogContextHelper;
import org.perf.log.context.PerfLogContextTrackingData;
import org.perf.log.logger.Logger;
import org.perf.log.logger.LoggerFactory;
import org.perf.log.utils.PrettyXML;




public abstract class JaxWSLogContextHandler implements  SOAPHandler<SOAPMessageContext> {

	private final static Logger logger = LoggerFactory.getLogger("JaxWSLogContextHandler");
	
	// Getting operation name is not easy in JAX-WS.. it doesn't work always
	//
	protected String getOperationName(SOAPMessageContext msgContext) {
		// get the operation name from the message context, if not available from the context directly
		QName propQName;
		String retValue = null;
		propQName = (QName) msgContext.get(ContextHandlerConstants.PROPERTY_NAME_JAXWS_WSDL_OPERATION);
		if (propQName != null) {
			logger.debug("1 - operationName = "+propQName.toString());
			return propQName.toString();
		}
		
				
		// else look in MEPContext
		MEPContext mepContext = (MEPContext)msgContext.get("org.apache.axis2.jaxws.handler.MEPContext");
		if(mepContext!=null) {
			// get the request Context 
			org.apache.axis2.jaxws.core.MessageContext reqMC = mepContext.getRequestMessageContext();
			if(reqMC!=null) {
					propQName = reqMC.getOperationName();
					if(propQName != null) {
						logger.debug("2 - operationName = "+propQName.toString());
						return propQName.toString();
					}
			}
			
			// if still null check in Operation Description
			OperationDescription operDesc = reqMC.getOperationDescription();
			if(operDesc != null && (retValue = operDesc.getOperationName()) != null) {
				logger.debug("3 - operationName = "+retValue);
				return retValue;
			}
			
		}
		// if not found return null
		logger.debug("4 - operationName = " + retValue);
		return retValue;
	}
	
	@Override
	public Set<QName> getHeaders() {

		logger.debug("JaxRpcLogContextHandler:getHeaders()");
		Set<QName> qNameSet = new HashSet<QName>(Arrays.asList(ContextHandlerConstants.PERF_LOGCONTEXT_TRACKING_DATA_HEADERS));
		return qNameSet;
	}

	
	
	protected String getSOAPMessageString(SOAPMessageContext msgContextt) {
		SOAPMessage msg = msgContextt.getMessage();
		
			
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

/* (non-Javadoc)
 * @see javax.xml.ws.handler.Handler#close(javax.xml.ws.handler.MessageContext)
 */
@Override
public void close(MessageContext msgContext) {
	boolean direction = ((Boolean) msgContext
            .get(MessageContext.MESSAGE_OUTBOUND_PROPERTY))
            .booleanValue();
	logger.debug("close(): direction = " + direction);
	
//	if(direction) {
//		// res
//	}
//	else {
//	}
	
}

// return value - true if successfully added log context to SOAP header
// return value - false if there was an error
   protected boolean addLogContextToSOAPHeader(MessageContext msgContext,
		PerfLogContextTrackingData perfLogContextTrackingData) {

	logger.debug("addContextToSOAPHeader");

	SOAPMessageContext soapContext = (SOAPMessageContext) msgContext;
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
		// attach json string containing the guid context tracking data
		// this will be parsed and de-serilized by the server side handler
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

protected PerfLogContextTrackingData retrieveLogContextFromSOAPHeader(
		MessageContext msgContext) {
	PerfLogContextTrackingData perfLogContextTrackingData = new PerfLogContextTrackingData();

	SOAPMessageContext soapContext = (SOAPMessageContext) msgContext;
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
	} else {
		// it is ok to have SOAP header as null, esp. if no header was inserted by the caller
		logger.debug("retrieveLogContextFromSOAPHeader: SOAP header = null");
	}

	// if there is an error in getting to SOAP header, this object will
	// contain all attributes set to null
	return perfLogContextTrackingData;

}

protected long getElapsedTime(MessageContext msgContext) {
	
	long endTime = System.currentTimeMillis();
	   Long startTimeLong = (Long) msgContext.get(ContextHandlerConstants.PROPERTY_NAME_startTime);
	   long elapsedTime = 0;
	   if (startTimeLong != null) {
		   elapsedTime = endTime - startTimeLong.longValue();
	   }
	   else {
		   logger.warn("Could not retrieve startTime property from message context.");
	   }
	   return elapsedTime;
}

/* (non-Javadoc)
 * @see javax.xml.ws.handler.Handler#handleMessage(javax.xml.ws.handler.MessageContext)
 */

	

}
