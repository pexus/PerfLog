/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/app/logger/CommonJAsyncWorkerThread.java 
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
 * WebSphere Work Manager Worker Thread for logging to a file 
 * 
 * Multiple threads can be created that listens to independent queues
 *  
 * @author Pradeep Nambiar 9/27/2012
 */

import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.perf.log.logger.LogQueueMetricTracker;
import org.perf.log.utils.PropertyFileLoader;

import commonj.work.Work;

public class CommonJAsyncWorkerThread implements Work {

	String asyncLogTaskName;
	LinkedBlockingQueue<AppLogData> logQueue;
	LogQueueMetricTracker logQueueMetricTracker;

	// Property Names

	public static final String ASYNC_THREAD_LOGGER_THREAD_SLEEP_TIME_IN_MILLIS = "commonJAsyncThreadLogger.threadSleepTimeInMillis";
	public static final String ASYNC_THREAD_LOGGER_PRINT_STAT_TIME_INTERVAL_IN_MILLIS = "commonJAsyncThreadLogger.printStateTimeIntervalInMillis";
	static int minFlushThreshold = 500;
	static long threadSleepTimeInMillis = 10000;
	static long printStatTimeIntervalInMillis = 600000;

	static boolean terminateThread = false;

	private static boolean propertiesInited = false;

	private synchronized void initProperties() {
		if (!propertiesInited) {
			try {
				String propVal;
				ClassLoader ctxClassLoader = Thread.currentThread().getContextClassLoader();
				Properties props = PropertyFileLoader.load(
						"perfLogAppLogger.properties",
						"perfLogAppLogerDefault.properties",
						ctxClassLoader,
						CommonJAsyncWorkerThread.class.getClass().getClassLoader(),
						CommonJAsyncWorkerThread.class.getName());

				if (props != null) {
					propVal = props
							.getProperty(ASYNC_THREAD_LOGGER_PRINT_STAT_TIME_INTERVAL_IN_MILLIS);
					if (propVal != null)
						setPrintStatTimeIntervalInMillis(new Long(propVal)
								.longValue());

					propVal = props
							.getProperty(ASYNC_THREAD_LOGGER_THREAD_SLEEP_TIME_IN_MILLIS);
					if (propVal != null)
						setThreadSleepTimeInMillis(new Long(propVal)
								.longValue());

				} else {
					System.out.println(CommonJAsyncWorkerThread.class.getName()
							+ ":Error in reading perfAppLogger properties");
				}

			} catch (Exception exception) {
				System.out.println(CommonJAsyncWorkerThread.class.getName()
						+ ":Error Loading perfLogAppLogger properties"
						+ exception.getMessage());
			}

			propertiesInited = true;
		}

	}

	public CommonJAsyncWorkerThread(String asyncLogTaskName,
			LinkedBlockingQueue<AppLogData> logQueue,
			LogQueueMetricTracker logQueueMetricTracker) {
		super();
		this.asyncLogTaskName = asyncLogTaskName;
		this.logQueue = logQueue;
		this.logQueueMetricTracker = logQueueMetricTracker;
		initProperties();
	}

	@Override
	public void release() {
		// stop the thread
		terminateThread = true;

	}

	@Override
	public void run() {
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		// add thread name to the task name
		this.asyncLogTaskName = Thread.currentThread().getName() + ":"
				+ this.asyncLogTaskName;
		logQueueMetricTracker
				.setThreadManagingThisQueue(Thread.currentThread());
		// Configuration params
		System.out.println(asyncLogTaskName + ": Worker thread started.");
		long lastPrintStatTimeInMillis = System.currentTimeMillis();
		System.out.println(asyncLogTaskName + ": "
				+ logQueueMetricTracker.getStats());

		// iterate over the queue and write to application log file
		while (!terminateThread) {
			try {	
					AppLogData appLogData = logQueue.poll(getThreadSleepTimeInMillis(), TimeUnit.MILLISECONDS);
					if(appLogData != null) {
						if (appLogData.throwable != null)
							appLogData.javaUtilLogger.log(
									appLogData.javaUtilLoggerLevel,
									appLogData.logData, appLogData.throwable);
						else
							appLogData.javaUtilLogger.log(
									appLogData.javaUtilLoggerLevel,
									appLogData.logData);
						logQueueMetricTracker.decrementSize();
					}
					if ((System.currentTimeMillis() - lastPrintStatTimeInMillis) > printStatTimeIntervalInMillis) {
						System.out.println(asyncLogTaskName + ": PerfLogAppLogger Stats: "
								+ logQueueMetricTracker.getStats());
						lastPrintStatTimeInMillis = System.currentTimeMillis();
					}
			}
			catch(InterruptedException e) {
				// ignore..
			}
		}
		System.out.println(asyncLogTaskName + ": "
				+ logQueueMetricTracker.getStats());
		System.out.println(asyncLogTaskName + ": Worker thread terminated.");

	}

	/**
	 * @return the threadSleepTimeInMillis
	 */
	public static long getThreadSleepTimeInMillis() {
		return threadSleepTimeInMillis;
	}

	/**
	 * @return the printStatTimeIntervalInMillis
	 */
	public static long getPrintStatTimeIntervalInMillis() {
		return printStatTimeIntervalInMillis;
	}

	/**
	 * @param threadSleepTimeInMillis
	 *            the threadSleepTimeInMillis to set
	 */
	public static void setThreadSleepTimeInMillis(long inThreadSleepTimeInMillis) {
		threadSleepTimeInMillis = inThreadSleepTimeInMillis;
	}

	/**
	 * @param printStatTimeIntervalInMillis
	 *            the printStatTimeIntervalInMillis to set
	 */
	public static void setPrintStatTimeIntervalInMillis(
			long inPrintStatTimeIntervalInMillis) {
		printStatTimeIntervalInMillis = inPrintStatTimeIntervalInMillis;
	}

	@Override
	public boolean isDaemon() {

		return true;
	}

}
