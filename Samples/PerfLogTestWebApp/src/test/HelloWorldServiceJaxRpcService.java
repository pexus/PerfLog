/**
 * HelloWorldServiceJaxRpcService.java
 *
 * This file was auto-generated from WSDL
 * by the IBM Web services WSDL2Java emitter.
 * cf231216.04 v42612222534
 */

package test;

public interface HelloWorldServiceJaxRpcService extends javax.xml.rpc.Service {
    public test.HelloWorld getHelloWorldServiceJaxRpc() throws javax.xml.rpc.ServiceException;

    public java.lang.String getHelloWorldServiceJaxRpcAddress();

    public test.HelloWorld getHelloWorldServiceJaxRpc(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
