/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/perf/log/test/TestApp.java 
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

/* This simple application demonstrates how to use PerfLog Framework for
 * standalone Java Applications.
 * PerfLog in conjunction with SampleAppLogger can be used to log thread contextual information
 * in addition to logging performance metrics 
 */
package perf.log.test;

import org.perf.log.app.logger.Logger;
import org.perf.log.app.logger.LoggerFactory;
import org.perf.log.context.PerfLogContextHelper;
import org.perf.log.context.TxnData;

public class TestApp {

	private static Logger appLogger = LoggerFactory.getLogger("TestApp");
	
	public static void sleepFunction(long numSleeps) {

		for (int i = 0; i < numSleeps; i++) {
			appLogger.debug("Debug Statement: Sleep Count = " + i);
			try {
				appLogger.info("Sleeping 1 second");
				Thread.sleep(1000);
				
			} catch (InterruptedException e) {
				appLogger.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		appLogger.info("In TestApp.main()");
		// Start a PerfLog Transaction Monitor
		TxnData txnData = new TxnData("MyTxnName1", "MySubTxnName1",
				"MyCustomTxnClass1", "MyCustomTxnType1");

		// Start monitoring the above transaction from this point..
		PerfLogContextHelper.startPerfLogTxnMonitor(txnData);
		appLogger.info("Start PerfLog Transaction Monitor..., Log statment should show additional transaction context like guid etc.");
		// Push some some application context data as name / value pairs to the PerfLog Context
		PerfLogContextHelper.pushInfoContext("myApplicationContextName1","Value1");
		PerfLogContextHelper.pushInfoContext("myApplicationContextDataName2","Value2");
		appLogger.info("See additional Info Context data indicating the two new name/value pairs in this log statement");
		//Pop the last pushed info context
		PerfLogContextHelper.popInfoContext();
		appLogger.info("The info context for this log statement shows one of the application context data removed");
		sleepFunction(10);
		appLogger.info("End PerfLog Transaction Monitor...");
		// Log performance metrics when ending the Txn Monitor
		PerfLogContextHelper.endPerfLogTxnMonitor(true);
		
		// Second transaction, this time seelp for 20 seconds
		// The PerfLogContext response time threshold is set to 15 seconds
		// So this time PerfLog will also dump the PerfLog Context data 
		// that contains additional context details for diagnois in addition
		// logging performance metrics for the transaction...
		
		txnData = new TxnData("MyTxnName2", "MySubTxnName2",
				"MyCustomTxnClass2", "MyCustomTxnType2");
		PerfLogContextHelper.startPerfLogTxnMonitor(txnData);
		sleepFunction(16);
		PerfLogContextHelper.endPerfLogTxnMonitor(true);
		
	}

}
