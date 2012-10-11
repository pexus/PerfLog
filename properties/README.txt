This folder contains sample properties that can be used to override the default properties
included with PerfLog.jar and PerfLogAppLogger.jar

Include the properties with your application (i.e. EAR or WAR file) or place it in the
application classpath.

PerfLog.jar Properties Files
----------------------------
PerfLog.jar already includes: 
	
	perfLogDefault.properties
	runtimeEnvDefault.properties
	
as default properties. PerfLog would first load the above properties.

If application wants to override the default properties include
 
	perfLog.properties
	runtimeEnv.properties
	 
files in your application src folder  or in your application class path.

PerfLog would merge the both properties set with properties 
defined in perfLog.properties and runtimeEnv.properties overriding the default properties.

Application can also use tunable properties implementation to additionally tune properties
at runtime. See documentation for more details on how to use tunable properties.


Optional property File - txnThresholdOverride.properties
--------------------------------------------------------
txnThresholdOverride.properties is an optional properties file that can be included
in the src folder of your application or application classpath. 
Request response time threshold value is used to decide if
the PerfLog context data needs to be dumped when deleting the PerfLog context.
(PerfLog context is deleted when the request leaves the JVM).

The PerfLog context data dump can be useful to debug performance problems
However if there are known transaction that takes more than the default
response time threshold (default 30000 milliseconds or whatever value is specified
by the overriding property file) then this file can be used to override this 
threshold for the specific transactions.




PerfLogAppLogger.jar Properties Files
-------------------------------------
PerfLogAppLogger.jar includes:
	
	perfLogAppLoggerDefault.properties

as default properties file. PerfLogAppLogger would first load the above properties.

If the application wants to override the default properties include
	
	perfLogAppLogger.properties

file in your application src folder  or in your application class path.

The properties from the above two files are merged with perfLogAppLogger.properties
overriding the default properties.


