/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/filter/types/PerfLogTxnType.java 
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
package org.perf.log.txn.types;

public final class PerfLogTxnType {
	// The following are special 
	public final static String START_PERF_LOG_TRANSACTION = "startPerfLogTxn";
	public final static String END_PERF_LOG_TRANSACTION = "endPerfLogTxn";
	
	public final static String PORTLET_ACTION_REQUEST = "portletAction";
	public final static String PORTLET_RENDER_REQUEST = "portletRender";
	public final static  String PORTLET_RESOURCE_REQUEST = "portletResource";
	public final static  String PORTLET_EVENT_REQUEST = "portletEvent";
	
	public final static  String SERVLET_REQUEST = "servlet";
	public final static  String STRUTS_ACTION_REQUEST = "struts";
	
	public final static String JAX_RPC_CLIENT_REQUEST = "jaxRpcClient";
	public final static String JAX_RPC_SERVER_REQUEST = "jaxRpcServer";
	
	public final static String JAX_WS_CLIENT_REQEUST = "jaxWsClient";
	public final static String JAX_WS_SERVER_REQUEST = "jaxWsServer";
	
	public final static String SQL_QUERY = "sqlQuery";
}
