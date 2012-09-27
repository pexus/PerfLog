/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/context/PerfLogContextConstants.java 
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
package org.perf.log.context;

/**
 *  
 * @author Pradeep Nambiar 2/10/2012
 */


public class PerfLogContextConstants {
	
	public static final String GUID = "guid";
	public static final String SID= "sid";
	public static final String THREAD_ID = "tid";
	public static final String USER_ID = "uid";
	public static final String HOST_ID = "host";
	public static final String HOST_IP = "ip";
	public static final String JVM_CLONE_ID = "clone";
	public static final String JVM_DEPTH = "jvmDepth";
	public static final String FILTER_DEPTH = "filDepth";
	public static final String CALLING_JVM_CONTEXT = "cJvmCtx";
	public static final String CALLING_JVM_HOST_ID = "cJvmHost";
	public static final String CALLING_JVM_CLONE_ID = "cJvmClone";
	public static final String CALLING_JVM_DEPTH = "cJvmDepth";
	
	public static final String SERVICE_NAME = "service";
	public static final String SQL = "sql";
	public static final String SQL_TIMING = "sqltiming";
	
	public static final String DEBUG_DATA = "debug";
	public static final String REQUEST_DATA = "request";
	
	
	
	
	// application can use custom context key names
	
	
}
