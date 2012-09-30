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
import java.util.Properties;
import org.perf.log.utils.PropertyFileLoader;

public class RuntimeEnvProperties {
		
		private static final String RUNTIME_ENV_CONTAINER_TYPE = "runtime.env.containerType";
		private static final String RUNTIME_ENV_JVM_CLONE_GETTER_IMPL_CLASS = "runtime.env.JvmCloneGetter.Impl";
		private static RuntimeEnvProperties _instance = null;
		private static Object syncObject = new Object();
		private String containerType = null;
		private String jvmCloneGetterImplClass = null;
			
	   private RuntimeEnvProperties(){
	    
	    try{
	    	// Check if there is runtimeEnv.properties file available in the environment
	    	// If not found load the runtimeEnvDefault.properties which is packaged in PerfLog.jar
	    	ClassLoader ctxClassLoader = Thread.currentThread().getContextClassLoader();
			Properties props = PropertyFileLoader.load(
					"runtimeEnv.properties", 
					"runtimeEnvDefault.properties",
					ctxClassLoader,
					this.getClass().getClassLoader(),
					RuntimeEnvProperties.class.getName());
			if (props != null) {
	    	
	        	containerType  = props.getProperty(RUNTIME_ENV_CONTAINER_TYPE);
	        	jvmCloneGetterImplClass =  props.getProperty(RUNTIME_ENV_JVM_CLONE_GETTER_IMPL_CLASS);
	        	
	        }
	        else {
	        	System.out.println("Error loading runtimeEnv.properties and runtimeEnvDefault.properties");
	        }
	       } 
	    catch(Exception e){
	    	System.out.println("Error loading runtimeEnv.properties" + e.getMessage());
	       }	 
	    }
		
	    void printCurrentPropertyValues() 
	    {
	    	System.out.println("---- org.perf.log.properties.RuntimeEnvProperties ----");
	    	System.out.println(RUNTIME_ENV_CONTAINER_TYPE + "=" + containerType);
	    	System.out.println(RUNTIME_ENV_JVM_CLONE_GETTER_IMPL_CLASS + "=" +jvmCloneGetterImplClass);
	    	
	    	System.out.println("-------------------------------------------------------------------");
	    	
	    }
	    static private  RuntimeEnvProperties getInstance(){
	        if (_instance == null) {
	        	synchronized(syncObject) {
	        		_instance = new RuntimeEnvProperties();
	        	}
	        }
	        return _instance;
	    }

		/**
		 * @return the containerType
		 */
		public static String getContainerType() {
			return getInstance().containerType;
		}

		public static String getJvmCloneGetterImplClass() {
			return getInstance().jvmCloneGetterImplClass;
		}

		

		

		
}
