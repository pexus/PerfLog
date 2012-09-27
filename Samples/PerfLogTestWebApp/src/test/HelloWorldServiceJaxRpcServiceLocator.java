/**
 * HelloWorldServiceJaxRpcServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the IBM Web services WSDL2Java emitter.
 * cf231216.04 v42612222534
 */

package test;

public class HelloWorldServiceJaxRpcServiceLocator extends com.ibm.ws.webservices.multiprotocol.AgnosticService implements com.ibm.ws.webservices.multiprotocol.GeneratedService, test.HelloWorldServiceJaxRpcService {

    public HelloWorldServiceJaxRpcServiceLocator() {
        super(com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
           "http://test",
           "HelloWorldServiceJaxRpcService"));

        context.setLocatorName("test.HelloWorldServiceJaxRpcServiceLocator");
    }

    public HelloWorldServiceJaxRpcServiceLocator(com.ibm.ws.webservices.multiprotocol.ServiceContext ctx) {
        super(ctx);
        context.setLocatorName("test.HelloWorldServiceJaxRpcServiceLocator");
    }

    // Use to get a proxy class for helloWorldServiceJaxRpc
    private final java.lang.String helloWorldServiceJaxRpc_address = "http://localhost:9080/HelloWorldJaxRpcWebServiceProject/services/HelloWorldServiceJaxRpc";

    public java.lang.String getHelloWorldServiceJaxRpcAddress() {
        if (context.getOverriddingEndpointURIs() == null) {
            return helloWorldServiceJaxRpc_address;
        }
        String overriddingEndpoint = (String) context.getOverriddingEndpointURIs().get("HelloWorldServiceJaxRpc");
        if (overriddingEndpoint != null) {
            return overriddingEndpoint;
        }
        else {
            return helloWorldServiceJaxRpc_address;
        }
    }

    private java.lang.String helloWorldServiceJaxRpcPortName = "HelloWorldServiceJaxRpc";

    // The WSDD port name defaults to the port name.
    private java.lang.String helloWorldServiceJaxRpcWSDDPortName = "HelloWorldServiceJaxRpc";

    public java.lang.String getHelloWorldServiceJaxRpcWSDDPortName() {
        return helloWorldServiceJaxRpcWSDDPortName;
    }

    public void setHelloWorldServiceJaxRpcWSDDPortName(java.lang.String name) {
        helloWorldServiceJaxRpcWSDDPortName = name;
    }

    public test.HelloWorld getHelloWorldServiceJaxRpc() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(getHelloWorldServiceJaxRpcAddress());
        }
        catch (java.net.MalformedURLException e) {
            return null; // unlikely as URL was validated in WSDL2Java
        }
        return getHelloWorldServiceJaxRpc(endpoint);
    }

    public test.HelloWorld getHelloWorldServiceJaxRpc(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        test.HelloWorld _stub =
            (test.HelloWorld) getStub(
                helloWorldServiceJaxRpcPortName,
                (String) getPort2NamespaceMap().get(helloWorldServiceJaxRpcPortName),
                test.HelloWorld.class,
                "test.HelloWorldServiceJaxRpcSoapBindingStub",
                portAddress.toString());
        if (_stub instanceof com.ibm.ws.webservices.engine.client.Stub) {
            ((com.ibm.ws.webservices.engine.client.Stub) _stub).setPortName(helloWorldServiceJaxRpcWSDDPortName);
        }
        return _stub;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (test.HelloWorld.class.isAssignableFrom(serviceEndpointInterface)) {
                return getHelloWorldServiceJaxRpc();
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("WSWS3273E: Error: There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        String inputPortName = portName.getLocalPart();
        if ("HelloWorldServiceJaxRpc".equals(inputPortName)) {
            return getHelloWorldServiceJaxRpc();
        }
        else  {
            throw new javax.xml.rpc.ServiceException();
        }
    }

    public void setPortNamePrefix(java.lang.String prefix) {
        helloWorldServiceJaxRpcWSDDPortName = prefix + "/" + helloWorldServiceJaxRpcPortName;
    }

    public javax.xml.namespace.QName getServiceName() {
        return com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://test", "HelloWorldServiceJaxRpcService");
    }

    private java.util.Map port2NamespaceMap = null;

    protected synchronized java.util.Map getPort2NamespaceMap() {
        if (port2NamespaceMap == null) {
            port2NamespaceMap = new java.util.HashMap();
            port2NamespaceMap.put(
               "HelloWorldServiceJaxRpc",
               "http://schemas.xmlsoap.org/wsdl/soap/");
        }
        return port2NamespaceMap;
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            String serviceNamespace = getServiceName().getNamespaceURI();
            for (java.util.Iterator i = getPort2NamespaceMap().keySet().iterator(); i.hasNext(); ) {
                ports.add(
                    com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                        serviceNamespace,
                        (String) i.next()));
            }
        }
        return ports.iterator();
    }

    public javax.xml.rpc.Call[] getCalls(javax.xml.namespace.QName portName) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            throw new javax.xml.rpc.ServiceException("WSWS3062E: Error: portName should not be null.");
        }
        if  (portName.getLocalPart().equals("HelloWorldServiceJaxRpc")) {
            return new javax.xml.rpc.Call[] {
                createCall(portName, "helloOperation", "helloOperationRequest"),
            };
        }
        else {
            throw new javax.xml.rpc.ServiceException("WSWS3062E: Error: portName should not be null.");
        }
    }
}
