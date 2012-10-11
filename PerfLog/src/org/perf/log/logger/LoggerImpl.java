/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/logger/LoggerImpl.java 
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
package org.perf.log.logger;


import java.util.logging.*;

import org.perf.log.context.PerfLogContextHelper;
import org.perf.log.properties.LoggerProperties;


public class LoggerImpl implements Logger {
	
	// Create a java.util.logggin.Logger 
	// This can be used to leverage J2EE container log level control e.g. in WebSphere
	private static java.util.logging.Logger JDKLogger = null; 
	
	private void initJDKLogger() {
		if(JDKLogger == null) {
			JDKLogger = java.util.logging.Logger.getLogger(this.getClass().getName());
			// initialize level from properties file
			// If running in WAS, subsequently the levels can be changed from WAS console if required
			// Navigate to Troubleshooting -> Logs and Traces -> <server> and Change Log Level Details
			// and look for org.perf.log.logger.LoggerImpl
			setErrorEnabled(LoggerProperties.getInstance().isLoggerImplErrorEnabled());
			setWarnEnabled(LoggerProperties.getInstance().isLoggerImplWarnEnabled());
			setInfoEnabled(LoggerProperties.getInstance().isLoggerImplInfoEnabled());
			setTraceEnabled(LoggerProperties.getInstance().isLoggerImplTraceEnabled());
			setDebugEnabled(LoggerProperties.getInstance().isLoggerImplDebugEnabled());
			JDKLogger.setUseParentHandlers(false);
		}
	}
	
	public LoggerImpl() {
		super();
		initJDKLogger();
			
	}
	  
	  
	  private static String INFO_STR = "INFO";
	  private static String WARN_STR = "WARN";
	  private static String ERROR_STR = "ERROR";
	  private static String DEBUG_STR = "DEBUG";
	  private static String TRACE_STR = "TRACE";
	  
		  
	
	public LoggerImpl(String loggerName) {
		super();
		initJDKLogger();
		this.loggerName = loggerName;
	}

	String loggerName;
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	//private static final String LINE_SEPARATOR = "\n";

	@Override
	public void debug(String msg) {
		if(getDebugEnabled())
				log(DEBUG_STR, msg,null);

	}

	@Override
	public void debug(String msg, Throwable t) {
		if(getDebugEnabled())
			log(DEBUG_STR, msg,t);

	}

	@Override
	public void error(String msg) {
		if(getErrorEnabled())
			log(ERROR_STR, msg,null);
	}

	@Override
	public void error(String msg, Throwable t) {
		if(getErrorEnabled())
			
			log(ERROR_STR, msg,t);

	}

	@Override
	public void info(String msg) {
		if(getInfoEnabled())
			log(INFO_STR, msg,null);

	}

	@Override
	public void info(String msg, Throwable t) {
		if(getInfoEnabled())
			log(INFO_STR, msg,t);

	}

	@Override
	public void trace(String msg) {
		if(getTraceEnabled())
			log(TRACE_STR, msg,null);

	}

	@Override
	public void trace(String msg, Throwable t) {
		if(getTraceEnabled())
			log(TRACE_STR, msg,t);

	}

	@Override
	public void warn(String msg) {
		if(getWarnEnabled())
				log(WARN_STR, msg,null);

	}

	@Override
	public void warn(String msg, Throwable t) {
		if(getWarnEnabled())
			log(WARN_STR, msg,t);

	}

	/**
	   * This is our internal implementation for logging regular (non-parameterized)
	   * log messages.
	   * 
	   * @param level
	   * @param message
	   * @param t
	   */
	  private void log(String level, String message, Throwable t) {

		StringBuffer buf = new StringBuffer();

		buf.append(level);
		buf.append(" ");
		buf.append(loggerName);
		buf.append(" - ");

		buf.append(PerfLogContextHelper.getBaseAndInfoContextString());
		buf.append(message);
		buf.append(LINE_SEPARATOR);

		System.out.print(buf.toString());
		if (t != null) {
			t.printStackTrace(System.out);
		}
		System.out.flush();
	}
	  

	@Override
	public String getLoggerName() {
		return loggerName;
	}

	@Override
	public void setLoggerName(String loggerName) {
	
		this.loggerName = loggerName;
	}

	@Override
	public boolean getDebugEnabled() {
		
		// this property can also change dynamically either via cell persistent name space binding
		// or via the Log / Tracing flags for a specific server from WAS console.
		
		 
		if(LoggerProperties.getInstance().isLoggerImplDebugEnabled())
			return true;
		else
			if(JDKLogger != null && JDKLogger.isLoggable(Level.FINEST))
				return true;
			else
				return false;
		
	}

	@Override
	public boolean getErrorEnabled() {
		if(JDKLogger != null && JDKLogger.isLoggable(Level.SEVERE))
			return true;
		else
			return false;
	}

	@Override
	public boolean getInfoEnabled() {
		
		if(JDKLogger != null && JDKLogger.isLoggable(Level.INFO))
			return true;
		else
			return false;
	}

	@Override
	public boolean getTraceEnabled() {
		
		if(JDKLogger != null && JDKLogger.isLoggable(Level.FINER))
			return true;
		else
			return false;
	}

	@Override
	public boolean getWarnEnabled() {
		if(JDKLogger != null && JDKLogger.isLoggable(Level.WARNING))
			return true;
		else
			return false;
	}

	@Override
	public void setErrorEnabled(boolean errorEnabled) {
		
		if(JDKLogger != null && errorEnabled)
			JDKLogger.setLevel(Level.SEVERE);
		
	}

	@Override
	public void setInfoEnabled(boolean infoEnabled) {
		
		if(JDKLogger != null && infoEnabled)
			JDKLogger.setLevel(Level.INFO);
	}

	@Override
	public void setTraceEnabled(boolean traceEnabled) {
		
		if(JDKLogger != null && traceEnabled)
			JDKLogger.setLevel(Level.FINER);
		
	}

	@Override
	public void setWarnEnabled(boolean warnEnabled) {
		
		if(JDKLogger != null && warnEnabled)
			JDKLogger.setLevel(Level.WARNING);
	}

	@Override
	public void setDebugEnabled(boolean debugEnabled) {
		
		if(JDKLogger != null && debugEnabled)
			JDKLogger.setLevel(Level.FINEST);
	}	
	
}
