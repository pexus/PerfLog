/**
 * HelloWorldServiceJaxWs.java
 *
 * This file was auto-generated from WSDL
 * by the IBM Web services WSDL2Java emitter.
 * cf231216.04 v42612222534
 */

package test;

public interface HelloWorldServiceJaxWs extends javax.xml.rpc.Service {
    public test.HelloWorldDelegate getHelloWorldPortJaxWs() throws javax.xml.rpc.ServiceException;

    public java.lang.String getHelloWorldPortJaxWsAddress();

    public test.HelloWorldDelegate getHelloWorldPortJaxWs(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
