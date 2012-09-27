package test;

public class HelloWorldProxy implements test.HelloWorld {
  private boolean _useJNDI = true;
  private boolean _useJNDIOnly = false;
  private String _endpoint = null;
  private test.HelloWorld __helloWorld = null;
  
  public HelloWorldProxy() {
    _initHelloWorldProxy();
  }
  
  private void _initHelloWorldProxy() {
  
    if (_useJNDI || _useJNDIOnly) {
      try {
        javax.naming.InitialContext ctx = new javax.naming.InitialContext();
        __helloWorld = ((test.HelloWorldServiceJaxRpcService)ctx.lookup("java:comp/env/service/HelloWorldServiceJaxRpcService")).getHelloWorldServiceJaxRpc();
      }
      catch (javax.naming.NamingException namingException) {
        if ("true".equalsIgnoreCase(System.getProperty("DEBUG_PROXY"))) {
          System.out.println("JNDI lookup failure: javax.naming.NamingException: " + namingException.getMessage());
          namingException.printStackTrace(System.out);
        }
      }
      catch (javax.xml.rpc.ServiceException serviceException) {
        if ("true".equalsIgnoreCase(System.getProperty("DEBUG_PROXY"))) {
          System.out.println("Unable to obtain port: javax.xml.rpc.ServiceException: " + serviceException.getMessage());
          serviceException.printStackTrace(System.out);
        }
      }
    }
    if (__helloWorld == null && !_useJNDIOnly) {
      try {
        __helloWorld = (new test.HelloWorldServiceJaxRpcServiceLocator()).getHelloWorldServiceJaxRpc();
        
      }
      catch (javax.xml.rpc.ServiceException serviceException) {
        if ("true".equalsIgnoreCase(System.getProperty("DEBUG_PROXY"))) {
          System.out.println("Unable to obtain port: javax.xml.rpc.ServiceException: " + serviceException.getMessage());
          serviceException.printStackTrace(System.out);
        }
      }
    }
    if (__helloWorld != null) {
      if (_endpoint != null)
        ((javax.xml.rpc.Stub)__helloWorld)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
      else
        _endpoint = (String)((javax.xml.rpc.Stub)__helloWorld)._getProperty("javax.xml.rpc.service.endpoint.address");
    }
    
  }
  
  
  public void useJNDI(boolean useJNDI) {
    _useJNDI = useJNDI;
    __helloWorld = null;
    
  }
  
  public void useJNDIOnly(boolean useJNDIOnly) {
    _useJNDIOnly = useJNDIOnly;
    __helloWorld = null;
    
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (__helloWorld == null)
      _initHelloWorldProxy();
    if (__helloWorld != null)
      ((javax.xml.rpc.Stub)__helloWorld)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public java.lang.String helloOperation(java.lang.String name) throws java.rmi.RemoteException{
    if (__helloWorld == null)
      _initHelloWorldProxy();
    return __helloWorld.helloOperation(name);
  }
  
  
  public test.HelloWorld getHelloWorld() {
    if (__helloWorld == null)
      _initHelloWorldProxy();
    return __helloWorld;
  }
  
}