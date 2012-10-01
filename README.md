Pexus PerfLog - J2EE Performance and Diagnostics Logging Framework
------------------------------------------------------------------

	http://www.pexus.com/perflog

What is PerfLog?
----------------

Pexus PerfLog is a free and open source (Apache 2.0 License) J2EE performance
and diagnostic logging framework. It can be easily integrated with any standard
J2EE application  via standard J2EE deployment configuration  files to capture
request performance metrics without writing any custom code or modifying
your J2EE application code. Performance metrics including JDBC SQLs can be
persisted to log files and a database. Log file data generated is compatible
with popular log scrapper products such as Splunk  for application diagnostics
and analytics. Data persisted to database can be used for long term storage,
SLA and performance analytics.

PerfLog package also includes an application logger - PerfLogAppLogger, that
is integrated with PerfLog and is based on standard Java java.util.logging.*
API logger implementation. PerfLog enhances application log statements with
additional request context data and cache application debug statements. The
cached debug statements can be dumped if the requests response time exceeds
a specified threshold.	The application logger implementation can be used
instead of log4j or other widely used application logger.

PerfLog and PerfLogAppLogger packages are available as free open source
community edition with Apache 2.0 License and a premium supported edition for
WebSphere environments. Customization services are also available from Pexus
LLC.  See PerfLog Edition section later in this document for more details.

PerfLog logging framework offers deep J2EE application performance insight
to application Architects, application developers and IT operations
team. Developers and Architects can extend the framework easily. It
can complement any monitoring solution in place for precise application
diagnosis and capturing performance metrics for SLA monitoring and performance
improvements.


What does PerfLog do?
---------------------

It captures key performance metrics and request and application contextual
data for the following type of J2EE requests:

		Servlet

		Portlet (WebSphere Portal 6.x+)

		Struts

		Web Service - JAX-RPC and JAX-WS

		JDBC - SQL queries and execution times

		Future enhancements will include EJB3 requests.

PerfLog use J2EE filter pattern to plug-in request interceptor classes without
changing application code. It can also be used within application code to
monitor custom transactions.  Each request or transaction from a thread is
assigned a globally unique identifier (Guid). Each request is associated
with the PerfLog context data that includes additional information such as
thread id, user id, request type, information context, request data, debug
trace data etc. Integration with application logger enables retrieving and
printing this contextual information for each application log entry. Full
PerfLog context data also can be dumped that can include the SQL queries
executed during the request along with application debug trace when request
response time exceeds a certain threshold.

In addition it can also be used within J2SE (Standalone Java) applications
to monitor custom transactions. PerfLog when used with application logger
can also provide additional contextual information to every log statement
and also caches request/thread specific data and application debug trace
for every request.  It can log and dump all  the contextual data for a
request/thread based on response time threshold. Threshold based logging
optimizes performance and at the same time provides insightful diagnostic
data when problems occur.

Persisting performance metric data to database and log files can be done
asynchronously thereby having no or very minimal impact to application
performance.  Buffer size properties enables fine tuning the memory and limits
memory used for caching the request and debug data. Note: Asynchronous thread
based logging requires J2EE environment. J2SE environment can use standard
out logging.  Additional logger implementation can be easily written to suit
any custom requirements.

PerfLog uses standard J2EE filter pattern to capture the performance metrics
along with user and application contextual data such as form data request
parameter data etc. without any change to application code. PerfLog filters
are configured via application configuration files such as web deployment
descriptors (web.xml), portlet deployment descriptors (portlet.xml),
Struts configuration file (struts-config.xml) and web services configuration
(webservice.xml). SQL queries and execution time are captured via JDBC data
source implementation class interceptor classes that can be easily configured
when defining JDBC provider configurations when defining J2EE data source. JDBC
interceptor classes for DB2, MySQL, Oracle and Derby Database are provided
in PerfLog package. Interceptors for other types of database can be easily
created using the same pattern. You will need access to the appropriate JDBC
driver to create SQL interceptors. Please refer to user guide documentation
for more information.



Multiple JVM Tracking
---------------------

