/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/logger/WasWmAsyncLoggerWorkerThread.java 
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
 * WebSphere Work Manager Worker Thread for logging to a file and database
 * The thread is created in PerfLoggerImplWasWmAsyncThread class
 * Multiple threads can be created that listens to independent queues
 * The PerfLoggerImplWasWmAsyncThread class uses a simple round robin
 * to queue to multiple queues
 * 
 * @author Pradeep Nambiar 2/10/2012
 */

import java.util.concurrent.LinkedBlockingQueue;

import com.ibm.websphere.asynchbeans.Work;

public class WasWmAsyncLoggerWorkerThread extends AsyncLoggerWorkerThreadAbstract implements Work {
	
	public WasWmAsyncLoggerWorkerThread(String asyncLogTaskName,
			LinkedBlockingQueue<PerfLogData> logQueue,
			LogQueueMetricTracker logQueueMetricTracker) {
		super(asyncLogTaskName,logQueue,logQueueMetricTracker);
	}
}
