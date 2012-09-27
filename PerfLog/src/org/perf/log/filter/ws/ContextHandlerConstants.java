/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/filter/ws/ContextHandlerConstants.java 
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

import javax.xml.namespace.QName;
import javax.xml.ws.handler.soap.SOAPMessageContext;


public class ContextHandlerConstants {

	// This header is inserted as SOAP headers when requests crosses JVMs
	public static final String PERF_LOGCONTEXT_TRACKING_DATA_NS_URI = "uri://org.perf.log.ContextTrackingData";
	public static final String PERF_LOGCONTEXT_TRACKING_DATA_PREFIX = "PerfLogContextTrackingData";
	public static final String PERF_LOGCONTEXT_TRACKING_DATA_ELEMENT = "PerfLogContextTrackingData";
	public static final QName PERF_LOGCONTEXT_TRACKING_DATA_HEADER = new QName(
			PERF_LOGCONTEXT_TRACKING_DATA_NS_URI,
			PERF_LOGCONTEXT_TRACKING_DATA_ELEMENT);

	public static final QName[] PERF_LOGCONTEXT_TRACKING_DATA_HEADERS = new QName[] { 
		ContextHandlerConstants.PERF_LOGCONTEXT_TRACKING_DATA_HEADER 
	};
	 
	 
	 public static final String PROPERTY_NAME_startTime = "startTime";
	 public static final String PROPERTY_NAME_contextCreated = "contextCreated";
	 public static final String PROPERTY_NAME_FAULT = "track.fault.flag";
	 
	 // message property names
	 public static final String PROPERTY_NAME_JAXRPC_INBOUND_URL = "inbound.url";
	 public static final String PROPERTY_NAME_JAXRPC_SOAP_ACTION = "javax.xml.rpc.soap.http.soapaction.uri";
	 
	 // for out going web service call
	 public static final String PROPERTY_NAME_JAXRPC_ENDPOINT = "javax.xml.rpc.service.endpoint.address";
	 
	 // jax-ws constants
	 
	 public static final String PROPERTY_NAME_JAXWS_MESSAGE_DIRECTION = "jaxws.message.direction";
	 public static final String PROPERTY_VALUE_JAXWS_MESSAGE_INBOUND = "jaxws.message.inbound";
	 public static final String PROPERTY_VALUE_JAXWS_MESSAGE_OUTBOUND = "jaxws.message.outbound";
	 
	 
	 public static final String PROPERTY_NAME_JAXWS_PATHINFO = SOAPMessageContext.PATH_INFO;
	 public static final String PROPERTY_NAME_JAXWS_QUERY_STRING = SOAPMessageContext.QUERY_STRING;
	 public static final String PROPERTY_NAME_JAXWS_WSDL_INTERFACE = SOAPMessageContext.WSDL_INTERFACE;
	 public static final String PROPERTY_NAME_JAXWS_WSDL_OPERATION = "javax.xml.ws.wsdl.operation"; //SOAPMessageContext.WSDL_OPERATION;
	 public static final String PROPERTY_NAME_JAXWS_WSDL_SERVICE = "javax.xml.ws.wsdl.service"; // SOAPMessageContext.WSDL_SERVICE;
	 public static final String PROPERTY_NAME_JAXWS_SERVER_ENDPOINT = "TransportInURL"; // this can also be retrieved from HttpServletRequest query URI
	 public static final String PROPERTY_NAME_JAXWS_CLIENT_ENDPOINT = "javax.xml.ws.service.endpoint.address";
	 public static final String PROPERTY_NAME_JAXWS_MESSAGE_VALUE = "jaxws.message.accessor"; // retrieves the soap message since WAS 7.0 FP15
	 public static final String PROPERTY_NAME_JAXWS_WSDL_DESCRIPTION = "javax.xml.ws.wsdl.description";

}
