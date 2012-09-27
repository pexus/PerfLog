/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/filter/types/PerfLogTxnClass.java 
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
package org.perf.log.txn.types;

import org.perf.log.logger.PerfLogData;

/**
 * 
 * This class holds the constants values for transaction class when writing to
 * Database
 * 
 * 
 */
public class PerfLogTxnClass {
	/**
	 * TRANSACTION CLASS TYPES
	 */
	// Perf Log Transaction markers - startPerfLogTxn and endPerfLogTxn
	public static String PERFLOG_TRANSACTION_CLASS = "perfLogTxnClass";

	// TRANSACTION CLASS RUN CODES FOR SERVLETS, Portlets and other UI type txns
	public static String WEB_UI_TRANSACTION_CLASS = "webUITxnClass";

	// DATABASE TRANSACTION CLASS RUN
	public static String DB_INQUIRY_TRANSACTION_CLASS = "dbInquiryTxnClass";
	public static String DB_UPDATE_TRANSACTION_CLASS = "dbUpdateTxnClasss";
	public static String DB_BATCH_TRANSACTION_CLASS = "dbBatchTxnClasss";
	public static String DB_EXECUTE_TRANSACTION_CLASS = "dbExecuteTxnClass";

	// WEBSERVICE TRANSACTION CLASS
	public static String WEB_SERVICE_TRANSACTION_CLASS = "webServiceTxnClass";

	public static String SERVICE_TRANSACTION_CLASS = "serviceTxnClass";

	// Custom
	public static String CUSTOM_TRANSACTION_CLASS = "customTxnClass";

	public static String getTxnClassFromTransactionType(PerfLogData perfLogData) {
		// Get TRANSACTION CLASS from Transaction Type...
		// try to deduce the transaction class from transaction type
		if (perfLogData.getTransactionType() != null) {
			String txnType = perfLogData.getTransactionType();
			if (txnType.equalsIgnoreCase(PerfLogTxnType.STRUTS_ACTION_REQUEST)
					|| (txnType
							.equalsIgnoreCase(PerfLogTxnType.SERVLET_REQUEST) || (txnType
							.equalsIgnoreCase(PerfLogTxnType.PORTLET_ACTION_REQUEST) || (txnType
							.equalsIgnoreCase(PerfLogTxnType.PORTLET_RENDER_REQUEST) || (txnType
							.equalsIgnoreCase(PerfLogTxnType.PORTLET_RESOURCE_REQUEST) || (txnType
							.equalsIgnoreCase(PerfLogTxnType.PORTLET_EVENT_REQUEST))))))) {
				return PerfLogTxnClass.WEB_UI_TRANSACTION_CLASS;
			}

			else if (txnType
					.equalsIgnoreCase(PerfLogTxnType.JAX_RPC_CLIENT_REQUEST)
					|| (txnType
							.equalsIgnoreCase(PerfLogTxnType.JAX_RPC_SERVER_REQUEST) || (txnType
							.equalsIgnoreCase(PerfLogTxnType.JAX_WS_CLIENT_REQEUST) || (txnType
							.equalsIgnoreCase(PerfLogTxnType.JAX_WS_SERVER_REQUEST))))) {
				return PerfLogTxnClass.WEB_SERVICE_TRANSACTION_CLASS;
			}

			else if (txnType.equalsIgnoreCase(PerfLogTxnType.SQL_QUERY)) {
				String dbTxnName = perfLogData.getTransactionName();
				if (dbTxnName != null) {
					if (dbTxnName.equals("executeQuery"))
						return PerfLogTxnClass.DB_INQUIRY_TRANSACTION_CLASS;
					else if (dbTxnName.equals("executeUpdate"))
						return PerfLogTxnClass.DB_UPDATE_TRANSACTION_CLASS;
					else if (dbTxnName.equals("executeBatch"))
						return PerfLogTxnClass.DB_BATCH_TRANSACTION_CLASS;
					else if (dbTxnName.equals("execute"))
						return PerfLogTxnClass.DB_EXECUTE_TRANSACTION_CLASS;
					else
						return null;
				} else
					return null;

			} else if (txnType
					.equalsIgnoreCase(PerfLogTxnType.END_PERF_LOG_TRANSACTION)
					|| txnType
							.equalsIgnoreCase(PerfLogTxnType.START_PERF_LOG_TRANSACTION))
				return PerfLogTxnClass.PERFLOG_TRANSACTION_CLASS;
			else
				return PerfLogTxnClass.CUSTOM_TRANSACTION_CLASS;

		} else
			return null;
	}

}
