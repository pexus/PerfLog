package test;

import java.util.Date;


@javax.jws.WebService (targetNamespace="http://test/HelloWorldJaxWs", serviceName="HelloWorldServiceJaxWs", portName="HelloWorldPortJaxWs", wsdlLocation="WEB-INF/wsdl/HelloWorldService.wsdl")
public class HelloWorldDelegate{

    test.HelloWorld _helloWorld = null;

    public HelloWorldDelegate() {
        _helloWorld = new test.HelloWorld(); }

    public String helloOperation(String name) {
        return _helloWorld.helloOperation(name);
    }

    public void main(String[] args) {
        _helloWorld.main(args);
    }

}