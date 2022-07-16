package io.apitestbase.models.endpoint;

public class JMSActiveMQEndpointProperties extends JMSEndpointProperties {
    @Override
    public String constructUrl(String host, Integer port) {
        return "tcp://" + host + ':' + port;
    }
}
