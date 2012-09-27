/**
 * HelloWorldPortJaxWsBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the IBM Web services WSDL2Java emitter.
 * cf231216.04 v42612222534
 */

package test;

public class HelloWorldPortJaxWsBindingStub extends com.ibm.ws.webservices.engine.client.Stub implements test.HelloWorldDelegate {
    public HelloWorldPortJaxWsBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws com.ibm.ws.webservices.engine.WebServicesFault {
        if (service == null) {
            super.service = new com.ibm.ws.webservices.engine.client.Service();
        }
        else {
            super.service = service;
        }
        super.engine = ((com.ibm.ws.webservices.engine.client.Service) super.service).getEngine();
        super.messageContexts = new com.ibm.ws.webservices.engine.MessageContext[2];
        java.lang.String theOption = (java.lang.String)super._getProperty("lastStubMapping");
        if (theOption == null || !theOption.equals("test.HelloWorldPortJaxWsBinding")) {
                initTypeMapping();
                super._setProperty("lastStubMapping","test.HelloWorldPortJaxWsBinding");
        }
        super.cachedEndpoint = endpointURL;
        super.connection = ((com.ibm.ws.webservices.engine.client.Service) super.service).getConnection(endpointURL);
    }

    private void initTypeMapping() {
        javax.xml.rpc.encoding.TypeMapping tm = super.getTypeMapping(com.ibm.ws.webservices.engine.Constants.URI_LITERAL_ENC);
        java.lang.Class javaType = null;
        javax.xml.namespace.QName xmlType = null;
        javax.xml.namespace.QName compQName = null;
        javax.xml.namespace.QName compTypeQName = null;
        com.ibm.ws.webservices.engine.encoding.SerializerFactory sf = null;
        com.ibm.ws.webservices.engine.encoding.DeserializerFactory df = null;
    }

