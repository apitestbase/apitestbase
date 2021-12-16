package io.apitestbase.resources;

import com.predic8.wsdl.Binding;
import com.predic8.wsdl.BindingOperation;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.WSDLParser;
import com.predic8.wstool.creator.RequestTemplateCreator;
import com.predic8.wstool.creator.SOARequestCreator;
import groovy.xml.MarkupBuilder;
import io.apitestbase.core.SSLTrustedExternalResolver;
import io.apitestbase.models.WSDLBinding;
import io.apitestbase.models.teststep.SOAPOperationInfo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

@Path("/wsdls") @Produces({ MediaType.APPLICATION_JSON })
public class WSDLResource {
    public WSDLResource() {}

    @GET @Path("/{wsdlUrl}/bindings")
    public List<WSDLBinding> getWSDLBindings(@PathParam("wsdlUrl") String wsdlUrl) {
        List<WSDLBinding> result = new ArrayList<>();
        WSDLParser parser = new WSDLParser();
        parser.setResourceResolver(new SSLTrustedExternalResolver());
        Definitions definition = parser.parse(wsdlUrl);
        for (Binding binding: definition.getBindings()) {
            List<String> operationNames = new ArrayList<>();
            for (BindingOperation operation: binding.getOperations()) {
                operationNames.add(operation.getName());
            }
            result.add(new WSDLBinding(binding.getName(), operationNames));
        }

        return result;
    }

    @GET @Path("/{wsdlUrl}/bindings/{bindingName}/operations/{operationName}")
    public SOAPOperationInfo getOperationInfo(@PathParam("wsdlUrl") String wsdlUrl, @PathParam("bindingName") String bindingName,
                                              @PathParam("operationName") String operationName) {
        SOAPOperationInfo info = new SOAPOperationInfo();
        WSDLParser parser = new WSDLParser();
        parser.setResourceResolver(new SSLTrustedExternalResolver());
        Definitions definition = parser.parse(wsdlUrl);
        StringWriter writer = new StringWriter();
        SOARequestCreator creator = new SOARequestCreator(definition, new RequestTemplateCreator(), new MarkupBuilder(writer));
        creator.createRequest(null, operationName, bindingName);
        info.setSampleRequest(writer.toString());

        return info;
    }
}
