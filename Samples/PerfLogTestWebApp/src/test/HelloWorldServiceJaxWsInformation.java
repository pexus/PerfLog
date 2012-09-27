/**
 * HelloWorldServiceJaxWsInformation.java
 *
 * This file was auto-generated from WSDL
 * by the IBM Web services WSDL2Java emitter.
 * cf231216.04 v42612222534
 */

package test;

public class HelloWorldServiceJaxWsInformation implements com.ibm.ws.webservices.multiprotocol.ServiceInformation {

    private static java.util.Map operationDescriptions;
    private static java.util.Map typeMappings;

    static {
         initOperationDescriptions();
         initTypeMappings();
    }

    private static void initOperationDescriptions() { 
        operationDescriptions = new java.util.HashMap();

        java.util.Map inner0 = new java.util.HashMap();

        java.util.List list0 = new java.util.ArrayList();
        inner0.put("helloOperation", list0);

        com.ibm.ws.webservices.engine.description.OperationDesc helloOperation0Op = _helloOperation0Op();
        list0.add(helloOperation0Op);

        java.util.List list1 = new java.util.ArrayList();
        inner0.put("main", list1);

        com.ibm.ws.webservices.engine.description.OperationDesc main1Op = _main1Op();
        list1.add(main1Op);

        operationDescriptions.put("HelloWorldPortJaxWs",inner0);
        operationDescriptions = java.util.Collections.unmodifiableMap(operationDescriptions);
    }

    private static com.ibm.ws.webservices.engine.description.OperationDesc _helloOperation0Op() {
        com.ibm.ws.webservices.engine.description.OperationDesc helloOperation0Op = null;
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
        helloOperation0Op = new com.ibm.ws.webservices.engine.description.OperationDesc("helloOperation", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://test/HelloWorldJaxWs", "helloOperation"), _params0, _returnDesc0, _faults0, null);
        helloOperation0Op.setOption("inoutOrderingReq","false");
        helloOperation0Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://test/HelloWorldJaxWs", "HelloWorldDelegate"));
        helloOperation0Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://test/HelloWorldJaxWs", "helloOperationResponse"));
        helloOperation0Op.setOption("ServiceQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://test/HelloWorldJaxWs", "HelloWorldServiceJaxWs"));
        helloOperation0Op.setOption("buildNum","cf231216.04");
        helloOperation0Op.setOption("ResponseNamespace","http://test/HelloWorldJaxWs");
        helloOperation0Op.setOption("targetNamespace","http://test/HelloWorldJaxWs");
        helloOperation0Op.setOption("ResponseLocalPart","helloOperationResponse");
        helloOperation0Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://test/HelloWorldJaxWs", "helloOperation"));
        helloOperation0Op.setStyle(com.ibm.ws.webservices.engine.enumtype.Style.WRAPPED);
        return helloOperation0Op;

    }

    private static com.ibm.ws.webservices.engine.description.OperationDesc _main1Op() {
        com.ibm.ws.webservices.engine.description.OperationDesc main1Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params0 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg0"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String[].class, false, false, false, true, false, false), 
          };
        _params0[0].setOption("inputPosition","0");
        _params0[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string[0,unbounded]");
        _params0[0].setOption("partName","string[0,unbounded]");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc0 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults0 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
          };
        main1Op = new com.ibm.ws.webservices.engine.description.OperationDesc("main", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://test/HelloWorldJaxWs", "main"), _params0, _returnDesc0, _faults0, null);
        main1Op.setOption("inoutOrderingReq","false");
        main1Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://test/HelloWorldJaxWs", "HelloWorldDelegate"));
        main1Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://test/HelloWorldJaxWs", "mainResponse"));
        main1Op.setOption("ServiceQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://test/HelloWorldJaxWs", "HelloWorldServiceJaxWs"));
        main1Op.setOption("buildNum","cf231216.04");
        main1Op.setOption("ResponseNamespace","http://test/HelloWorldJaxWs");
        main1Op.setOption("targetNamespace","http://test/HelloWorldJaxWs");
        main1Op.setOption("ResponseLocalPart","mainResponse");
        main1Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://test/HelloWorldJaxWs", "main"));
        main1Op.setStyle(com.ibm.ws.webservices.engine.enumtype.Style.WRAPPED);
        return main1Op;

    }


    private static void initTypeMappings() {
        typeMappings = new java.util.HashMap();
        typeMappings = java.util.Collections.unmodifiableMap(typeMappings);
    }

    public java.util.Map getTypeMappings() {
        return typeMappings;
    }

    public Class getJavaType(javax.xml.namespace.QName xmlName) {
        return (Class) typeMappings.get(xmlName);
    }

    public java.util.Map getOperationDescriptions(String portName) {
        return (java.util.Map) operationDescriptions.get(portName);
    }

    public java.util.List getOperationDescriptions(String portName, String operationName) {
        java.util.Map map = (java.util.Map) operationDescriptions.get(portName);
        if (map != null) {
            return (java.util.List) map.get(operationName);
        }
        return null;
    }

}
