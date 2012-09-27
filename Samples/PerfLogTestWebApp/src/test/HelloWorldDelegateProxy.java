package test;

public class HelloWorldDelegateProxy implements test.HelloWorldDelegate {
  private boolean _useJNDI = true;
  private boolean _useJNDIOnly = false;
  private String _endpoint = null;
  private test.HelloWorldDelegate __helloWorldDelegate = null;
  
  public HelloWorldDelegateProxy() {
    _initHelloWorldDelegateProxy();
  }
  
  private void _initHelloWorldDelegateProxy() {
  
    if (_useJNDI || _useJNDIOnly) {
      try {
        javax.naming.InitialContext ctx = new javax.naming.InitialContext();
        __helloWorldDelegate = ((test.HelloWorldServiceJaxWs)ctx.lookup("java:comp/env/service/HelloWorldServiceJaxWs")).getHelloWorldPortJaxWs();
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
    if (__helloWorldDelegate == null && !_useJNDIOnly) {
      try {
        __helloWorldDelegate = (new test.HelloWorldServiceJaxWsLocator()).getHelloWorldPortJaxWs();
        
      }
      catch (javax.xml.rpc.ServiceException serviceException) {
        if ("true".equalsIgnoreCase(System.getProperty("DEBUG_PROXY"))) {
          System.out.println("Unable to obtain port: javax.xml.rpc.ServiceException: " + serviceException.getMessage());
          serviceException.printStackTrace(System.out);
        }
      }
    }
    if (__helloWorldDelegate != null) {
      if (_endpoint != null)
        ((javax.xml.rpc.Stub)__helloWorldDelegate)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
      else
        _endpoint = (String)((javax.xml.rpc.Stub)__helloWorldDelegate)._getProperty("javax.xml.rpc.service.endpoint.address");
    }
    
  }
  
  
  public void useJNDI(boolean useJNDI) {
    _useJNDI = useJNDI;
    __helloWorldDelegate = null;
    
  }
  
  public void useJNDIOnly(boolean useJNDIOnly) {
    _useJNDIOnly = useJNDIOnly;
    __helloWorldDelegate = null;
    
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (__helloWorldDelegate == null)
      _initHelloWorldDelegateProxy();
    if (__helloWorldDelegate != null)
      ((javax.xml.rpc.Stub)__helloWorldDelegate)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public java.lang.String helloOperation(java.lang.String arg0) throws java.rmi.RemoteException{
    if (__helloWorldDelegate == null)
      _initHelloWorldDelegateProxy();
    return __helloWorldDelegate.helloOperation(arg0);
  }
  
  public void main(java.lang.String[] arg0) throws java.rmi.RemoteException{
    if (__helloWorldDelegate == null)
      _initHelloWorldDelegateProxy();
    __helloWorldDelegate.main(arg0);
  }
  
  
  public test.HelloWorldDelegate getHelloWorldDelegate() {
    if (__helloWorldDelegate == null)
      _initHelloWorldDelegateProxy();
    return __helloWorldDelegate;
  }
  
}