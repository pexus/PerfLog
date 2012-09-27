		Pexus PerfLog - J2EE Performance and Diagnostics Logging Framework
		------------------------------------------------------------------

What is PerfLog 
---------------------
PerfLog is a J2EE performance and diagnostic logging framework. 
It is NOT an application logger framework like log4j or JDK logger, 
but it complements any application logger framework by easily integrating 
with any application logger and at the same time enables capturing of 
key request performance metrics with very minimal or no overhead. PerfLog use
J2EE filter pattern to plug-in request interceptors without changing application
code. It can also be used within the code to monitor custom transactions.
Each request or transactions from a thread is  assigned a globally 
unique identifier (Guid). Each thread/request is associated with the
PerfLog context data that includes additional information such as thread id,
user id, request type, information context, request data, debug trace data
etc. Integration with application logger enables retrieving and printing
this contextual information along with application logs. Full PerfLog context data
also can be dumped that can include the SQL queries executed during the request
along with application debug trace when request response time exceeds a certain
threshold.

PerfLog logging framework offers deep J2EE application performance insight  to application Architects,
application developers and IT operations team. Developers and Architects can extend the 
framework easily. It can complement any monitoring solution or even the basic PMI monitoring 
available in all J2EE servers.


What does PerfLog do
--------------------
It helps in capturing key performance time and contextual data for the following type 
of J2EE requests:
	1. Servlet 
	2. Portlet (WebSphere Portal 6.x+)
	3. Struts
	4. Web Service - JAX-RPC and JAX-WS
	5. JDBC - SQL queries and execution times
Future enhancements will include EJB3 requests.

In addition it can also be used within J2SE (Standalone Java) applications to 
monitor custom transactions. PerfLog when used with application logger can also 
provide additional contextual information to every log statement and also caches
request/thread specific data and application debug trace for every request. 
It can log and dump all  the contextual data for a request/thread based on 
response time threshold. Threshold based logging optimizes performance and 
at the same time provides insightful diagnostic data when problems occur.

Persisting performance metric data to database and log files can be done 
asynchronously thereby having no or very minimal impact to application performance.
Buffer size properties enables fine tuning the memory and limits memory 
used for caching the request and debug data. Note: Asynchronous thread based
logging requires J2EE environment. J2SE environment will use standard out logging.
Additional logger implementation can be easily written to suit any custom 
requirements.


PerfLog uses standard J2EE filter pattern to capture the performance
metrics along with user and application contextual data such as form data
request parameters etc. without any change to application code. PerfLog 
filters are configured via application configuration files such as web 
deployment descriptors (web.xml), portlet deployment descriptors (portlet.xml), 
Struts configuration file (struts-config.xml) and web services 
configuration (webservice.xml). SQL queries and execution time are 
captured via JDBC data source implementation class interceptor classes
that can be easily configured when defining JDBC provider configurations
when defining J2EE data source. JDBC interceptor classes for DB2, MySQL, 
Oracle and Derby Database are provided. Interceptors for other types of 
database can be easily created using the same pattern. You will need access
to the appropriate JDBC driver to create SQL interceptors. Please refer to 
user guide documentation for more information.

