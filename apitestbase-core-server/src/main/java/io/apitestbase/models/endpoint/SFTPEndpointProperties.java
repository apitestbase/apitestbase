package io.apitestbase.models.endpoint;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(EndpointProperties.SFTP_ENDPOINT_PROPERTIES)
public class SFTPEndpointProperties extends EndpointProperties {
    @Override
    public String constructUrl(String host, Integer port) {
        return "sftp://" + host + ":" + port;
    }
}