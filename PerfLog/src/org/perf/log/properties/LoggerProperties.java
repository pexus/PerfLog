/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/properties/LoggerProperties.java 
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

import java.io.InputStream;
import java.util.Properties;

import org.perf.log.logger.FileWriter;
import org.perf.log.utils.PropertyFileLoader;


public class LoggerProperties {

	private static final String thisClassName = LoggerProperties.class.getName();
	private static final String PERF_LOG_PROPERTIES = "perfLog.properties";
	private static Object syncObject = new Object();
	// property names from perfLog.properties
	private static final String LOGGER_DYNAMIC_LOGGER_IMPL_WARN_ENABLED = "dynamic.logger.loggerImpl.warnEnabled";
	private static final String LOGGER_DYNAMIC_LOGGER_IMPL_TRACE_ENABLED = "dynamic.logger.loggerImpl.traceEnabled";
	private static final String LOGGER_DYNAMIC_LOGGER_IMPL_INFO_ENABLED = "dynamic.logger.loggerImpl.infoEnabled";
	private static final String LOGGER_DYNAMIC_LOGGER_IMPL_ERROR_ENABLED = "dynamic.logger.loggerImpl.errorEnabled";	
	private static final String LOGGER_DYNAMIC_LOGGER_IMPL_DEBUG_ENABLED = "dynamic.logger.loggerImpl.debugEnabled"; // dynamic
	private static final String LOGGER_STATIC_LOGGER_IMPL_CLASS = "static.logger.loggerImplClass";	
	private static final String LOGGER_STATIC_PERF_LOGGER_IMPL_CLASS = "static.logger.perfLoggerImplClass";
	private static final String LOGGER_DYNAMIC_PERF_LOGGER_IMPL_LOG_ENABLED = "dynamic.logger.perfLoggerImpl.logEnabled"; // dynamic
	private static final String LOGGER_DYNAMIC_PERF_LOGGER_IMPL_LOG_THRESHOLD = "dynamic.logger.perfLoggerImpl.logThreshold"; // dynamic
	private static final String LOGGER_DYNAMIC_PERF_LOG_STRUTS_ENABLED = "dynamic.logger.perfLogStruts.Enabled";
	private static final String LOGGER_DYNAMIC_PERF_LOG_STRUTS_THRESHOLD = "dynamic.logger.perfLogStruts.Threshold";
	private static final String LOGGER_DYNAMIC_PERF_LOG_SERVLET_ENABLED = "dynamic.logger.perfLogServlet.Enabled";
	private static final String LOGGER_DYNAMIC_PERF_LOG_SERVLET_THRESHOLD = "dynamic.logger.perfLogServlet.Threshold";
	private static final String LOGGER_DYNAMIC_PERF_LOG_PORTLET_ENABLED = "dynamic.logger.perfLogPortlet.Enabled";
	private static final String LOGGER_DYNAMIC_PERF_LOG_PORTLET_THRESHOLD = "dynamic.logger.perfLogPortlet.Threshold";
	private static final String LOGGER_DYNAMIC_PERF_LOG_SQL_ENABLED = "dynamic.logger.perfLogSql.Enabled";
	private static final String LOGGER_DYNAMIC_PERF_LOG_SQL_THRESHOLD = "dynamic.logger.perfLogSql.Threshold";
	private static final String LOGGER_DYNAMIC_PERF_LOG_SQL_CACHE_SQL_IN_CONTEXT  = "dynamic.logger.perfLogSQL.CacheSQLInContext";
	private static final String LOGGER_DYNAMIC_PERF_LOG_SQL_CACHE_SQL_IN_CONTEXT_THRESHOLD = "dynamic.logger.perfLogSQL.CacheSQLInContext.threshold";
	
	
	private static final String LOGGER_DYNAMIC_PERF_LOG_WS_ENABLED = "dynamic.logger.perfLogWS.Enabled";
	private static final String LOGGER_DYNAMIC_PERF_LOG_WS_CACHE_SOAP_MESSAGE = "dynamic.logger.perfLogWS.CacheSOAPMessage";
	private static final String LOGGER_DYNAMIC_PERF_LOG_WS_THRESHOLD = "dynamic.logger.perfLogWS.Threshold";
	private static final String LOGGER_DYNAMIC_PERF_LOG_START_END_PERFLOGTXN_ENABLED = "dynamic.logger.logStartEndPerfLogTxn.Enabled";
	
