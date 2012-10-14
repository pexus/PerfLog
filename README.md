Pexus PerfLog - Performance and Diagnostics Logging Framework for J2EE Applications
-----------------------------------------------------------------------------------

http://www.pexus.com/perflog

What is Pexus PerfLog?
---------------------

Pexus PerfLog is an open source performance and diagnostic logging framework for 
J2EE/Java Applications. It can easily be integrated with any standard J2EE 
application via the standard J2EE configuration files to capture performance 
metrics without writing any custom code or modifying the J2EE application code. 
Performance metrics that includes JDBC SQLs can be persisted to log files and database. 
The log file data generated is compatible with the popular log file monitoring tools such as 
Splunk. Data persisted to a database can be used for long term storage, aggregation of 
performance metrics for reporting and visualization.

The PerfLog package also includes an application logger - PerfLogAppLogger, this 
is integrated with PerfLog and  based on the standard Java java.util.logging.*
API logger implementation. PerfLog enhances the application log statements with
additional request context data and cached the application debug statements. The
cached debug statements can be dumped if the requests response time exceeds
specified threshold. The application logger implementation can be used
for application logger such as log4j.

Pexus PerfLog logging framework offers deep J2EE application performance insight
to application Architects, application developers and IT operations
team. Developers and Architects can capture specific business contextual information and 
extend the framework easily to capture application specific request data.
It can complement any monitoring solution already in place for the precise application
diagnosis and capturing performance metrics for SLA monitoring and performance
analysis. IT Operations team can leverage the log monitoring tools such as Splunk,
to identify slow performing transactions and perform search analytics  easily from 
performance logs created by PerfLog.

PerfLog and PerfLogAppLogger packages are available as a free open source
community edition with Apache 2.0 License and is a supported edition for IBM WebSphere
J2EE application server. The community edition comes with PerfLog and PerfLogAppLogger
binary jars, source and documentation. See the Pexus PerfLog Editions section of this 
documentation for more details.

How does Pexus PerfLog work?
---------------------------

Pexus PerfLog captures the key request response performance metrics, request data and application
contextual data for the following type of J2EE requests:

		Servlet

		Portlet 

		Web Service - JAX-RPC and JAX-WS

		JDBC - SQL queries and execution times
		
		Struts (IBM WebSphere Portal)
	

PerfLog uses the J2EE filter pattern to plug-in the request interceptor classes without
changing the application code. It can also be used within the application code to
monitor custom transactions.  Each request or transaction from a thread is
assigned to a globally unique identifier (guid). Each request is associated
with the PerfLog context data that includes additional information such as
thread id, user id, request type, information context, request data, debug
trace data among others. Integration with application logger enables retrieving and
printing of contextual information for each application log entry. Full
PerfLog context data also can be dumped to which  can include the SQL queries
executed during the request along with the application debug trace when the request
response time exceeds a certain threshold.


Persisting performance metric data to database and log files can be done
asynchronously thereby having no or very minimal impact to application
performance.  Buffer size properties enables fine tuning of the memory and limits
memory used for caching the request and debug data. Note: Asynchronous thread
based logging requires J2EE environment. PerfLog also supports JMS / Queue based
logging. Additional logger implementation can easily be written to suit any 
custom requirements.


Multiple JVM Tracking
---------------------

Pexus PerfLog can track requests spanning multiple JVMs using a request GUID
(Globally Unique Identifier). Currently,  multiple JVM tracking is available
for Web Service calls for both JAX-RPC and JAX-WS. 

Extensibility
-------------

Pexus PerfLog is extensible and can easily be customized. Custom performance metrics can
be captured and logged from the application code, in addition to the standard J2EE
requests and JDBC SQL queries. PerfLog filter code can be extended easily
to capture custom metrics or custom session data e.g. extracting userid
from  session data or application contextual information from session or
request data.

Integration with Application Loggers
------------------------------------

Pexus PerfLog can also be easily integrated with any application logger
to enhance application logging to include elapsed time for each logged statement along
with request context data. PerfLog also enables caching of debug traces 
when application log level prevents logging the debug statements.  The cached debug log
statements are logged when the request response time exceeds a defined threshold. The cache is
cleared when the request finishes executing and thereby optimizing the memory usage.
The cache size is configurable via property setting. When debug trace exceeds cache size, 
the oldest debug logs are discarded to make room for the most recent debug log statements.