    private static com.ibm.ws.webservices.engine.description.OperationDesc _helloOperationOperation0 = null;
    private static com.ibm.ws.webservices.engine.description.OperationDesc _gethelloOperationOperation0() {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params0 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg0"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, true, true, false), 
          };
        _params0[0].setOption("inputPosition","0");
        _params0[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params0[0].setOption("partName","string");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc0 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "return"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, true, false, false, true, true, false); 
        _returnDesc0.setOption("outputPosition","0");
        _returnDesc0.setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _returnDesc0.setOption("partName","string");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults0 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
          };
        _helloOperationOperation0 = new com.ibm.ws.webservices.engine.description.OperationDesc("helloOperation", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://test/HelloWorldJaxWs", "helloOperation"), _params0, _returnDesc0, _faults0, "");
        _helloOperationOperation0.setOption("inoutOrderingReq","false");
        _helloOperationOperation0.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://test/HelloWorldJaxWs", "HelloWorldDelegate"));
        _helloOperationOperation0.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://test/HelloWorldJaxWs", "helloOperationResponse"));
        _helloOperationOperation0.setOption("ServiceQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://test/HelloWorldJaxWs", "HelloWorldServiceJaxWs"));
        _helloOperationOperation0.setOption("buildNum","cf231216.04");
        _helloOperationOperation0.setOption("ResponseNamespace","http://test/HelloWorldJaxWs");
        _helloOperationOperation0.setOption("targetNamespace","http://test/HelloWorldJaxWs");
        _helloOperationOperation0.setOption("ResponseLocalPart","helloOperationResponse");
        _helloOperationOperation0.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://test/HelloWorldJaxWs", "helloOperation"));
        _helloOperationOperation0.setUse(com.ibm.ws.webservices.engine.enumtype.Use.LITERAL);
        _helloOperationOperation0.setStyle(com.ibm.ws.webservices.engine.enumtype.Style.WRAPPED);
        return _helloOperationOperation0;

    }

    private int _helloOperationIndex0 = 0;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _gethelloOperationInvoke0(Object[] parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext mc = super.messageContexts[_helloOperationIndex0];
        if (mc == null) {
            mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            mc.setOperation(HelloWorldPortJaxWsBindingStub._helloOperationOperation0);
            mc.setUseSOAPAction(true);
            mc.setSOAPActionURI("");
            mc.setEncodingStyle(com.ibm.ws.webservices.engine.Constants.URI_LITERAL_ENC);
            mc.setProperty(com.ibm.wsspi.webservices.Constants.SEND_TYPE_ATTR_PROPERTY, Boolean.FALSE);
            mc.setProperty(com.ibm.wsspi.webservices.Constants.ENGINE_DO_MULTI_REFS_PROPERTY, Boolean.FALSE);
            super.primeMessageContext(mc);
            super.messageContexts[_helloOperationIndex0] = mc;
        }
        try {
            mc = (com.ibm.ws.webservices.engine.MessageContext) mc.clone();
        }
        catch (CloneNotSupportedException cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, mc, parameters);
    }

    public java.lang.String helloOperation(java.lang.String arg0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        java.util.Vector _resp = null;
        try {
            _resp = _gethelloOperationInvoke0(new java.lang.Object[] {arg0}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault wsf) {
            Exception e = wsf.getUserException();
            throw wsf;
        } 
        try {
            return (java.lang.String) ((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue();
        } catch (java.lang.Exception _exception) {
            return (java.lang.String) super.convert(((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue(), java.lang.String.class);
        }
    }

    private static com.ibm.ws.webservices.engine.description.OperationDesc _mainOperation1 = null;
    private static com.ibm.ws.webservices.engine.description.OperationDesc _getmainOperation1() {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params1 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg0"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String[].class, false, false, false, true, false, false), 
          };
        _params1[0].setOption("inputPosition","0");
        _params1[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string[0,unbounded]");
        _params1[0].setOption("partName","string[0,unbounded]");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc1 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults1 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
          };
        _mainOperation1 = new com.ibm.ws.webservices.engine.description.OperationDesc("main", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://test/HelloWorldJaxWs", "main"), _params1, _returnDesc1, _faults1, "");
        _mainOperation1.setOption("inoutOrderingReq","false");
        _mainOperation1.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://test/HelloWorldJaxWs", "HelloWorldDelegate"));
        _mainOperation1.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://test/HelloWorldJaxWs", "mainResponse"));
        _mainOperation1.setOption("ServiceQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://test/HelloWorldJaxWs", "HelloWorldServiceJaxWs"));
        _mainOperation1.setOption("buildNum","cf231216.04");
        _mainOperation1.setOption("ResponseNamespace","http://test/HelloWorldJaxWs");
        _mainOperation1.setOption("targetNamespace","http://test/HelloWorldJaxWs");
        _mainOperation1.setOption("ResponseLocalPart","mainResponse");
        _mainOperation1.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://test/HelloWorldJaxWs", "main"));
        _mainOperation1.setUse(com.ibm.ws.webservices.engine.enumtype.Use.LITERAL);
        _mainOperation1.setStyle(com.ibm.ws.webservices.engine.enumtype.Style.WRAPPED);
        return _mainOperation1;

    }

    private int _mainIndex1 = 1;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getmainInvoke1(Object[] parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext mc = super.messageContexts[_mainIndex1];
        if (mc == null) {
            mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            mc.setOperation(HelloWorldPortJaxWsBindingStub._mainOperation1);
            mc.setUseSOAPAction(true);
            mc.setSOAPActionURI("");
            mc.setEncodingStyle(com.ibm.ws.webservices.engine.Constants.URI_LITERAL_ENC);
            mc.setProperty(com.ibm.wsspi.webservices.Constants.SEND_TYPE_ATTR_PROPERTY, Boolean.FALSE);
            mc.setProperty(com.ibm.wsspi.webservices.Constants.ENGINE_DO_MULTI_REFS_PROPERTY, Boolean.FALSE);
            super.primeMessageContext(mc);
            super.messageContexts[_mainIndex1] = mc;
        }
        try {
            mc = (com.ibm.ws.webservices.engine.MessageContext) mc.clone();
        }
        catch (CloneNotSupportedException cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, mc, parameters);
    }

    public void main(java.lang.String[] arg0) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        try {
            _getmainInvoke1(new java.lang.Object[] {arg0}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault wsf) {
            Exception e = wsf.getUserException();
            throw wsf;
        } 
    }

    private static void _staticInit() {
        _mainOperation1 = _getmainOperation1();
        _helloOperationOperation0 = _gethelloOperationOperation0();
    }

    static {
       _staticInit();
    }
}
