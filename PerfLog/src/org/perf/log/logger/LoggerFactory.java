/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/logger/LoggerFactory.java 
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

import org.perf.log.properties.LoggerProperties;

public class LoggerFactory {

	public static Logger getLogger(String name) {
		LoggerImpl logger = null;
		String errorMessagePrefix = "Exception initializing Logger implementation class, using default:";
		String errorMessageSuffix = ":Using default implementation - org.perf.log.logger.LoggerImpl";
		try {
			String loggerImplClass = LoggerProperties.getInstance().getLoggerImplClass();
			logger = (LoggerImpl) Class.forName(loggerImplClass).newInstance();
			logger.setLoggerName(name);
			
		} catch (IllegalAccessException e) {
			System.out.println(errorMessagePrefix + "IllegalAccessException:"+ e.getMessage()+errorMessageSuffix);
			logger = new LoggerImpl(name);

		} catch (InstantiationException e) {
			System.out.println(errorMessagePrefix  + "InstantiationException:"+ e.getMessage()+errorMessageSuffix);
			logger = new LoggerImpl(name);
		} catch (ClassNotFoundException e) {
			System.out.println(errorMessagePrefix  + "ClassNotFoundException:"+ e.getMessage()+errorMessageSuffix);
			logger = new LoggerImpl(name);
		}
		return logger;
	}
}
