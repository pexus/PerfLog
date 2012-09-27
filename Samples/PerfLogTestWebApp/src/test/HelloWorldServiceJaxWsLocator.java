/**
 * HelloWorldServiceJaxWsLocator.java
 *
 * This file was auto-generated from WSDL
 * by the IBM Web services WSDL2Java emitter.
 * cf231216.04 v42612222534
 */

package test;

public class HelloWorldServiceJaxWsLocator extends com.ibm.ws.webservices.multiprotocol.AgnosticService implements com.ibm.ws.webservices.multiprotocol.GeneratedService, test.HelloWorldServiceJaxWs {

    public HelloWorldServiceJaxWsLocator() {
        super(com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
           "http://test/HelloWorldJaxWs",
           "HelloWorldServiceJaxWs"));

        context.setLocatorName("test.HelloWorldServiceJaxWsLocator");
    }

    public HelloWorldServiceJaxWsLocator(com.ibm.ws.webservices.multiprotocol.ServiceContext ctx) {
        super(ctx);
        context.setLocatorName("test.HelloWorldServiceJaxWsLocator");
    }

    // Use to get a proxy class for helloWorldPortJaxWs
    private final java.lang.String helloWorldPortJaxWs_address = "http://localhost:9080/HelloWorldJaxWsWebServiceProject/HelloWorldServiceJaxWs";

    public java.lang.String getHelloWorldPortJaxWsAddress() {
        if (context.getOverriddingEndpointURIs() == null) {
            return helloWorldPortJaxWs_address;
        }
        String overriddingEndpoint = (String) context.getOverriddingEndpointURIs().get("HelloWorldPortJaxWs");
        if (overriddingEndpoint != null) {
            return overriddingEndpoint;
        }
        else {
            return helloWorldPortJaxWs_address;
        }
    }

    private java.lang.String helloWorldPortJaxWsPortName = "HelloWorldPortJaxWs";

    // The WSDD port name defaults to the port name.
    private java.lang.String helloWorldPortJaxWsWSDDPortName = "HelloWorldPortJaxWs";

    public java.lang.String getHelloWorldPortJaxWsWSDDPortName() {
        return helloWorldPortJaxWsWSDDPortName;
    }

    public void setHelloWorldPortJaxWsWSDDPortName(java.lang.String name) {
        helloWorldPortJaxWsWSDDPortName = name;
    }

    public test.HelloWorldDelegate getHelloWorldPortJaxWs() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(getHelloWorldPortJaxWsAddress());
        }
        catch (java.net.MalformedURLException e) {
            return null; // unlikely as URL was validated in WSDL2Java
        }
        return getHelloWorldPortJaxWs(endpoint);
    }

    public test.HelloWorldDelegate getHelloWorldPortJaxWs(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        test.HelloWorldDelegate _stub =
            (test.HelloWorldDelegate) getStub(
                helloWorldPortJaxWsPortName,
                (String) getPort2NamespaceMap().get(helloWorldPortJaxWsPortName),
                test.HelloWorldDelegate.class,
                "test.HelloWorldPortJaxWsBindingStub",
                portAddress.toString());
        if (_stub instanceof com.ibm.ws.webservices.engine.client.Stub) {
            ((com.ibm.ws.webservices.engine.client.Stub) _stub).setPortName(helloWorldPortJaxWsWSDDPortName);
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
            if (test.HelloWorldDelegate.class.isAssignableFrom(serviceEndpointInterface)) {
                return getHelloWorldPortJaxWs();
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
        if ("HelloWorldPortJaxWs".equals(inputPortName)) {
            return getHelloWorldPortJaxWs();
        }
        else  {
            throw new javax.xml.rpc.ServiceException();
        }
    }

    public void setPortNamePrefix(java.lang.String prefix) {
        helloWorldPortJaxWsWSDDPortName = prefix + "/" + helloWorldPortJaxWsPortName;
    }

    public javax.xml.namespace.QName getServiceName() {
        return com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://test/HelloWorldJaxWs", "HelloWorldServiceJaxWs");
    }

    private java.util.Map port2NamespaceMap = null;

    protected synchronized java.util.Map getPort2NamespaceMap() {
        if (port2NamespaceMap == null) {
            port2NamespaceMap = new java.util.HashMap();
            port2NamespaceMap.put(
               "HelloWorldPortJaxWs",
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
        if  (portName.getLocalPart().equals("HelloWorldPortJaxWs")) {
            return new javax.xml.rpc.Call[] {
                createCall(portName, "helloOperation", "null"),
                createCall(portName, "main", "null"),
            };
        }
        else {
            throw new javax.xml.rpc.ServiceException("WSWS3062E: Error: portName should not be null.");
        }
    }
}
