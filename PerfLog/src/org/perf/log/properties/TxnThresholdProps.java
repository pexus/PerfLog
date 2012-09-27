/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/properties/TxnThresholdProps.java 
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
package org.perf.log.properties;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

public class TxnThresholdProps {
	private final static String thisClassName = "TxnThresholdProps:";
	private static Properties txnThresholdProps = new Properties();
	
	// this properties stores the matched names to cache to avoid
	// pattern matching every time
	private static HashMap<String, Long> cachedMatchedProps = new HashMap<String, Long>();
	private static final int maxCacheSize = 300;

	static {
		// load the response time threshold properties for transactions
		// from txnThresholdOverride.properties file
		try {
			InputStream in = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("txnThresholdOverride.properties");

			if (in != null) {
				txnThresholdProps.load(in);
				Enumeration<Object> keys = txnThresholdProps.keys();
				while (keys.hasMoreElements()) {
					String name = (String) keys.nextElement();
					String value = (String) txnThresholdProps.get(name);
					System.out.println(thisClassName + name + "=" + value);

				}
			} else {
				System.out.println(thisClassName
						+ "txnThresholdOverride.properties not found");
			}

		} catch (Exception e) {
			System.out.println(thisClassName
					+ "Exception loading txnThresholdOverride.properties:"
					+ e.getMessage());
		}
	}

	// return response time threshold for a given txn name or sub txn name
	// Checks if there the given txn name (or sub txn name) has an override
	// from the default response time threshold
	// when checking the name will compare true even if there is a partial match
	// if no special override is available, return the default response time
	// threshold
	public static synchronized long getResponseTimeThresholdForTxnName(String name) {
		String txnThresholdStr;
		Long txnThresholdLong;
		if ((txnThresholdStr = txnThresholdProps.getProperty(name)) != null) {
			// found a match..
			return new Long(txnThresholdStr).longValue();
		}
		// check in cached matched property hash map
		else if ((txnThresholdLong = cachedMatchedProps.get(name)) != null) {
			return txnThresholdLong.longValue();
		} else {
			Enumeration<Object> keys = txnThresholdProps.keys();
			while (keys.hasMoreElements()) {
				String thisName = (String) keys.nextElement();
				String thisValue = (String) txnThresholdProps.get(thisName);
				if (name.matches(".*" + Pattern.quote(thisName) + ".*")) {
					txnThresholdLong = new Long(thisValue).longValue();
					// restrict the size of cache
					if(cachedMatchedProps.size() > maxCacheSize)
						cachedMatchedProps.clear();
					cachedMatchedProps.put(name, txnThresholdLong);
					return txnThresholdLong.longValue();

				}

			}
			// if we reach here there is no special override for this
			// transaction
			// return the default response time threshold
			return PerfLogContextProperties.instance()
					.getResponseTimeThresholdInMillis();

		}

	}
	
	public static long getMaxResponseTimeThresholdForTxnList(
			List<String> txnList) {
		// initialize with default global response time threshold value
		long maxResponseTimeThreshold = PerfLogContextProperties.instance()
				.getResponseTimeThresholdInMillis();

		// override if specific transaction override is available
		if (!txnThresholdProps.isEmpty()) {
			long thisResponseTimeThreshold;
			// loop through to find the override for the txn list
			for (String txnName : txnList) {
				thisResponseTimeThreshold = getResponseTimeThresholdForTxnName(txnName);
				if (thisResponseTimeThreshold > maxResponseTimeThreshold)
					maxResponseTimeThreshold = thisResponseTimeThreshold;
			}
		}
		return maxResponseTimeThreshold;

	}
}
