/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/logger/PerfLoggerImplStdOut.java 
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

public class PerfLoggerImplStdOut implements PerfLogger {
	
	@Override
	public boolean getLogEnabled(long txnTimeInMillis) {

		return (LoggerProperties.getInstance().isPerfLoggerImplLogEnabled() 
				&& 
				(txnTimeInMillis >= 
					LoggerProperties.getInstance().getPerfLoggerImplLogThreshold()));
	}
	
	public void setLogEnabled(boolean inLogEnabled) {
	}

	@Override
	public void log(PerfLogData perfLogData) {
		if(getLogEnabled(perfLogData.getTransactionTime())) {
		    System.out.println(perfLogData.getFullFormatedPerfDataStr());
		  }
		}

}