	private static final String LOGGER_STATIC_PERF_LOG_TUNABLE_PROPERTY_REFRESH_INTERNVAL = "static.logger.tunableProperties.propertyRefreshInterval";
	private static final String LOGGER_STATIC_PERF_LOG_TUNABLE_PROPERTIES_IMPL_CLASS = "static.logger.tunablePropertiesImplClass";
	
	// Properties for two implementation of TunableProperties provided in perfLog project
	private static final String LOGGER_STATIC_PERF_LOG_TUNABLE_PROPERTIES_ENABLED = "static.logger.tunableProperties.enabled";
	private static final String LOGGER_STATIC_PERF_LOG_NSB_TUNABLE_PROPERTIES_NSBROOT = "static.logger.tunableProperties.nameSpaceBindingRoot";
	private static final String LOGGER_STATIC_PERF_LOG_URL_RESOURCE_TUNABLE_PROPERTIES_URL = "static.logger.URLResourceTunablePropertiesImpl.URLResource";
	

	// Don't use loggers here since they may have not been initialized yet
	static private LoggerProperties instance = null;
	
	// default implementation - override using properties file
	private String loggerImplClass="org.perf.log.logger.LoggerImpl";
	
	private boolean loggerImplDebugEnabled=false;
	private boolean loggerImplErrorEnabled=true;
	private boolean loggerImplInfoEnabled=false;
	
	private boolean loggerImplTraceEnabled=false;
	private boolean loggerImplWarnEnabled=true;
	
	
	private  String perfLoggerImplClass="org.perf.log.logger.PerfLoggerImpl";
	private boolean perfLoggerImplLogEnabled=true;
	private long perfLoggerImplLogThreshold=0;
	
	// finer control of perf logging and caching SQL in debug context
	private boolean perfLogSqlEnabled=true;
	private long perfLogSqlThreshold=0;
	private boolean perfLogSqlCacheSQLInContext=true;
	private long perfLogSqlCacheSQLInContextThreshold=0;
	
	private boolean perfLogWSEnabled=true; // for web service
	private long perfLogWSThreshold=0;
	private boolean perfLogWSCacheSOAPMessage=false;
	private boolean perfLogPortletEnabled=true;
	private long perfLogPortletThreshold=0;
	private boolean perfLogServletEnabled=true;
	private long perfLogServletThreshold=0;
	private boolean perfLogStrutsEnabled=true;
	private long perfLogStrutsThreshold=0;
	private boolean logStartEndPerfLogTxnEnabled=true;
	
	private boolean tunablePropertiesEnabled = false; // indicates whether to use tunable properties feature
	private long tunablePropertyRefreshInterval = 180000; // in ms = 3 minutes
	private String tunablePropertiesImplClass = null;
	