PerfLog can track requests spanning multiple JVMs using a request GUID
(Globally Unique Identifier. Currently multiple JVM tracking is available
for Web Service calls - both JAX-RPC and JAX-WS. Future enhancements may
include tracking of remote JVM calls using remote EJB3 calls.

Extensibility
-------------

PerfLog is extensible and easily customizable. Custom performance metrics can
be captured and logged from application code in addition to the standard J2EE
requests and JDBC SQL queries. PerfLog filter code can be extended easily
to capture custom metrics or custom session data e.g. extracting userid
from  session data or application contextual information from session or
request data.

Integration with Application Loggers
------------------------------------

PerfLog can also easily integrate with your existing application logger
to enhance application logging by providing a request context and enabling
caching of debug traces on a request/thread basis which can be written to log
files only when request response time threshold are exceeded. The cache is
cleared when the request finishes executing thereby optimizing memory usage.
The included sample PerfLogAppLogger application logger is fully integrated
with PerfLog to demonstrate this integration. The integration is achieved
by a couple of lines of code.  PerfLogAppLogger can also be used as your
application logger as it is based of standard J2EE/J2SE API and will work
without any additional third party jars.

PerfLog diagnostic features capture, cache and log key request data such as
form data, request parameters, application debug trace, SOAP request  and
response and SQL queries. Diagnostic logging can be disabled or enabled
based on a specified request response threshold.

PerfLogAppLogger package is based of standard Java - java.util.logging.*
APIs.  If you are using WebSphere environment, loggers created using this
API enables changing log levels dynamically.

Application Diagnostics
-----------------------

Diagnostic logging and multiple JVM tracking feature via request GUID can
greatly enhance  diagnosis when used with log file scrapper tools such as
Splunk and Splunk alternatives.  Multiple logging implementations including
logging to file and database via asynchronous work manager threads, minimize
any overhead introduced by loggers. Performance log data can also be sent
to a central location via JMS queue logger implementation.

Runtime Environment Changes
---------------------------

PerfLog uses standard J2EE APIs and will work unchanged in all J2EE
environment.  It has been tested in Tomcat and IBM WebSphere environment.
However some minor change may be required for other J2EE Environment especially
to get the JVM instance name in a clustered environment. Retrieving the
JVM instance name is vendor dependent. The relevant code is abstracted
and can be implemented easily using a provided interface. Sample skeleton
implementations are provided for JBOSS, Geronimo, GlassFish, and  Weblogic
application servers. Use the runtimeEnv.properties within your application
to override default environment.

Property Files
--------------

PerfLog properties are defined in perfLogDefault.properties and
runtimeEnvDefault.properties. Applications including PerfLog jar can include
their own perfLog.properties and runtimeEnv.properties to override properties
defined in the perfLogDefault.properties and runtimeEnvDefault.properties
file. Properties can also be overridden using TunablePropertiesImplementation
at runtime that enables dynamic lookup of properties. This is extremely
useful when you may have to tune to certain log thresholds or enable or
disable properties based on your environment at runtime without re-deploying
your application.  Tunable properties can be defined either using WebSphere
Name Space Binding or via URL resource.  Dynamic properties are picked up at
regular intervals. The refresh interval is define in perfLogDefault.properties
or perfLog.properties.

PerfLogAppLogger includes perfLogAppLoggerDefault.properties as part of
the jar file.  Applications can easily override the default properties by
including perfLogAppLogger.properties in their application classpath by
including their custom version of the file.

PerfLog Editions
----------------

PerfLog is available as free open source edition and a premium supported
edition for IBM WebSphere Environment.

PerfLog Community Edition (CE)
------------------------------

The free open source edition comes with Apache 2.0 License and includes the
source code and binary jar file. Public Git repository are also available
from the following two locations:

	GitHub:
		Src Browse: https://github.com/pexus/PerfLog
		Clone: git://github.com/pexus/PerfLog.git
		https://github.com/pexus/PerfLog.git
		https://github.com/pexus/PerfLog/zipball/master

	Pexus Git Repository:
		Src Browse: http://git.pexus.net/perflog/
		Clone:	http://git.pexus.net/perflog
		git@git.pexus.net:perflog

	Binary Downloads:
		http://www.pexus.com/perflog

IBM WebSphere Supported Edition
-------------------------------

The supported edition for IBM WebSphere comes with WebSphere binary files,
documentation and full support and regular maintenance upgrades. Supported
edition is priced per JVM. Please visit http://www.pexus.com/perflog for
more details on pricing. Supported Edition also comes with sources for
customization if required.

PerfLog Customization Services
------------------------------

Pexus LLC also offers integration and customization services for integrating
PerfLog for other J2EE environments  and customer application.

Building PerfLog.jar from Sources
---------------------------------

The dependency list for PerfLog and PerfLogAppLogger project are given below
to help you in planning your build environment if you choose to build from
the sources.

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
		PerfLog.jar (dependent on PerfLog.jar) Standard
		Java Runtime jars J2EE Runtime jars - if using
		Asynchronous Logging via CommonJ Work Manager

You can always use the binary jars -  PerfLog.jar and PerfLogAppLogger.jar
if you intend to use them in your applications. Java docs and usage guide is
included in the documentation to help you use them in your application. You
will find the dependent jars in your J2EE environment.

When building PerfLog jar from the sources you will see dependency to the
following jars from the specified packages in addition to standard J2EE
libraries.  You will have to download the dependent jars or use the version
from your J2EE environment and include them in your build script,  if you
intend to use these packages and classes. When using the provided PerfLog.jar
binary in your application you will not need the depending jars unless you
decide to use the appropriate filters or interceptor classes.

	org.perf.log.filter.struts1
		struts.jar wp.struts.standard.framework.jar (WebSphere Portal)

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
performance data the DBWriter code has been tested with DB2, MySQL and
Oracle databases.


Documentation
-------------

Refer to included javadocs and documents on installing, configuring and using PerfLog jar.
PerfLogAppLogger project  shows how you can integrate PerfLog with your favorite 
application logger to capture debug trace data from your application code.

Sample Web Application and Web Service application with source code are
provided to show how you can configure and use the PerfLog package. The
PerfLogAppLogger APIs are also used in the sample Web (PerfLogTestWebApp)
and stand alone Java test application (PerfLogTestJavaApp)

Directory content in distribution archive
-----------------------------------------
	
	The latest distribution zip files can be downloaded from:
		http://www.pexus.com/perflog
	
	The directory content in the distribution archive are as follows: 

	Database/ 
		DDL for creating the perfDB database for persisting 
		performance log records 
	lib/ 
		binary jars for PerfLog.jar and PerfLogAppLogger.jar
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

Pexus LLC
 
	http://www.pexus.com
 
	http://www.pexus.com/perflog

Author

	Pradeep Nambiar, Pexus LLC
		  