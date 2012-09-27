/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/logger/PerfLoggerImplCommonJAsyncThread.java 
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

/**
 * This Perf Logger implementation uses CommonJ J2EE APIs to create
 * asynchronous work manager threads  to log performance data to a file and database
 * The class extends from the PerfLoggerAbsAsyncThread abstract class
 * @author Pradeep Nambiar 2/10/2012
 */

import java.util.concurrent.ConcurrentLinkedQueue;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import commonj.work.WorkException;
import commonj.work.WorkManager;

public class PerfLoggerImplCommonJAsyncThread extends PerfLoggerImplAsyncThreadAbstract {

	private static Logger aLogger = LoggerFactory.getLogger(PerfLoggerImplCommonJAsyncThread.class.getName());

	private static boolean inited = false;
	private static Context ctx = null;
	private Object syncObject = new Object();
	
	public PerfLoggerImplCommonJAsyncThread() {
		super();
		// Initialize and start the worker threads
		if (inited == false) {
			synchronized (syncObject) {
				if (inited == false) {
					int numThreads = getNumAsyncLoggerTaskThreads();
					logQueueMetricTrackerArray = new LogQueueMetricTracker[numThreads];
					CommonJAsyncLoggerWorkerThread asyncLogTaskArray[] = new CommonJAsyncLoggerWorkerThread[numThreads];
					// create the work objects
					for (int i = 0; i < numThreads; i++) {
						logQueueMetricTrackerArray[i] = new LogQueueMetricTracker();
						perfLogQueueArrayList.add(i,
								new ConcurrentLinkedQueue<PerfLogData>());
						asyncLogTaskArray[i] = new CommonJAsyncLoggerWorkerThread(
								"asyncLoggerTaskThread_" + i,
								perfLogQueueArrayList.get(i),
								logQueueMetricTrackerArray[i]);
					}
					try {
						ctx = new InitialContext();
						WorkManager wm = (WorkManager) ctx
								.lookup("wm/default");
						for (int i = 0; i < numThreads; i++)
							wm.schedule(asyncLogTaskArray[i]);
					} catch (NamingException e) {
						aLogger.error(e.getMessage(), e);
					} catch (WorkException e) {
						aLogger.error(e.getMessage(), e);
					} catch (IllegalArgumentException e) {
						aLogger.error(e.getMessage(), e);
					}
				} // if
			} // sync

		} // if
	}

	

}
