package io.apitestbase.models.endpoint;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type", defaultImpl = EndpointProperties.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SOAPEndpointProperties.class, name = EndpointProperties.SOAP_ENDPOINT_PROPERTIES),
        @JsonSubTypes.Type(value = JMSActiveMQEndpointProperties.class, name = EndpointProperties.JMS_ACTIVEMQ_ENDPOINT_PROPERTIES),
        @JsonSubTypes.Type(value = JMSSolaceEndpointProperties.class, name = EndpointProperties.JMS_SOLACE_ENDPOINT_PROPERTIES),
        @JsonSubTypes.Type(value = FTPEndpointProperties.class, name = EndpointProperties.FTP_ENDPOINT_PROPERTIES),
        @JsonSubTypes.Type(value = SFTPEndpointProperties.class, name = EndpointProperties.SFTP_ENDPOINT_PROPERTIES),
        @JsonSubTypes.Type(value = MQEndpointProperties.class, name = EndpointProperties.MQ_ENDPOINT_PROPERTIES),
        @JsonSubTypes.Type(value = IIBEndpointProperties.class, name = EndpointProperties.IIB_ENDPOINT_PROPERTIES)
})
public class EndpointProperties {
    protected static final String SOAP_ENDPOINT_PROPERTIES = "SOAPEndpointProperties";
    protected static final String JMS_ACTIVEMQ_ENDPOINT_PROPERTIES = "JMSActiveMQEndpointProperties";
    protected static final String JMS_SOLACE_ENDPOINT_PROPERTIES = "JMSSolaceEndpointProperties";
    protected static final String FTP_ENDPOINT_PROPERTIES = "FTPEndpointProperties";
    protected static final String SFTP_ENDPOINT_PROPERTIES = "SFTPEndpointProperties";
    protected static final String MQ_ENDPOINT_PROPERTIES = "MQEndpointProperties";
    protected static final String IIB_ENDPOINT_PROPERTIES = "IIBEndpointProperties";

    /**
     * Construct url when it is not supposed to be provided by user.
     * This method should return non-null string when url is needed but not supposed to be provided by user.
     * This method is supposed to be overridden by sub classes.
     */
    public String constructUrl(String host, Integer port) {
        return null;
    }
}
