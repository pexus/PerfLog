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
import java.util.HashMap;
import java.util.Properties;

public class PropertyFileLoader {
	// Hash map of properties file already loaded
	// Key is the full url path of the property file
	static HashMap<String, Properties> cachedProperties = new HashMap<String, Properties>();
	static int maxCachedPropertiesHashMapSize = 50;

	public static Properties load(String propertyFileName,
			String defaultPropertyFileName,
			ClassLoader ctxClassLoader,
			ClassLoader callingClassLoader,
			String callingClassName) {

		InputStream in = null;
		boolean loadingDefaultProperties = false;
		URL urlForTheResourceLoaded = null;

		// First try to load the propertyFIleName using the current thread's
		// context class loader
		// If not found use the passed in calling class's class loader to load
		// the default property file
		
		if (ctxClassLoader != null) {
			in = ctxClassLoader.getResourceAsStream(propertyFileName);
			if (in != null)
				urlForTheResourceLoaded = ctxClassLoader
						.getResource(propertyFileName);
		}
		if (in == null) {
			loadingDefaultProperties = true;
			// try loading the default properties using the calling class's
			// class loader
			System.out.println(PropertyFileLoader.class.getName()
					+ ":Error loading " + propertyFileName
					+ " attempting to load " + defaultPropertyFileName
					+ " now.");

			if (callingClassLoader != null) {
				in = callingClassLoader
						.getResourceAsStream(defaultPropertyFileName);
				urlForTheResourceLoaded = callingClassLoader
						.getResource(defaultPropertyFileName);
			}

		}

		if (in == null) {
			System.out.println(PropertyFileLoader.class.getName()
					+ ":Error loading " + defaultPropertyFileName);
			return null;
		}

		// else load properties and return the properties

		String propertyFileUsed;
		if (loadingDefaultProperties) {
			propertyFileUsed = defaultPropertyFileName;
		} else {
			propertyFileUsed = propertyFileName;
		}

		Properties props = null;
		try {
			// Check if the property file already loaded and cached..
			if ((props = cachedProperties.get(urlForTheResourceLoaded.toString())) != null) {
				System.out.println(PropertyFileLoader.class.getName() + ":"
						+ urlForTheResourceLoaded
						+ " properties already loaded, return from cache.");

			} else {
				props = new Properties();
				props.load(in);
				if (urlForTheResourceLoaded != null)
					System.out.println(PropertyFileLoader.class.getName()
							+ ":URL for resource loaded:"
							+ urlForTheResourceLoaded.getPath());
				System.out.println(callingClassName
						+ ":Loaded following properties from: "
						+ propertyFileUsed);

				System.out.println(
						"\n--------------------------------------------------------------------------\n"
						+ props.toString()
						+ "\n-------------------------------------------------------------------------\n");
				// Put the loaded property in the cache
				if(cachedProperties.size()>=maxCachedPropertiesHashMapSize)
					cachedProperties.clear(); // and start over again
				cachedProperties.put(urlForTheResourceLoaded.toString(), props);
			}
		} catch (IOException e) {
			System.out.println(PropertyFileLoader.class.getName()
					+ ":IO Exception loading property file: "
					+ propertyFileUsed);
		}
		return props;

	}

}