PerfLog includes a sample application logger called - PerfLogAppLogger.
The included application logger is fully integrated with PerfLog. 
PerfLogAppLogger, can also be used as your application logger as this is based on the  
standard J2EE/J2SE API and it will work without any additional third party jars.


Runtime Environment Changes
---------------------------

Pexus PerfLogg uses  standard J2EE APIs and it will work unchanged in all the  J2EE
environment. 

Any J2EE environment dependent code is abstracted as an interface. The implementation class
can be specified in runtimeEnv.properties file. 

Currently the following two interfaces are required:

	org.perf.log.utils.JvmCloneGetter
	org.perf.log.utils.PortletInfoGetter

JvmCloneGetter implementation returns the JVM instance name in a clustered environment and 
PortletInfoGetter implementation returns the ortlet name and page name  for a portlet. 
Default implementations and a limited J2EE environment specific implementations are
provided in PerfLog. Custom  implementation can be easily specified via runtimeEnv.properties.

JvmCloneGetter implementation for Tomcat returns the "name,jvmRoute" attribute for the "Catalina:type=Engine"
JMX object. Implementation for IBM WebSphere returns the "cell\node\server" to identify the JVM instance.
The default implementation for JvmCloneGetter returns the ManagementFactory.getRuntimeMXBean().getName() which 
is the proceed-id@hostname to identify the JVM instance. Implementation for JBOSS, GlassFish, Oracle App Server, 
and Weblogic currently returns the default implementation. A custom impelemtation can be easily implemented 
and implementing class can be specified in runtimeEnv.properties file.

PortletInfoGetter implementation for IBM WebSphere Portal is included. Default implementation returns the portlet ID
for portlet name and portlet context path for portlet page name.

Property Files
--------------

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
	in the src folder an application or in the application classpath. 
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

Pexus PerfLog Editions
----------------------

Pexus PerfLog is available as a free open source edition and a premium supported
edition for IBM WebSphere Environment.

Pexus PerfLog - Community Edition (CE)
--------------------------------------

Pexus PerfLog CE is an open source edition comes with Apache 2.0 License and includes
the source code and binary jar file. Public Git repository are also available
from the following two locations:

	GitHub:
		Src Browse: https://github.com/pexus/PerfLog
		Git Clone:  git://github.com/pexus/PerfLog.git
		Git Clone:  https://github.com/pexus/PerfLog.git
		Git Clone:  https://github.com/pexus/PerfLog/zipball/master

	Pexus Git Repository:
		Src Browse: http://git.pexus.net/perflog/
		Git Clone:	http://git.pexus.net/perflog
		Git Clone:  git@git.pexus.net:perflog

	Pexus PerfLog CE Binary Download Page and Links:
		http://www.pexus.com/perflog/
		http://download.pexus.net/perflog/CE/PerfLog_CE.zip
		http://download.pexus.net/perflog/CE/PerfLog_CE.tar
		http://download.pexus.net/perflog/CE/PerfLog_CE.tar.gz

Pexus PerfLog - Supported Edition (SE) for IBM WebSphere
--------------------------------------------------------

The Pexus PerfLog Supported Edition (SE) for IBM WebSphere comes with binary jars,
sample test/demo binary files, documentation, full e-mail support, and regular 
maintenance upgrades. Supported edition is priced per JVM. Please visit 
http://www.pexus.com/perflog for more details on pricing or contact Pexus LLC. 


Pexus PerfLog Customization Services
------------------------------------

Pexus LLC also offers integration and customization consulting services in integrating
Pexus PerfLog SE for IBM WebSphere and other standard J2EE environment and customer applications.

Building Pexus PerfLog Community Edition
----------------------------------------

