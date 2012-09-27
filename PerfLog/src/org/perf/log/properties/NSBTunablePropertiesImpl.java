/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/properties/NSBProperties.java 
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

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;


/**
 * This class provides lookup for Name Space Binding properties The class caches
 * the property by default after the first lookup. If the requested property is
 * passed attribute of dynamic it will refreshes the property at regular
 * interval
 * 
 * @author Pradeep Nambiar
 * 
 */
public class NSBTunablePropertiesImpl extends CachedTunablePropertiesAbstract {
	
	public NSBTunablePropertiesImpl() {
		super();
	}

	private static final String thisClassName = "NSBTunablePropertiesImpl";
	
	protected static Context cachedContext = null;
	
	// This implementation reads the tunable properties from the WebSphere name space binding
	protected TunablePropertyValue readTunableProperty(String name) {
		if (cachedContext == null) {
			// init and cache ctx
			try {
				cachedContext = new InitialContext();
			} catch (NamingException e) {
				System.out.println(thisClassName+".readTunableProperty(): " + e.getMessage());
				return null;
			}
		}

		String value;
		String nameSpaceBindingRoot = LoggerProperties.getInstance().getNameSpaceBindingRoot();
		try {
			value = (String) cachedContext.lookup(nameSpaceBindingRoot
					+ "/" + name);
		} catch (NamingException e) {
			System.out.println(thisClassName+".readTunableProperty(): " +e.getMessage());
			return null;
		}

		if (value != null) {
				TunablePropertyValue nsbPropertyValue = new TunablePropertyValue();
				nsbPropertyValue.setValue(value);
				nsbPropertyValue.setDefined(true);
				nsbPropertyValue.setLastCheckTime(System.currentTimeMillis());
				return nsbPropertyValue;
		} else
				return null;
	}

	public String getStaticProperty(String name) {
		// Abstract implementation method manages the property via readTunableProperty implementation
		return getCachedTunableProperty(name, false);

	}

	public String getDynamicProperty(String name) {
		// Abstract implementation method manages the property via readTunableProperty implementation
		return getCachedTunableProperty(name, true);
	}

}
