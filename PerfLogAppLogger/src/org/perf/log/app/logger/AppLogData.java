/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/app/logger/AppLogData.java 
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
package org.perf.log.app.logger;

import java.util.logging.Level;


public class AppLogData {
	java.util.logging.Logger    javaUtilLogger;
	Level javaUtilLoggerLevel;
	String logData;
	Throwable throwable;
	public java.util.logging.Logger getJavaUtilLogger() {
		return javaUtilLogger;
	}
	public void setJavaUtilLogger(java.util.logging.Logger javaUtilLogger) {
		this.javaUtilLogger = javaUtilLogger;
	}
	public Level getJavaUtilLoggerLevel() {
		return javaUtilLoggerLevel;
	}
	public void setJavaUtilLoggerLevel(Level javaUtilLoggerLevel) {
		this.javaUtilLoggerLevel = javaUtilLoggerLevel;
	}
	public String getLogData() {
		return logData;
	}
	public void setLogData(String logData) {
		this.logData = logData;
	}
	public Throwable getThrowable() {
		return throwable;
	}
	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}
	
	
}