The dependency jar list for PerfLog and PerfLogAppLogger jars are given below
if you choose to extend, customize and build PerfLog and PerfLogAppLogger from sources.

	PerfLog:
		ojdbc6.jar (Oracle JDBC driver jar) 
		derby.jar (IBM embedded Derby DB JDBC driver jar)
		mysql-connector-java-5.x-bin.jar (MySQL JDBC Driver jar) 
		db2jcc.jar (DB2 JDBC Driver jar)
		catalina.jar (Tomcat) 
		wp.base.jar (WebSphere Portal) 
		wp.model.api.jar (WebSphere Portal)
		struts.jar (Apache Struts1 - available in WebSphere Portal Runtime environment)
		wp.struts.standard.framework.jar (WebSphere Portal Runtime environment)
		org.apache.axis2.jar (Apache Axis 2 jar, also available in WebSphere Environment)
		J2EE Runtime jars (Available in your J2EE environment)

	PerfLogAppLogger:
		PerfLog.jar (dependent on PerfLog.jar) 
		Standard Java Runtime jars
		J2EE Runtime jars - if using Asynchronous Logging for CommonJ Work Manager APIs

The compiled binary jars - PerfLog.jar and PerfLogAppLogger.jar are included in the 
PerfLog CE download. Java docs and usage documentation are included in the docs folder.


When building PerfLog jar from the sources you will see the dependencies to the
following jars from the specified packages and classes in addition to standard J2EE
libraries.  You will have to download the dependent jars from vendor sites or use the 
jars from the J2EE environment.

When using the provided PerfLog.jar binary in the application you do  not have the need for 
depending jars unless you decide to use the appropriate filters or interceptor classes.

	org.perf.log.filter.struts1
		struts.jar wp.struts.standard.framework.jar 
			(IBM WebSphere Portal)

	org.perf.log.filter.portal
		wp.base.jar wp.model.api.jar


	org.perf.log.utils.TomcatJvmCloneGetterImp.java
		catalina.jar (Tomcat)

	org.perf.log.filter.sql.DB2*
		db2jcc.jar (DB2 JDBC driver)

	org.perf.log.filter.sql.Oracle*
		db2jcc.jar (DB2 JDBC driver)

	org.perf.log.filter.sql.Derby*
		derby.jar (Derby JDBC Driver)

	org.perf.log.filter.sql.MySQL*
		mysql-connector-java-5.1.21-bin.jar

When compiling from sources, If you don't intend to use the above filters
or interceptors you can remove them from your build easily to avoid compile
time errors.

PerfLog has been tested with Tomcat 7.x and IBM WebSphere 6/7/8 Environment
and with DB2, MySQL and Oracle Database and Derby databases.  For storing
performance data the DBWriter class has been tested with DB2, MySQL and
Oracle databases.


Documentation
-------------

Refer to the included javadocs and documents on installing, configuring and using PerfLog jar.
PerfLogAppLogger project  shows how you can integrate PerfLog with your favorite 
application logger to capture debug trace data from your application code.

Sample Web Application and Web Service application with source code are
provided to show how you can configure and use the PerfLog package. The
PerfLogAppLogger APIs are also used in the sample Web (PerfLogTestWebApp)
and stand alone Java test application (PerfLogTestJavaApp)

Directory content in the distribution archive
----------------------------------------------

	The latest distribution zip files can be downloaded from:
		http://www.pexus.com/perflog

	The directory content in the distribution archive are as follows: 

	Database/ 
		DDL for creating the perfDB database for persisting 
		performance log records 
	lib/ 
		binary jars for PerfLog.jar and PerfLogAppLogger.jar
	properties/
		Sample properties file for customizing for your application
		that can be included in the application src folder or in the
		application classpath 
	PerfLog/
		source for PerfLog framework 
	PerfLogAppLogger/
		Sample application logger using  
		java.util.logging.* APIs, enabled to use PerfLog framework
	PerfLogMDB/
		Optional Message Driven Bean (MDB) for PerfLog framework 
		that uses JMS Queue implementation 
	Samples/
		Sample Web and stand alone Java applications to demonstrate 
		PerfLog framework and PerfLogAppLogger

For more information visit http://www.pexus.com/perflog

Copyright(C) 2012 Pexus LLC
 
	Pexus LLC - http://www.pexus.com
 	Pexus PerfLog Info - http://www.pexus.com/perflog
 	
 	
Author

	Pradeep Nambiar, Pexus LLC

