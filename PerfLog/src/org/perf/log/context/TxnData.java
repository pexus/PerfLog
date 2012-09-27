/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/context/TxnData.java 
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
package org.perf.log.context;

/* 
 * Class that defines the Txn to monitor
 */
public class TxnData {
	public TxnData() {
		super();
		// TODO Auto-generated constructor stub
	}
	public TxnData(String txnName, String subTxnName, String txnClass,
			String txnType) {
		super();
		this.txnName = txnName;
		this.subTxnName = subTxnName;
		this.txnClass = txnClass;
		this.txnType = txnType;
	}
	String txnName;
	String subTxnName;
	String txnClass;
	String txnType;
	public String getTxnName() {
		return txnName;
	}
	public void setTxnName(String txnName) {
		this.txnName = txnName;
	}
	public String getSubTxnName() {
		return subTxnName;
	}
	public void setSubTxnName(String subTxnName) {
		this.subTxnName = subTxnName;
	}
	public String getTxnClass() {
		return txnClass;
	}
	public void setTxnClass(String txnClass) {
		this.txnClass = txnClass;
	}
	public String getTxnType() {
		return txnType;
	}
	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}
}
