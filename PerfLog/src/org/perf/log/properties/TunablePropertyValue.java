/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/properties/NSBPropertyValue.java 
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

/**
 * Name Space Binding Property object
 *   *
 */
public class TunablePropertyValue {
	boolean defined; // checked only once to avoid repeated lookups
	boolean dynamic;
	String value;
	long lastCheckTime;
	/**
	 * @return the defined
	 */
	public boolean isDefined() {
		return defined;
	}
	/**
	 * @param defined the defined to set
	 */
	public void setDefined(boolean defined) {
		this.defined = defined;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * @return the dynamic
	 */
	public boolean isDynamic() {
		return dynamic;
	}
	/**
	 * @param dynamic the dynamic to set
	 */
	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}
	/**
	 * @return the lastCheckTime
	 */
	public long getLastCheckTime() {
		return lastCheckTime;
	}
	/**
	 * @param lastCheckTime the lastCheckTime to set
	 */
	public void setLastCheckTime(long lastCheckTime) {
		this.lastCheckTime = lastCheckTime;
	}
}