Multiple JVM Tracking
---------------------
PerfLog can track requests spanning multiple JVMs using a request GUID
(Globally Unique Identifier. Currently multiple JVM tracking is available
for Web Service calls. Future enhancements may include tracking of remote
JVM calls using remote EJB3 calls. 

Extensibility
--------------
PerfLog is extensible and easily customizable. Custom performance metrics
can be captured and logged from application code in addition to the above
request metrics. PerfLog filter code can be extended easily to 
capture custom metrics or custom session data e.g. extracting userid
from  session data or application contextual information from session 
or request data.

Integration with Application Logger
-----------------------------------	
PerfLog also plugs into any application logger and provides diagnostic
information for J2EE requests based on a set threshold. This feature can
capture, cache and log key request data such as form data, request 
parameters, application debug trace, SOAP request  and response and 
SQL queries. Diagnostic logging can be disabled or enabled based on  
a specified request response threshold. 

Application Diagnostics
------------------------
Diagnostic logging and multiple JVM tracking feature via request GUID 
can greatly enhance  diagnosis when used with log file scrapper tools 
such as Splunk and Splunk alternatives.  Multiple logging implementations 
including logging to file and database via asynchronous work manager 
threads, minimize any overhead introduced by loggers. Performance log 
data can also be sent to a central location via JMS queue logger implementation.

Runtime Environment Changes
--------------------------
PerfLog uses standard J2EE APIs and will work unchanged in all J2EE environment.
It has been tested in Tomcat and IBM WebSphere environment.
However some minor change may be required for other J2EE Environment especially 
to get the JVM instance name in a clustered environment. Retrieving the JVM instance
name is vendor dependent. The relevant code is abstracted and can be implemented 
easily using a provided interface. Sample skeleton implementations are provided 
for JBOSS, Geronimo, GlassFish, and  Weblogic  application servers. Use the 
runtimeEnv.properties within your application to override default environment.

Property Files
---------------
PerfLog properties are defined in perfLogDefault.properties and 
runtimeEnvDefault.properties. Applications including PerfLog jar can 
include their own perfLog.properties and runtimeEnv.properties to override 
properties defined in the perfLogDefault.properties and runtimeEnvDefault.properties
file. Properties can also be overridden using TunablePropertiesImplementation 
at runtime that enables dynamic lookup of properties. This is extremely useful 
when you may have to tune to certain log thresholds or enable or disable properties 
based on your environment at runtime without re-deploying your application. 
Tunable properties can be defined either using WebSphere Name Space Binding or via URL resource.
Dynamic properties are picked up at regular intervals. The refresh interval is 
define in perfLogDefault.properties or perfLog.properties.

PerfLog Editions
-----------------
PerfLog is available as free open source edition and a premium supported
edition for IBM WebSphere Environment.

PerfLog Community Edition (CE)
------------------------------
The free open source edition comes with Apache 2.0 License and includes the source 
code and binary jar file.

IBM WebSphere Supported Edition
--------------------------------
The supported edition for IBM WebSphere comes with WebSphere binary files, 
documentation and full support and regular maintenance upgrades. Supported 
edition is priced per JVM. Please visit http://www.pexus.com/perflog for 
more details on pricing. Supported Edition also comes with sources for customization
if required.

PerfLog Customization Services
-------------------------------
Pexus LLC also offers integration and customization services for 
integrating PerfLog for other J2EE environments  and customer application.

Building PerfLog.jar from Sources
---------------------------------
When building PerfLog jar from the sources you will see dependency to the following
jars from the specified packages in addition to standard J2EE libraries.
You will have download and include them into your build if you intend to use these
packages/classes. When using the provided PerfLog.jar binary in your application you will 
not need the depending jars unless you decide to use the appropriate filters or
interceptors.

	struts.jar  - org.perf.log.filter.struts1
	
	wp.base.jar
	wp.model.api.jar
	wp.struts.standard.framework.jar (WebSphere Portal) - org.perf.log.filter.portal
	
	catalina.jar (Tomcat) -org.perf.log.utils.TomcatJvmCloneGetterImp.java
	 
	db2jcc.jar (DB2 JDBC driver) - org.perf.log.filter.sql.DB2*
	
	ojdbc6.jar (Oracle JDBC driver)- org.perf.log.filter.sql.Oracle*
	
	derby.jar (Derby JDBC Driver) - org.perf.log.filter.sql.Derby*
	
	mysql-connector-java-5.1.21-bin.jar - org.perf.log.filter.sql.MySQL*
	 
When compiling from sources, If you don't intend to use the above filters or interceptors
you can remove them from your build easily to avoid compile time errors.

PerfLog has been tested with Tomcat 7.x and IBM WebSphere 6/7/8 Environment and
with DB2, MySQL and Oracle Database and Derby databses.
For storing performance data the DBWriter code has been tested with DB2, MySQL and
Oracle databases.



Documentation
-------------
Refer to included document on installing, configuring and using PerfLog jar.
A sample application logger using JDK java.util.logging.* is also provided that
shows how you can integrate PerfLog with your favorite application logger
to capture debug trace data from your application code.

Directory Files
---------------
Database/ - DDL for creating the perfDB database for persisting performance log records
lib/ - binary jars for PerfLog.jar and PerfLogAppLogger.jar
PerfLog/ - source for PerfLog framework
PerfLogAppLogger/ - Sample application logger using IBM JDK logger enabled to use PerfLog framework
PerfLogMDB/ - Optional Message Driven Bean (MDB) for PerfLog framework that uses JMS Queue implementation
Samples/ - Sample Web and stand alone Java application to demonstrate PerfLog framework

 

For more information visit http://www.pexus.com/perflog

Pexus LLC
http://www.pexus.com

		  