	private String tunablePropertiesImplNSBindingRoot = "cell/persistent/PerfLog";
	// This following is a sample URL resource default
	private String tunablePropertiesImplUrlResource = "file:///properties/perfLog/myTunedPerfLog.properties";
	// default
	private  TunableProperties tunableProperties = new NSBTunablePropertiesImpl();
	
		
    protected LoggerProperties(){
    String errorMsg = thisClassName + ":Error Loading perfLog.properties, using defaults. ";
    try{
    	// Initialize default values from perfLog.properties
    	// Properties can be re-initialized by TunableProperties implementation
    	String propVal;
    	ClassLoader ctxClassLoader = Thread.currentThread().getContextClassLoader();
		Properties props = PropertyFileLoader.load(
				"perfLog.properties", 
				"perfLogDefault.properties", 
				ctxClassLoader,
				this.getClass().getClassLoader(),
				LoggerProperties.class.getName());
		if (props != null) {
    	
        	propVal  = props.getProperty(LOGGER_STATIC_LOGGER_IMPL_CLASS);
        	if(propVal!=null)
        			loggerImplClass = propVal;
        	propVal  = props.getProperty(LOGGER_DYNAMIC_LOGGER_IMPL_DEBUG_ENABLED);
        	if(propVal!=null)
        			loggerImplDebugEnabled = new Boolean(propVal).booleanValue();
        	propVal  = props.getProperty(LOGGER_DYNAMIC_LOGGER_IMPL_ERROR_ENABLED);
        	if(propVal!=null)
        			loggerImplErrorEnabled = new Boolean(propVal).booleanValue();
        	        	
        	propVal  = props.getProperty(LOGGER_DYNAMIC_LOGGER_IMPL_INFO_ENABLED);
        	if(propVal!=null)
        			loggerImplInfoEnabled = new Boolean(propVal).booleanValue();
        	
        	propVal  = props.getProperty(LOGGER_DYNAMIC_LOGGER_IMPL_TRACE_ENABLED);
        	if(propVal!=null)
        			loggerImplTraceEnabled = new Boolean(propVal).booleanValue();
        	
        	propVal  = props.getProperty(LOGGER_DYNAMIC_LOGGER_IMPL_WARN_ENABLED);
        	if(propVal!=null)
        			loggerImplWarnEnabled = new Boolean(propVal).booleanValue();
        	
        	propVal  = props.getProperty(LOGGER_STATIC_PERF_LOGGER_IMPL_CLASS);
        	if(propVal!=null)
        			perfLoggerImplClass = propVal;
        	propVal  = props.getProperty(LOGGER_DYNAMIC_PERF_LOGGER_IMPL_LOG_ENABLED);
        	if(propVal!=null)
        		perfLoggerImplLogEnabled = new Boolean(propVal).booleanValue();
        	propVal  = props.getProperty(LOGGER_DYNAMIC_PERF_LOGGER_IMPL_LOG_THRESHOLD);
        	if(propVal!=null)
        		perfLoggerImplLogThreshold = new Long(propVal).longValue();
        	
        	
        	// SQL properties
        	propVal  = props.getProperty(LOGGER_DYNAMIC_PERF_LOG_SQL_ENABLED);
        	if(propVal!=null)
        		perfLogSqlEnabled = new Boolean(propVal).booleanValue();
        	
        	propVal  = props.getProperty(LOGGER_DYNAMIC_PERF_LOG_SQL_THRESHOLD);
        	if(propVal!=null)
        		perfLogSqlThreshold = new Long(propVal).longValue();
        	
        	propVal  = props.getProperty(LOGGER_DYNAMIC_PERF_LOG_SQL_CACHE_SQL_IN_CONTEXT);
        	if(propVal!=null)
        		perfLogSqlCacheSQLInContext = new Boolean(propVal).booleanValue();
        	propVal  = props.getProperty(LOGGER_DYNAMIC_PERF_LOG_SQL_CACHE_SQL_IN_CONTEXT_THRESHOLD);
        	if(propVal!=null)
        		perfLogSqlCacheSQLInContextThreshold = new Long(propVal).longValue();
        	
        	
        	// Web Service properties
        	propVal  = props.getProperty(LOGGER_DYNAMIC_PERF_LOG_WS_ENABLED);
        	if(propVal!=null)
        		perfLogWSEnabled = new Boolean(propVal).booleanValue();
        	propVal  = props.getProperty(LOGGER_DYNAMIC_PERF_LOG_WS_CACHE_SOAP_MESSAGE);
        	if(propVal!=null)
        		perfLogWSCacheSOAPMessage = new Boolean(propVal).booleanValue();
        	propVal  = props.getProperty(LOGGER_DYNAMIC_PERF_LOG_WS_THRESHOLD);
        	if(propVal!=null)
        		perfLogWSThreshold = new Long(propVal).longValue();
        	
        	// Portlet properties
        	propVal  = props.getProperty(LOGGER_DYNAMIC_PERF_LOG_PORTLET_ENABLED);
        	if(propVal!=null)
        		perfLogPortletEnabled = new Boolean(propVal).booleanValue();
        	propVal  = props.getProperty(LOGGER_DYNAMIC_PERF_LOG_PORTLET_THRESHOLD);
        	if(propVal!=null)
        		perfLogPortletThreshold = new Long(propVal).longValue();
        	
        	// Struts properties
        	propVal  = props.getProperty(LOGGER_DYNAMIC_PERF_LOG_STRUTS_ENABLED);
        	if(propVal!=null)
        		perfLogStrutsEnabled = new Boolean(propVal).booleanValue();
        	propVal  = props.getProperty(LOGGER_DYNAMIC_PERF_LOG_STRUTS_THRESHOLD);
        	if(propVal!=null)
        		perfLogStrutsThreshold = new Long(propVal).longValue();
        	
        	// Servlet properties
        	propVal  = props.getProperty(LOGGER_DYNAMIC_PERF_LOG_SERVLET_ENABLED);
        	if(propVal!=null)
        		perfLogServletEnabled = new Boolean(propVal).booleanValue();
        	propVal  = props.getProperty(LOGGER_DYNAMIC_PERF_LOG_SERVLET_THRESHOLD);
        	if(propVal!=null)
        		perfLogServletThreshold = new Long(propVal).longValue();
        	
        	propVal  = props.getProperty(LOGGER_DYNAMIC_PERF_LOG_START_END_PERFLOGTXN_ENABLED);
        	if(propVal!=null)
        		logStartEndPerfLogTxnEnabled = new Boolean(propVal).booleanValue();
        	
        	// ------------------------------------------------------------------
        	// Tunable Properties properties
        	propVal  = props.getProperty(LOGGER_STATIC_PERF_LOG_TUNABLE_PROPERTIES_ENABLED);
        	if(propVal!=null)
        		tunablePropertiesEnabled = new Boolean(propVal).booleanValue();
        	propVal = props.getProperty(LOGGER_STATIC_PERF_LOG_TUNABLE_PROPERTY_REFRESH_INTERNVAL);
        	if(propVal!=null)
        		tunablePropertyRefreshInterval = new Long(propVal).longValue();
        	propVal = props.getProperty(LOGGER_STATIC_PERF_LOG_TUNABLE_PROPERTIES_IMPL_CLASS);
        	if(propVal!=null) {
        		tunablePropertiesImplClass = propVal;
        		
        	}
        	// Tunable Properties implementation specific properties
        	// NSBTunablePrpopertiesImpl specific properties
        	propVal = props.getProperty(LOGGER_STATIC_PERF_LOG_NSB_TUNABLE_PROPERTIES_NSBROOT);
        	if(propVal!=null)
        		tunablePropertiesImplNSBindingRoot = propVal;
        	
        	//URLResourceTunablePropertiesImpl specific properties 
        	propVal = props.getProperty(LOGGER_STATIC_PERF_LOG_URL_RESOURCE_TUNABLE_PROPERTIES_URL);
        	if(propVal!=null)
        		tunablePropertiesImplUrlResource = propVal;
        	
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
    	System.out.println(thisClassName + ":---- Logger properties from file ------------------------");
    	System.out.println(thisClassName + ":"+LOGGER_STATIC_PERF_LOGGER_IMPL_CLASS+"="+loggerImplClass);    	
    	System.out.println(thisClassName + ":"+LOGGER_DYNAMIC_LOGGER_IMPL_DEBUG_ENABLED+"="+loggerImplDebugEnabled);
    	System.out.println(thisClassName + ":"+LOGGER_DYNAMIC_LOGGER_IMPL_ERROR_ENABLED+"="+loggerImplErrorEnabled);
    	System.out.println(thisClassName + ":"+LOGGER_DYNAMIC_LOGGER_IMPL_INFO_ENABLED+"="+loggerImplInfoEnabled);
    	System.out.println(thisClassName + ":"+LOGGER_DYNAMIC_LOGGER_IMPL_TRACE_ENABLED+"="+loggerImplTraceEnabled);
    	System.out.println(thisClassName + ":"+LOGGER_DYNAMIC_LOGGER_IMPL_WARN_ENABLED+"="+loggerImplWarnEnabled);
    	System.out.println(thisClassName + ":"+LOGGER_STATIC_PERF_LOGGER_IMPL_CLASS+"="+perfLoggerImplClass);
    	System.out.println(thisClassName + ":"+LOGGER_DYNAMIC_PERF_LOGGER_IMPL_LOG_ENABLED+"="+perfLoggerImplLogEnabled);
    	System.out.println(thisClassName + ":"+LOGGER_DYNAMIC_PERF_LOGGER_IMPL_LOG_THRESHOLD+"="+perfLoggerImplLogThreshold);
    	System.out.println(thisClassName + ":"+LOGGER_DYNAMIC_PERF_LOG_SQL_ENABLED+"="+perfLogSqlEnabled);
    	System.out.println(thisClassName + ":"+LOGGER_DYNAMIC_PERF_LOG_SQL_THRESHOLD+"="+perfLogSqlThreshold);
    	System.out.println(thisClassName + ":"+LOGGER_DYNAMIC_PERF_LOG_SQL_CACHE_SQL_IN_CONTEXT+"="+perfLogSqlCacheSQLInContext);
    	System.out.println(thisClassName + ":"+LOGGER_DYNAMIC_PERF_LOG_SQL_CACHE_SQL_IN_CONTEXT_THRESHOLD+"="+perfLogSqlCacheSQLInContextThreshold);
    	System.out.println(thisClassName + ":"+LOGGER_DYNAMIC_PERF_LOG_WS_ENABLED+"="+perfLogWSEnabled);
    	System.out.println(thisClassName + ":"+LOGGER_DYNAMIC_PERF_LOG_WS_CACHE_SOAP_MESSAGE+"="+perfLogWSCacheSOAPMessage);
    	System.out.println(thisClassName + ":"+LOGGER_DYNAMIC_PERF_LOG_WS_THRESHOLD+"="+perfLogWSThreshold);
    	System.out.println(thisClassName + ":"+LOGGER_DYNAMIC_PERF_LOG_START_END_PERFLOGTXN_ENABLED+"="+logStartEndPerfLogTxnEnabled);
    	System.out.println(thisClassName + ":"+LOGGER_DYNAMIC_PERF_LOG_PORTLET_ENABLED+"="+perfLogPortletEnabled);
    	System.out.println(thisClassName + ":"+LOGGER_DYNAMIC_PERF_LOG_PORTLET_THRESHOLD+"="+perfLogPortletThreshold);
    	System.out.println(thisClassName + ":"+LOGGER_DYNAMIC_PERF_LOG_SERVLET_ENABLED+"="+perfLogServletEnabled);
    	System.out.println(thisClassName + ":"+LOGGER_DYNAMIC_PERF_LOG_SERVLET_THRESHOLD+"="+perfLogServletThreshold);
    	System.out.println(thisClassName + ":"+LOGGER_DYNAMIC_PERF_LOG_STRUTS_ENABLED+"="+perfLogStrutsEnabled);
    	System.out.println(thisClassName + ":"+LOGGER_DYNAMIC_PERF_LOG_STRUTS_THRESHOLD+"="+perfLogStrutsThreshold);
    	System.out.println(thisClassName + ":"+LOGGER_STATIC_PERF_LOG_TUNABLE_PROPERTIES_ENABLED+"="+tunablePropertiesEnabled);
    	System.out.println(thisClassName + ":"+LOGGER_STATIC_PERF_LOG_URL_RESOURCE_TUNABLE_PROPERTIES_URL+"="+tunablePropertiesImplUrlResource);
    	System.out.println(thisClassName + ":"+LOGGER_STATIC_PERF_LOG_NSB_TUNABLE_PROPERTIES_NSBROOT+"="+tunablePropertiesImplNSBindingRoot);
    	System.out.println(thisClassName + ":"+LOGGER_STATIC_PERF_LOG_TUNABLE_PROPERTY_REFRESH_INTERNVAL+"="+tunablePropertyRefreshInterval);
    	System.out.println(thisClassName + ":"+LOGGER_STATIC_PERF_LOG_TUNABLE_PROPERTIES_IMPL_CLASS+"="+tunablePropertiesImplClass);
    	System.out.println(thisClassName + ":--------------------------------------------------------------");
    	
    }
    static public LoggerProperties getInstance(){
        if (instance == null) {
        	synchronized (syncObject) {
        		if(instance == null)
        			instance = new LoggerProperties();
        		//-----------------------------------------------------
        		// Instantiate TunableProperties Implementation
        		System.out.println("LoggerProperties: instantiating " + instance.tunablePropertiesImplClass + " class for TunableProperties implementation." );
        		try {
        			if(instance.tunablePropertiesEnabled && instance.tunablePropertiesImplClass != null) {
					instance.tunableProperties = (TunableProperties) Thread
						.currentThread().getContextClassLoader()
								.loadClass(instance.tunablePropertiesImplClass).newInstance();
        			}
        			else
        				instance.tunableProperties = new NullTunablePropertiesImpl();
				} catch (Exception e) {
					System.out.println("LoggerProperties: Error instantiating " + instance.tunablePropertiesImplClass + " using default implementation NSBTunablePropertiesImpl.");
					instance.tunableProperties = new NSBTunablePropertiesImpl();
				}
        		
        	}
        }
        return instance;
    }

	public  String getLoggerImplClass() {
		
		String propertyValue = tunableProperties.getStaticProperty(LOGGER_STATIC_LOGGER_IMPL_CLASS);
		if(propertyValue != null) {
			System.out.println(thisClassName + ":Overriding property via TunableProperties: static.logger.loggerImplClass="+propertyValue);
			return propertyValue;
		}
		else // return value initialized in this class
			return loggerImplClass;
	}

	public  String getPerfLoggerImplClass() {
		
		String propertyValue = tunableProperties.getStaticProperty(LOGGER_STATIC_PERF_LOGGER_IMPL_CLASS);
		if(propertyValue != null) {
			System.out.println(thisClassName + ":Overriding property via TunableProperties: static.logger.perfLoggerImplClass="+propertyValue);
			return propertyValue;
		}
		else // return value initialized in this class
			return perfLoggerImplClass;
	}

	public boolean isLoggerImplDebugEnabled() {
		String propertyValue = tunableProperties.getDynamicProperty(LOGGER_DYNAMIC_LOGGER_IMPL_DEBUG_ENABLED);
		if(propertyValue != null) {
			return new Boolean(propertyValue).booleanValue();
		}
		else // return value initialized in this class
			return loggerImplDebugEnabled;
	}

	public boolean isLoggerImplErrorEnabled() {
		String propertyValue = tunableProperties.getDynamicProperty(LOGGER_DYNAMIC_LOGGER_IMPL_ERROR_ENABLED);
		if(propertyValue != null) {
			return new Boolean(propertyValue).booleanValue();
		}
		else // return value initialized in this class
			return loggerImplErrorEnabled;
	}

	public boolean isLoggerImplInfoEnabled() {
		String propertyValue = tunableProperties.getDynamicProperty(LOGGER_DYNAMIC_LOGGER_IMPL_INFO_ENABLED);
		if(propertyValue != null) {
			return new Boolean(propertyValue).booleanValue();
		}
		else // return value initialized in this class
			return loggerImplInfoEnabled;
	}

	public boolean isLoggerImplTraceEnabled() {
		String propertyValue = tunableProperties.getDynamicProperty(LOGGER_DYNAMIC_LOGGER_IMPL_TRACE_ENABLED);
		if(propertyValue != null) {
			return new Boolean(propertyValue).booleanValue();
		}
		else // return value initialized in this class
			return loggerImplTraceEnabled;
	}

	public boolean isLoggerImplWarnEnabled() {
		String propertyValue = tunableProperties.getDynamicProperty(LOGGER_DYNAMIC_LOGGER_IMPL_WARN_ENABLED);
		if(propertyValue != null) {
			return new Boolean(propertyValue).booleanValue();
		}
		else // return value initialized in this class
			return loggerImplWarnEnabled;
	}

	public boolean isPerfLoggerImplLogEnabled() {
		
		String propertyValue = tunableProperties.getDynamicProperty(LOGGER_DYNAMIC_PERF_LOGGER_IMPL_LOG_ENABLED);
		if(propertyValue != null) {
			return new Boolean(propertyValue).booleanValue();
		}
		else // return value initialized in this class
			return perfLoggerImplLogEnabled;
	}

	/**
	 * @return the perfLogSqlEnabled
	 */
	public boolean isPerfLogSqlEnabled() {
		
		String propertyValue = tunableProperties.getDynamicProperty(LOGGER_DYNAMIC_PERF_LOG_SQL_ENABLED);
		if(propertyValue != null) {
			
			return new Boolean(propertyValue).booleanValue();
		}
		else // return value initialized in this class
		
			return perfLogSqlEnabled;
	}
	
	public boolean isPerfLogSqlCacheSQLInContext() {
		String propertyValue = tunableProperties.getDynamicProperty(LOGGER_DYNAMIC_PERF_LOG_SQL_CACHE_SQL_IN_CONTEXT);
		if(propertyValue != null) {
			
			return new Boolean(propertyValue).booleanValue();
		}
		else // return value initialized in this class
			return perfLogSqlCacheSQLInContext;
	}

	

	/**
	 * @return the perfLogWSEnabled
	 */
	public boolean isPerfLogWSEnabled() {
		
		String propertyValue = tunableProperties.getDynamicProperty(LOGGER_DYNAMIC_PERF_LOG_WS_ENABLED);
		if(propertyValue != null) {
			
			return new Boolean(propertyValue).booleanValue();
		}
		else // return value initialized in this class
			return perfLogWSEnabled;
	}

	/**
	 * @return the perfLogPortletEnabled
	 */
	public boolean isPerfLogPortletEnabled() {
		
		String propertyValue = tunableProperties.getDynamicProperty(LOGGER_DYNAMIC_PERF_LOG_PORTLET_ENABLED);
		if(propertyValue != null) {
			return new Boolean(propertyValue).booleanValue();
		}
		else // return value initialized in this class
			return perfLogPortletEnabled;
	}

	/**
	 * @return the perfLogServletEnabled
	 */
	public boolean isPerfLogServletEnabled() {
		
		String propertyValue = tunableProperties.getDynamicProperty(LOGGER_DYNAMIC_PERF_LOG_SERVLET_ENABLED);
		if(propertyValue != null) {
			return new Boolean(propertyValue).booleanValue();
		}
		else // return value initialized in this class
			return perfLogServletEnabled;
	}

	/**
	 * @return the perfLogStrutsEnabled
	 */
	public boolean isPerfLogStrutsEnabled() {
		
		String propertyValue = tunableProperties.getDynamicProperty(LOGGER_DYNAMIC_PERF_LOG_STRUTS_ENABLED);
		if(propertyValue != null) {
			return new Boolean(propertyValue).booleanValue();
		}
		else // return value initialized in this class
			return perfLogStrutsEnabled;
	}
	
   public boolean isLogStartEndPerfLogTxnEnabled() {
		String propertyValue = tunableProperties.getDynamicProperty(LOGGER_DYNAMIC_PERF_LOG_START_END_PERFLOGTXN_ENABLED);
		if(propertyValue != null) {
			return new Boolean(propertyValue).booleanValue();
		}
		else // return value initialized in this class
			return logStartEndPerfLogTxnEnabled;
	}
	
	public long getPerfLoggerImplLogThreshold() {
		String propertyValue = tunableProperties.getDynamicProperty(LOGGER_DYNAMIC_PERF_LOGGER_IMPL_LOG_THRESHOLD);
		if(propertyValue != null) {
			return new Long(propertyValue).longValue();
		}
		else // return value initialized in this class
			return perfLoggerImplLogThreshold;
	}

	public long getPerfLogSqlThreshold() {
		String propertyValue = tunableProperties.getDynamicProperty(LOGGER_DYNAMIC_PERF_LOG_SQL_THRESHOLD);
		if(propertyValue != null) {
			return new Long(propertyValue).longValue();
		}
		else // return value initialized in this class
			return perfLogSqlThreshold;
	}
	

	public long getPerfLogSqlCacheSQLInContextThreshold() {
		String propertyValue = tunableProperties.getDynamicProperty(LOGGER_DYNAMIC_PERF_LOG_SQL_CACHE_SQL_IN_CONTEXT_THRESHOLD);
		if(propertyValue != null) {
			return new Long(propertyValue).longValue();
		}
		else // return value initialized in this class
			return perfLogSqlCacheSQLInContextThreshold;
	}
	
	
	
	public long getPerfLogWSThreshold() {
		String propertyValue = tunableProperties.getDynamicProperty(LOGGER_DYNAMIC_PERF_LOG_WS_THRESHOLD);
		if(propertyValue != null) {
			return new Long(propertyValue).longValue();
		}
		else // return value initialized in this class
			return perfLogWSThreshold;
	}

	public boolean isPerfLogWSCacheSOAPMessage() {
		String propertyValue = tunableProperties.getDynamicProperty(LOGGER_DYNAMIC_PERF_LOG_WS_CACHE_SOAP_MESSAGE);
		if(propertyValue != null) {
			return new Boolean(propertyValue).booleanValue();
		}
		else // return value initialized in this class
			return perfLogWSCacheSOAPMessage;
	}
	
	public long getPerfLogPortletThreshold() {
		String propertyValue = tunableProperties.getDynamicProperty(LOGGER_DYNAMIC_PERF_LOG_PORTLET_THRESHOLD);
		if(propertyValue != null) {
			return new Long(propertyValue).longValue();
		}
		else // return value initialized in this class
			return perfLogPortletThreshold;
	}
	
	public long getPerfLogServletThreshold() {
		String propertyValue = tunableProperties.getDynamicProperty(LOGGER_DYNAMIC_PERF_LOG_SERVLET_THRESHOLD);
		if(propertyValue != null) {
			return new Long(propertyValue).longValue();
		}
		else 
			return perfLogServletThreshold;
	}

	public long getPerfLogStrutsThreshold() {
		String propertyValue = tunableProperties.getDynamicProperty(LOGGER_DYNAMIC_PERF_LOG_STRUTS_THRESHOLD);
		if(propertyValue != null) {
			return new Long(propertyValue).longValue();
		}
		else 
		return perfLogStrutsThreshold;
	}
	
	
	// return singleton
	public TunableProperties getTunableProperties() {
		return tunableProperties;
	}
	
	//------------------------------------------------------------------------
	// The following properties can only be defined in perfLog.properties
	// and initialized only once.. If there is a change, the application will
	// have to be re-deployed
	
	public long getTunablePropertyRefreshInterval() {
		return tunablePropertyRefreshInterval;
	}
	
	public String getTunablePropertiesImplClass() {
		
		return tunablePropertiesImplClass;
	}

	public String getTunablePropertiesImplUrlResource() {
		return tunablePropertiesImplUrlResource;
	}
	
	/**
	 * @return the nameSpaceBindingRoot
	 */
	public String getNameSpaceBindingRoot() {
		return tunablePropertiesImplNSBindingRoot;
	}

	



	



	

	
	

	

	
	
}
