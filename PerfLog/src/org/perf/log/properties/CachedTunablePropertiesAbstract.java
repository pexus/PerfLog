/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/properties/CachedTunablePropertiesAbstract.java 
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

import java.util.concurrent.ConcurrentHashMap;


public abstract class CachedTunablePropertiesAbstract implements TunableProperties {
	public CachedTunablePropertiesAbstract() {
		super();
		
	}

	protected static ConcurrentHashMap<String, TunablePropertyValue> tunablePropertyHashMap = new ConcurrentHashMap<String, TunablePropertyValue>();
	
			

	// Extending class should implement this to read the property in an implementation 
	// dependent way
	protected abstract TunablePropertyValue readTunableProperty(String name) ;

	protected synchronized  String getCachedTunableProperty(String name, boolean dynamic) {
		long tunablePropertyRefreshInterval = LoggerProperties.getInstance().getTunablePropertyRefreshInterval();
		TunablePropertyValue tunablePropertyValue = tunablePropertyHashMap.get(name);
		if (tunablePropertyValue == null) {
			tunablePropertyValue = readTunableProperty(name);
			if (tunablePropertyValue == null) {
				tunablePropertyValue = new TunablePropertyValue();
				tunablePropertyValue.setDefined(false);
				tunablePropertyValue.setValue(null);
			}
			else if(!dynamic) {
				// print to indicate we read this property from tunable properties source, as this property will be
				// read only once
				System.out.println("getCachedTunableProperty(): Property overriden from tunable property source: " 
						+ name + "=" + tunablePropertyValue.getValue());
			}
			tunablePropertyValue.setDynamic(dynamic);
			tunablePropertyHashMap.put(name, tunablePropertyValue);
			return tunablePropertyValue.getValue();
		} else if (!tunablePropertyValue.isDefined()) {
			return null;
		} else if (tunablePropertyValue.isDefined()
				&& !tunablePropertyValue.isDynamic()) {
			return tunablePropertyValue.getValue();
		} else if (tunablePropertyValue.isDefined() && tunablePropertyValue.isDynamic()) {
			// check if the property needs to be refreshed
			if (System.currentTimeMillis()
					- tunablePropertyValue.getLastCheckTime() > tunablePropertyRefreshInterval) {
				String currentValue = tunablePropertyValue.getValue();
				tunablePropertyValue = readTunableProperty(name);
				if (tunablePropertyValue != null) {
					if (currentValue != null
							&& !currentValue
									.equals(tunablePropertyValue.getValue())) {
						System.out.println("getCachedTunableProperty(): " +"Property value for property name "
										+ name + " changed. Current value = "
										+ currentValue + " New value = "
										+ tunablePropertyValue.getValue());
					}
				} else {
					tunablePropertyValue = new TunablePropertyValue();
					tunablePropertyValue.setDefined(false);
					tunablePropertyValue.setValue(null);
				}
				tunablePropertyValue.setDynamic(true);
				tunablePropertyHashMap.put(name, tunablePropertyValue);
				return tunablePropertyValue.getValue();
			} else
				return tunablePropertyValue.getValue();
		} else
			return null;
	}

}
