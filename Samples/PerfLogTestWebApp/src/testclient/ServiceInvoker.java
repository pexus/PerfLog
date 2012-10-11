/*******************************************************************************
 * Copyright 2012 Pradeep Nambiar,  Pexus LLC
 * 
 * Source File: src/testclient/ServiceInvoker.java 
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
package testclient;

import java.io.IOException;
import java.net.URL;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.perf.log.app.logger.Logger;
import org.perf.log.app.logger.LoggerFactory;
import org.perf.log.context.PerfLogContextHelper;
import org.perf.log.context.TxnData;
import test.HelloWorld;
import test.HelloWorldDelegate;
import test.HelloWorldServiceJaxRpcService;
import test.HelloWorldServiceJaxWs;




/**
 * Servlet implementation class ServiceInvoker
 * This servlet is used to demonstrate JAX-RPC and JAX-WS Web Service invocation 
 * with PerfLog instrumentation
 */
public class ServiceInvoker extends HttpServlet {
	// Get instance of sample Application logger
	private static Logger logger = LoggerFactory.getLogger("ServiceInvoker");
	
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ServiceInvoker() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		// Creating a custom performance record...
		TxnData txnData = new TxnData();
		txnData.setTxnName("MyCustomTransactionName");
		txnData.setSubTxnName("MyCustomSubTransactionName");
		txnData.setTxnType("MyCustomTransactionType");
		txnData.setTxnClass("MyCustomTransactionClass");
		// Start my Transaction monitoring... 
		PerfLogContextHelper.startPerfLogTxnMonitor(txnData);
		
		String host = "localhost";
		String port = "9081";
		if(request.getParameter("host") != null)
				host = request.getParameter("host");
		if(request.getParameter("port") != null)
				port = request.getParameter("port");
		
		// Add some context to PerfLog
		PerfLogContextHelper.pushInfoContext("host="+host);
		PerfLogContextHelper.pushInfoContext("port="+port);
		PerfLogContextHelper.pushInfoContext("MyCustomContextName=MyCustomContextValue");
		
		logger.info("In doGet()");
		
		
		testutils.HtmlUtils.printResponsePrologue(request, response);
		
		// Invoking JAX-PRC Web Service from one JVM to another JVM
		// ------------------------------------------------------
		String serviceName = "service/HelloWorldServiceJaxRpcService";
		HelloWorldServiceJaxRpcService helloWorldService = null;
		HelloWorld helloWorld = null;
		String helloWorldOperationResponse = null;
		// end point of web service in JVM2
		String specificURL = "http://"+host+":"+port+"/HelloWorldJaxRpcWebServiceProject/services/HelloWorldServiceJaxRpc";
		
		logger.debug("specificURL = "+specificURL);

		try {
			helloWorldService = (HelloWorldServiceJaxRpcService) locateService(serviceName);

			// Ovveride Endpoint
			helloWorld = helloWorldService.getHelloWorldServiceJaxRpc(new URL(specificURL));
			testutils.HtmlUtils
					.printResponseLine(
							request,
							response,
							"Invoking JAX-RPC Web Service from JVM1 to JVM2 for demostrating PerfLog JVM Tracking");
			testutils.HtmlUtils.printResponseLine(request,response,"target Web Service URL:"+specificURL);
			helloWorldOperationResponse = helloWorld
					.helloOperation("HelloWorldService:");
			testutils.HtmlUtils.printResponseLine(request, response,"Response from JAX-RPC Web Service Operation:"+ helloWorldOperationResponse);
			
			testutils.HtmlUtils.printResponseLine(request, response,
					"Successfully invoked JAX-RPC Web Service from JVM1...");
			testutils.HtmlUtils.printResponseLine(request, response, testutils.HtmlUtils.getHRule());


		} catch (NamingException e) {

			logger.error(e.getMessage(), e);
			testutils.HtmlUtils.printResponseLine(request, response,
					"Naming Exception getting service" + serviceName);
		} catch (Exception e) {

			logger.error(e.getMessage(), e);
			testutils.HtmlUtils.printResponseLine(request, response,
					"Exception: " + e.getMessage());
		}


		// ====================================================================================
		try {

			// Invoking JAX-WS Web Service from one JVM to another JVM
			// ------------------------------------------------------
			serviceName = "service/HelloWorldServiceJaxWs";
			HelloWorldServiceJaxWs  helloWorldServiceJAXWS = (HelloWorldServiceJaxWs) locateService(serviceName);
			specificURL = "http://"+host+":"+port+"/HelloWorldJaxWsWebServiceProject/HelloWorldServiceJaxWs";
			HelloWorldDelegate 	helloWorldDelegate = helloWorldServiceJAXWS.getHelloWorldPortJaxWs(new URL(specificURL));		
								
			testutils.HtmlUtils.printResponseLine(request,response,
					"Now Invoking JAX-WS Web Service from JVM1 to JVM2 for demostrating PerfLog JVM Tracking");
			testutils.HtmlUtils.printResponseLine(request,response,"target Web Service URL:"+specificURL);
			helloWorldOperationResponse = helloWorldDelegate.helloOperation("HelloWorldServiceJAXWS:");

			testutils.HtmlUtils.printResponseLine(request, response,"Response from Web Service Operation:"+ helloWorldOperationResponse);
			testutils.HtmlUtils.printResponseLine(request, response,
					"Successfully invoked JAX-WS Web Service from JVM1...");
			testutils.HtmlUtils.printResponseLine(request, response, testutils.HtmlUtils.getHRule());

		} catch (NamingException e) {
			logger.error(e.getMessage(), e);
			testutils.HtmlUtils.printResponseLine(request, response,
					"Naming Exception getting service" + serviceName);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			testutils.HtmlUtils.printResponseLine(request, response,
					"Exception: " + e.getMessage());
		}
		finally {
			testutils.HtmlUtils.printResponseEpilogue(this,request,response);
			// End my Transaction monitoring and log my performance data			
			PerfLogContextHelper.endPerfLogTxnMonitor(true);
			
			
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

	/*
	 * Simple service locator Note - the service proxy class generated by wsgen
	 * ant script provided by RAD/WebSphere SDK already does a JNDI lookup by
	 * default using the service reference entries created by the script. This
	 * code is only for demonstration.
	 */
	protected Object locateService(String serviceName) throws NamingException {
		logger.info("lookupService: serviceName = " + serviceName);
		InitialContext jndiContext = null;
		try {
			jndiContext = new InitialContext();
			// Lookup my service via the reference as you would an EJB service
			Object serviceInterface = jndiContext.lookup("java:comp/env/"
					+ serviceName);
			return serviceInterface;
		} catch (NamingException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jndiContext != null)
				jndiContext.close();
		}
	}

}
