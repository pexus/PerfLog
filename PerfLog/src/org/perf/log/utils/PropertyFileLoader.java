/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/utils/PropertyFileLoader.java 
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

/*
 * This class loads property files and provides flexibility to load the file
 * if one is included with in the application's class path before trying to load
 * a propety from the parent's class path. It also gives an option to load 
 * default property file if the specified property file is not found
 */
package org.perf.log.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

public class PropertyFileLoader {
	// Hash map of properties file already loaded
	// Key is the full url path of the property file
	static HashMap<String, Properties> cachedProperties = new HashMap<String, Properties>();
	static int maxCachedPropertiesHashMapSize = 50;

	// This method loads the default set of properties first
	// Then it loads the overriding properties file
	// It returns the merged properties to the caller and also caches the returned properties
	public static Properties load(
			String overridingPropertyFileName,
			String defaultPropertyFileName,
			ClassLoader ctxClassLoader,
			ClassLoader callingClassLoader,
			String callingClassName) {

		InputStream inDefaultPropertiesStream = null;
		InputStream inOverridingPropertiesStream = null;
		
		URL urlForTheDefaultPropertiesLoaded = null;
		URL urlForTheOverridingPropertiesLoaded = null;

		// First try to get the input stream for default properties using the defaultPropertyFileName using the callingClassLoader
		if (callingClassLoader != null) {
			inDefaultPropertiesStream = callingClassLoader.getResourceAsStream(defaultPropertyFileName);
			urlForTheDefaultPropertiesLoaded = callingClassLoader.getResource(defaultPropertyFileName);
		}
		
		// Next try to get the input stream for overriding properties using overridingPropertyFileName
		// using the context class loader
			
		if (ctxClassLoader != null) {
			inOverridingPropertiesStream = ctxClassLoader.getResourceAsStream(overridingPropertyFileName);
			if (inOverridingPropertiesStream != null)
				urlForTheOverridingPropertiesLoaded = ctxClassLoader.getResource(overridingPropertyFileName);
		}
		
		
		if (inDefaultPropertiesStream == null && inOverridingPropertiesStream == null) {
			System.out.println(PropertyFileLoader.class.getName()
					+ ":Error loading " + defaultPropertyFileName + " and " + overridingPropertyFileName + " properties files");
			return null;
		}

		// else check cache for the same set of properties already loaded or load properties and return the properties
		
		String cacheKey = null;
		if(inDefaultPropertiesStream !=null && inOverridingPropertiesStream != null)
			cacheKey = urlForTheDefaultPropertiesLoaded.getPath() + " + " + urlForTheOverridingPropertiesLoaded.getPath();
		else if(inDefaultPropertiesStream == null)
			cacheKey = urlForTheOverridingPropertiesLoaded.getPath();
		else if(inOverridingPropertiesStream == null)
			cacheKey = urlForTheDefaultPropertiesLoaded.getPath();

		Properties props = null;
		try {
			// Check if the property file already loaded and cached..
			if ((props = cachedProperties.get(cacheKey)) != null) {
				System.out.println(PropertyFileLoader.class.getName() + ":"
						+ cacheKey
						+ " properties already loaded, returning from cache.");

			} else {
				props = new Properties();
				if(inDefaultPropertiesStream != null) {
					props.load(inDefaultPropertiesStream);
					System.out.println(PropertyFileLoader.class.getName()
							+ ":Loading properties from:"
							+ urlForTheDefaultPropertiesLoaded.getPath());
				}
				if(inOverridingPropertiesStream != null) {
					props.load(inOverridingPropertiesStream);
					System.out.println(PropertyFileLoader.class.getName()
							+ ":Overriding properties from:"
							+ urlForTheOverridingPropertiesLoaded.getPath());
				}
				
				System.out.println("------------ Merged Properties -----");
				Enumeration <Object>  keys =  props.keys();
				while(keys.hasMoreElements()) {
					String key = (String)keys.nextElement();
					System.out.println( key  + " = " + props.get(key));
				}		
				System.out.println("------------------------------------");
				
				// Put the loaded property in the cache
				if(cachedProperties.size()>=maxCachedPropertiesHashMapSize)
					cachedProperties.clear(); // and start over again
				cachedProperties.put(cacheKey, props);
			}
		} catch (IOException e) {
			System.out.println(PropertyFileLoader.class.getName()
					+ ":IO Exception loading property file: "
					+ cacheKey);
		}
		return props;

	}

}
