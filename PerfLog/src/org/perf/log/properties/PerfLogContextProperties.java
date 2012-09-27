/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/properties/PerfLogContextProperties.java 
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
package org.perf.log.properties;
import java.io.*;
import java.util.Properties;

public class PerfLogContextProperties {
		
		private static final String PERFLOG_CONTEXT_DYNAMIC_FORCE_DUMP_OF_DEBUG_CONTEXT_ON_DELETE = "dynamic.perflog.context.forceDumpOfDebugContextOnDelete";
		private static final String PERFLOG_CONTEXT_DYNAMIC_RESPONSE_TIME_THRESHOLD_IN_MILLIS = "dynamic.perflog.context.responseTimeThresholdInMillis";
		private static final String PERFLOG_CONTEXT_STATIC_MAX_REQUEST_DATA_CONTEXT_SIZE_IN_BYTES = "static.perflog.context.maxRequestDataContextSizeInBytes";
		private static final String PERFLOG_CONTEXT_STATIC_MAX_DEBUG_CONTEXT_SIZE_IN_BYTES = "static.perflog.context.maxDebugContextSizeInBytes";
		private static TunableProperties tunableProperties = null;
		private static Object syncObject = new Object();
		
		
		// don't use loggers here since they may have not been initialized yet
		static private PerfLogContextProperties _instance = null;
		
		private long maxDebugContextSizeInBytes = 10*1024; // in bytes
		private long maxRequestDataContextSizeInBytes = 10*1024; // in bytes
		private long responseTimeThresholdInMillis = 5000; // in milliseconds
		private boolean forceDumpOfDebugContextOnDelete = false;
		
		
	
		// Properties are read from perfLog.properties
		// They can be overridden from definitions in name space string bindings at
		// cell/persistent/PerfLog/<property name>
	    protected PerfLogContextProperties(){
	    String errorMsg = "Error Loading perfLog.properties or perfLogDefault.properties, using defaults.";
	    try{
	    	
	    	InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("perfLog.properties");	
	        if(in == null) {
	        	System.out.println("Error loading perfLog.properties, attempting to load perfLogDefault.properties now.");
	        	in = this.getClass().getClassLoader().getResourceAsStream("perfLogDefault.properties");
	        }
	        String propVal;
	        Properties props = new Properties();
	        if(in!=null) {
	        	props.load(in);
	        	propVal  = props.getProperty(PERFLOG_CONTEXT_STATIC_MAX_DEBUG_CONTEXT_SIZE_IN_BYTES);
	        	if(propVal!=null)
	        			maxDebugContextSizeInBytes = new Long(propVal).longValue();
	        	propVal  = props.getProperty(PERFLOG_CONTEXT_STATIC_MAX_REQUEST_DATA_CONTEXT_SIZE_IN_BYTES);
	        	if(propVal!=null)
	        			maxRequestDataContextSizeInBytes = new Long(propVal).longValue();
	        	propVal = props.getProperty(PERFLOG_CONTEXT_DYNAMIC_RESPONSE_TIME_THRESHOLD_IN_MILLIS);
	        	if(propVal!=null)
	        			responseTimeThresholdInMillis = new Long(propVal).longValue();
	        	
	        	propVal = props.getProperty(PERFLOG_CONTEXT_DYNAMIC_FORCE_DUMP_OF_DEBUG_CONTEXT_ON_DELETE);
	        	if(propVal!=null)
	        		forceDumpOfDebugContextOnDelete = new Boolean(propVal).booleanValue();
	        	printCurrentPropertyValues();
	        }
	        else {
	        	System.out.println(errorMsg);
	        }
	       } 
	    catch(Exception e){
	    	System.out.println(errorMsg + e.getMessage());
	       }	 
	    }
		
	    void printCurrentPropertyValues() 
	    {
	    	System.out.println("---- org.perf.log.properties.PerfLogContextProperties ----");
	    	System.out.println(PERFLOG_CONTEXT_STATIC_MAX_DEBUG_CONTEXT_SIZE_IN_BYTES + "=" + maxDebugContextSizeInBytes);
	    	System.out.println(PERFLOG_CONTEXT_STATIC_MAX_REQUEST_DATA_CONTEXT_SIZE_IN_BYTES + "=" +maxRequestDataContextSizeInBytes);
	    	System.out.println(PERFLOG_CONTEXT_DYNAMIC_RESPONSE_TIME_THRESHOLD_IN_MILLIS + "=" +responseTimeThresholdInMillis);
	    	System.out.println(PERFLOG_CONTEXT_DYNAMIC_FORCE_DUMP_OF_DEBUG_CONTEXT_ON_DELETE + "=" +forceDumpOfDebugContextOnDelete);
	    	System.out.println("-------------------------------------------------------------------");
	    	
	    }
	    static public PerfLogContextProperties instance(){
	        if (_instance == null) {
	            synchronized (syncObject) {
	            	if(_instance == null)
	            	_instance = new PerfLogContextProperties();
	            	tunableProperties = LoggerProperties.getInstance().getTunableProperties();
				}
	        }
	        return _instance;
	    }

		/**
		 * @return the maxDebugContextSizeInBytes
		 */
		public long getMaxDebugContextSizeInBytes() {
			// Check if this property is defined in Name Space binding
			String propertyValue = tunableProperties.getStaticProperty(PERFLOG_CONTEXT_STATIC_MAX_DEBUG_CONTEXT_SIZE_IN_BYTES);
			if(propertyValue != null) {
				return new Long(propertyValue).longValue();
			}
			else // return value initialized in this class
			
				return maxDebugContextSizeInBytes;
		}

		/**
		 * @return the maxRequestDataContextSizeInBytes
		 */
		public long getMaxRequestDataContextSizeInBytes() {
			String propertyValue = tunableProperties.getStaticProperty(PERFLOG_CONTEXT_STATIC_MAX_REQUEST_DATA_CONTEXT_SIZE_IN_BYTES);
			if(propertyValue != null) {
				return new Long(propertyValue).longValue();
			}
			else // return value initialized in this class
				return maxRequestDataContextSizeInBytes;
		}

		/**
		 * @return the responseTimeThresholdInMillis
		 */
		public long getResponseTimeThresholdInMillis() {
			String propertyValue = tunableProperties.getDynamicProperty(PERFLOG_CONTEXT_DYNAMIC_RESPONSE_TIME_THRESHOLD_IN_MILLIS);
			if(propertyValue != null) {
				return new Long(propertyValue).longValue();
			}
			else // return value initialized in this class
				return responseTimeThresholdInMillis;
		}

		/**
		 * @return the forceDumpOfDebugContextOnDelete
		 */
		public boolean isForceDumpOfDebugContextOnDelete() {
			String propertyValue = tunableProperties.getDynamicProperty(PERFLOG_CONTEXT_DYNAMIC_FORCE_DUMP_OF_DEBUG_CONTEXT_ON_DELETE);
			if(propertyValue != null) {
				return new Boolean(propertyValue).booleanValue();
			}
			else // return value initialized in this class
			
			return forceDumpOfDebugContextOnDelete;
		}
	
}
