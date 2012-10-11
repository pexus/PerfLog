/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/logger/LogQueueMetricTracker.java 
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
 * 
 * @author Pradeep Nambiar 2/10/2012
 */

public class LogQueueMetricTracker {
	long enqueueRate; // enqueue rate per minute
	long dequeueRate; // dequeue rate per minute
	long enQueued = 0; // total enqueued
	long deQueued = 0; // total dequeued
	long enQueuedInThisInterval = 0;
	long deQueuedInThisInterval = 0;
	long qSize = 0;
	long firstEnqueueTime=0;
	long firstDequeueTime=0;
	long enqueueIntervalStartTime = System.currentTimeMillis();
	long dequeueIntervalStartTime = System.currentTimeMillis();
	long rateCheckInterval = 60000; // 60 seconds
	long maxEnqueueRate = 0;
	long maxDequeueRate = 0;
	long maxQDepth = 0;
	long numDropped = 0; // number of records discarded due to full queue
	long numErrored = 0; // number of records that was not persisted due to error
	Thread threadManagingThisQueue = null; // set in the worker thread
	boolean threadManagingThisQueueIsSleeping = false;
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	/**
	 * @return the qSize
	 */
	public long getQSize() {
		return qSize;
	}

	public void incrementSize() {
		synchronized (this) {
			if(enQueued == 0) firstEnqueueTime = System.currentTimeMillis();
			++qSize;
			++enQueued;
			++enQueuedInThisInterval;
			updateEnqueueRate();
		}

	}
	public void updateEnqueueRate() {
		long thisInterval = System.currentTimeMillis() - enqueueIntervalStartTime;
		if (thisInterval > rateCheckInterval) {
			enqueueRate = (enQueuedInThisInterval * 60 * 1000) / thisInterval;
			enQueuedInThisInterval = 0;
			enqueueIntervalStartTime = System.currentTimeMillis();
			if (enqueueRate > maxEnqueueRate)
				maxEnqueueRate = enqueueRate;
			if(qSize > maxQDepth)
				maxQDepth = qSize;
		}
		
	}
	public void updateDequeueRate() {
		long thisInterval = System.currentTimeMillis() - dequeueIntervalStartTime;
		if (thisInterval > rateCheckInterval) {
			dequeueRate = (deQueuedInThisInterval * 60 * 1000) / thisInterval;
			deQueuedInThisInterval = 0;
			dequeueIntervalStartTime = System.currentTimeMillis();
			if (dequeueRate > maxDequeueRate)
				maxDequeueRate = dequeueRate;
			if(qSize > maxQDepth)
				maxQDepth = qSize;
		}
	}
	public void updateEnqueueAndDequeueRate() {
		updateEnqueueRate();
		updateDequeueRate();
	}

	public void decrementSize() {
		synchronized (this) {
			if(deQueued == 0) firstDequeueTime = System.currentTimeMillis();
			--qSize;
			++deQueued;
			++deQueuedInThisInterval;
			updateDequeueRate();
		}
		

	}

	public synchronized void incrementNumDropped() {
		++numDropped;
	}
	
	public synchronized void incrementNumErrored() {
		++numErrored;
	}

	/**
	 * @return the enqueueRate
	 */
	public long getEnqueueRate() {
		return enqueueRate;
	}

	/**
	 * @return the dequeueRate
	 */
	public long getDequeueRate() {
		return dequeueRate;
	}

	/**
	 * @return the enQueued
	 */
	public long getEnQueued() {
		return enQueued;
	}

	/**
	 * @return the deQueued
	 */
	public long getDeQueued() {
		return deQueued;
	}

	/**
	 * @return the enqueueIntervalStartTime
	 */
	public long getEnqueueIntervalStartTime() {
		return enqueueIntervalStartTime;
	}

	/**
	 * @return the dequeueIntervalStartTime
	 */
	public long getDequeueIntervalStartTime() {
		return dequeueIntervalStartTime;
	}

	/**
	 * @return the interval
	 */
	public long getRateCheckInterval() {
		return rateCheckInterval;
	}

	/**
	 * @return the maxEnqueueRate
	 */
	public long getMaxEnqueueRate() {
		return maxEnqueueRate;
	}

	/**
	 * @return the maxDequeueRate
	 */
	public long getMaxDequeueRate() {
		return maxDequeueRate;
	}

	/**
	 * @return the numDropped
	 */
	public long getNumDropped() {
		return numDropped;
	}
	
	public String getStats() {
		updateEnqueueAndDequeueRate();
		
		long totalTimeSinceFirstEnqueue;
		long totalTimeSinceFirstDequeue;
		if(firstEnqueueTime==0)
			totalTimeSinceFirstEnqueue = 0;
		else
			totalTimeSinceFirstEnqueue = 
					(System.currentTimeMillis() - firstEnqueueTime)/(1000*60);
		if(firstDequeueTime==0)
			totalTimeSinceFirstDequeue = 0;
		else
			totalTimeSinceFirstDequeue = 
				(System.currentTimeMillis() - firstDequeueTime)/(1000*60);
			
		
		return( LINE_SEPARATOR + 
				"Enqueue Rate/min (current/max)= ("
				+ this.getEnqueueRate() + "/"
				+ this.getMaxEnqueueRate() + ") Total Enqueues/Total time (min)= " 
					+ this.enQueued  + "/" + totalTimeSinceFirstEnqueue + LINE_SEPARATOR  
				+ "Dequeue Rate/min (current/max)= ("
				+ this.getDequeueRate() + "/"
				+ this.getMaxDequeueRate() + ") Total Dequeues/Total time (min)= " 
					+ this .deQueued + "/" + totalTimeSinceFirstDequeue + LINE_SEPARATOR   
				+ "Q Depth (current/max)= (" + qSize + "/" + maxQDepth 
				+ ") Num Dropped= " + this.getNumDropped()
				+ " Num Errored= " + this.getNumErrored()
				);
	}
	@Override
	public String toString() {
		return getStats();
	}

	public Thread getThreadManagingThisQueue() {
		return threadManagingThisQueue;
	}

	public void setThreadManagingThisQueue(Thread threadManagingThisQueue) {
		this.threadManagingThisQueue = threadManagingThisQueue;
	}

	public boolean isThreadManagingThisQueueIsSleeping() {
		return threadManagingThisQueueIsSleeping;
	}

	public void setThreadManagingThisQueueIsSleeping(
			boolean threadManagingThisQueueIsSleeping) {
		this.threadManagingThisQueueIsSleeping = threadManagingThisQueueIsSleeping;
	}

	public long getNumErrored() {
		return numErrored;
	}
}
