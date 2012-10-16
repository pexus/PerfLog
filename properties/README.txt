This folder contains sample properties that can be used to override the default properties
included with PerfLog.jar and PerfLogAppLogger.jar

Include the overriding property files in the source folder of the  application (i.e. EAR or WAR file)
or place it in the application classpath.

PerfLog.jar Properties Files
----------------------------
	PerfLog.jar  includes the following default properties files: 
	
	 	perfLogDefault.properties
	 	runtimeEnvDefault.properties
	
	PerfLog would first load the above properties. If an application wants to override the 
	default properties include
 
		perfLog.properties
		runtimeEnv.properties
	 
	files in the application src folder or in the application class path.

	PerfLog would merge default properties and overriding properties.
	
	Application can also use tunable properties implementation to additionally tune properties
	at runtime. See included documentation for more details on how to use tunable properties.

Optional property File - txnThresholdOverride.properties
--------------------------------------------------------
	txnThresholdOverride.properties is an optional properties file that can be included
	in the src folder of the application or in the application classpath. 
	Request response time threshold value is used to decide if
	the PerfLog context data needs to be dumped when deleting the PerfLog context.
	(PerfLog context is deleted when the request leaves the JVM).
	If there are known transaction that takes more than the default
	response time threshold as  specified in PerfLogDefault.properties or
	the overriding perfLog.properties file, then this file can be used to override this 
	threshold for the specific transactions.
	
PerfLogAppLogger.jar Properties Files
-------------------------------------
	PerfLogAppLogger.jar includes:
	
		perfLogAppLoggerDefault.properties

	as default properties file. PerfLogAppLogger would first load the above properties.
	If the application wants to override the default properties include
	
		perfLogAppLogger.properties

	file in the  application src folder  or in the application class path.

	The properties from the above two files are merged with perfLogAppLogger.properties
	overriding the default properties.

