/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/org/perf/log/utils/RuntimeEnvironmentHelper.java 
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
package org.perf.log.utils;

import java.net.UnknownHostException;



/**
 * Singleton helper class to acquire hostname, ip and JVM name
 */
public class RuntimeEnvHelper {
	
	private static final String UNKNOWN_VALUE = "Unknown";
	private static RuntimeEnvHelper instance = new RuntimeEnvHelper();
	
	private static boolean inited = false;

	// stores the host name of the local machine

	private String hostName = null;

	// stores the ip address of the local machine
	private String ipAddress = null;

	// stores the JVM name, this is useful in a clustered environment
	// to identify the java container instance
	// Call to get this is usually implementation specific
	// Use JMX interface to query the instance name
	// If a suitable class is not found,
	// ManagementFactory.getRuntimeMXBean().getName() is used that
	// returns ProcessID@hostname

	private String cloneName = null;

	private void initHostNameAndIpAddress() {
		// set up host name
		try {
			// the following line is where the exception may be thrown
			java.net.InetAddress i = java.net.InetAddress.getLocalHost();

			this.hostName = i.getHostName();
			this.ipAddress = i.getHostAddress();
		} catch (UnknownHostException e) {
			// if the exception is thrown, set the hostname and ip address to
			// the unknown constant
			this.hostName = UNKNOWN_VALUE;
			this.ipAddress = UNKNOWN_VALUE;
		}
	}

	private void initCloneName() {
		this.cloneName = JvmCloneGetterFactory.getJvmCloneGetterImpl().getName();
	}
	
	private synchronized void initInstanceVars() {
		if(inited)
			return;
		initHostNameAndIpAddress();
		initCloneName();
		System.out.println("org.perf.log.RuntimeEnvHelper: hostName =" + this.hostName  + " ipAddress = "+ this.ipAddress + " cloneName = "+this.cloneName);
		inited=true;

	}


	/*
	 * Default constructor for this class
	 * 
	 * initialize the environment related details
	 */
	private RuntimeEnvHelper() {
		
	}

	public String getHostAndIP() {
		if(!inited) 
			initInstanceVars();
		
		return this.getHostName() + "/" + this.getIpAddress();
	}

	/**
	 * Returns the hostname for the local server
	 * 
	 * @return
	 */
	public String getHostName() {
		if(!inited) 
			initInstanceVars();
		return this.hostName;

	}

	/**
	 * Returns the IP Address for the local server
	 * 
	 * @return
	 */
	public String getIpAddress() {
		if(!inited) 
			initInstanceVars();
		return this.ipAddress;
	}

	/**
	 * Returns the singleton instance of the class
	 * 
	 * @return
	 */
	public static RuntimeEnvHelper getInstance() {
		return RuntimeEnvHelper.instance;
	}

	/**
	 * method to return the current cloneName / instance of the server (JVM)
	 * 
	 * @return cloneName for the current instance
	 */
	public String getCloneName() {
		if(!inited) 
			initInstanceVars();
		return this.cloneName;

	}
}
