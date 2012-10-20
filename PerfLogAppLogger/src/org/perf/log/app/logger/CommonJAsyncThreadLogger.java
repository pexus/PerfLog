/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/app/logger/CommonJAsyncThreadLogger.java 
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

/**
 *Implement asynchronous thread based logging using J2EE CommonJ 
 * @author Pradeep Nambiar 9/27/2012
 */

import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.perf.log.app.logger.CommonJAsyncWorkerThread;
import org.perf.log.logger.LogQueueMetricTracker;
import org.perf.log.utils.PropertyFileLoader;

import commonj.work.WorkException;
import commonj.work.WorkManager;


public  class CommonJAsyncThreadLogger {
	
	// Async Logger Thread Implementation properties
	public static final String ASYNC_THREAD_LOGGER_NUM_ASYNC_LOGGER_TASK_THREADS = "commonJAsyncThreadLogger.numAsyncLoggerTaskThreads"; 
	public static final String ASYNC_THREAD_LOGGER_MAX_Q_SIZE = "commonJAsyncThreadLogger.maxQSize"; 
	public static final String ASYNC_THREAD_LOGGER_WORK_MANAGER_RESOURCE_NAME = "commonJAsyncThreadLogger.workManagerThreadPoolResourceName";
	// Max records in a single Q
	static long maxQSize = 1000;
	// num instances of worker thread
	static int numAsyncLoggerTaskThreads = 2;
	// JNDI Resource for default work manager
	static String workManagerResourceName = "wm/default";
	private Object syncObject = new Object();

	static ArrayList<LinkedBlockingQueue<AppLogData>> appLogQueueArrayList = new ArrayList<LinkedBlockingQueue<AppLogData>>();
	LogQueueMetricTracker logQueueMetricTrackerArray[];
	private static Context ctx = null;
	
	int roundRobinCount = 0;
		
	static boolean inited = false;
	
	private void initProperties() {

		try {

			String propVal;
			ClassLoader ctxClassLoader = Thread.currentThread().getContextClassLoader();
			Properties props = PropertyFileLoader.load(
					"perfLogAppLogger.properties", 
					"perfLogAppLogerDefault.properties", 
					ctxClassLoader,
					CommonJAsyncThreadLogger.class.getClass().getClassLoader(),
					CommonJAsyncThreadLogger.class.getName());
			
			if (props != null) {
				propVal = props.getProperty(ASYNC_THREAD_LOGGER_MAX_Q_SIZE);
				if (propVal != null)
					setMaxQSize(new Long(propVal).longValue());
				propVal = props
						.getProperty(ASYNC_THREAD_LOGGER_NUM_ASYNC_LOGGER_TASK_THREADS);
				if (propVal != null)
					setNumAsyncLoggerTaskThreads(new Integer(propVal)
							.intValue());
				propVal = props
						.getProperty(ASYNC_THREAD_LOGGER_WORK_MANAGER_RESOURCE_NAME);
				if (propVal != null)
					setWorkManagerResourceName(propVal);
			} else {
				System.out.println(CommonJAsyncThreadLogger.class.getName()
						+ ":Error in reading perfLogAppLogger properties");
			}
		} catch (Exception exception) {
			System.out.println(CommonJAsyncThreadLogger.class.getName()
					+ ":Error Loading perfLogAppLogger properties"
					+ exception.getMessage());
		}

	}

	public CommonJAsyncThreadLogger() {
		super();
		
		// Initialize and start the worker threads
		if (inited == false) {
			synchronized (syncObject) {
				if (inited == false) {
					initProperties();
					int numThreads = getNumAsyncLoggerTaskThreads();
					logQueueMetricTrackerArray = new LogQueueMetricTracker[numThreads];
					CommonJAsyncWorkerThread asyncLogTaskArray[] = new CommonJAsyncWorkerThread[numThreads];
					// create the work objects
					for (int i = 0; i < numThreads; i++) {
						logQueueMetricTrackerArray[i] = new LogQueueMetricTracker();
						appLogQueueArrayList.add(i,
								new LinkedBlockingQueue<AppLogData>());
						asyncLogTaskArray[i] = new CommonJAsyncWorkerThread(
								"PerfLogAppLoggerAsyncWorkerThread_" + i,
								appLogQueueArrayList.get(i),
								logQueueMetricTrackerArray[i]);
					}
					try {
						ctx = new InitialContext();
						WorkManager wm = (WorkManager) ctx
								.lookup("wm/default");
						for (int i = 0; i < numThreads; i++)
							wm.schedule(asyncLogTaskArray[i]);
					} catch (NamingException e) {
						System.out.println(CommonJAsyncThreadLogger.class.getName()+"Exception:"+e.getMessage());
					} catch (WorkException e) {
						System.out.println(CommonJAsyncThreadLogger.class.getName()+"Exception:"+e.getMessage());
					} catch (IllegalArgumentException e) {
						System.out.println(CommonJAsyncThreadLogger.class.getName()+"Exception:"+e.getMessage());
					}
				} // if
			} // sync

		} // if
		

	}

	private synchronized int getQueueIndexToLog() {
		// do a round robin between the number of Queues available..
		int index = roundRobinCount;
		roundRobinCount++;
		if (roundRobinCount == getNumAsyncLoggerTaskThreads())
			roundRobinCount = 0;
		return index;
	}

	
	public void log(AppLogData appLogData) {
		 {
			//--- Begin Log - if log enabled and exceeds log threshold
			int index = getQueueIndexToLog();
			if (logQueueMetricTrackerArray[index].getQSize() <= getMaxQSize()) {
				appLogQueueArrayList.get(index).add(appLogData);
				logQueueMetricTrackerArray[index].incrementSize();

			} else {
				// remove the oldest in the Q and add this one to the end..
				// This will ensure the memory will not grow unbounded.
				// and at the same time keep the most recent perf log records
				
				// and increment the num dropped count..
				appLogQueueArrayList.get(index).remove();
				appLogQueueArrayList.get(index).add(appLogData);
				logQueueMetricTrackerArray[index].incrementNumDropped();
			}
			
			//--- End log
		}

	}

	
	/**
	 * @return the maxQSize
	 */
	public static long getMaxQSize() {
	
		return maxQSize;

	}

	/**
	 * @return the numAsyncLoggerTaskThreads
	 */
	public static int getNumAsyncLoggerTaskThreads() {
		
		
		return numAsyncLoggerTaskThreads;
	}

	/**
	 * @param maxQSize
	 *            the maxQSize to set
	 */
	public static void setMaxQSize(long maxQSize) {
		CommonJAsyncThreadLogger.maxQSize = maxQSize;
	}

	/**
	 * @param numAsyncLoggerTaskThreads
	 *            the numAsyncLoggerTaskThreads to set
	 */
	public static void setNumAsyncLoggerTaskThreads(int numAsyncLoggerTasks) {
		CommonJAsyncThreadLogger.numAsyncLoggerTaskThreads = numAsyncLoggerTasks;
	}

	

	public static String getWorkManagerResourceName() {
		return workManagerResourceName;
	}

	public static void setWorkManagerResourceName(String workManagerResourceName) {
		CommonJAsyncThreadLogger.workManagerResourceName = workManagerResourceName;
	}

}
