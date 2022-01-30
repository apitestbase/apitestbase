package io.apitestbase.core.teststep;

import io.apitestbase.models.endpoint.Endpoint;
import io.apitestbase.models.teststep.apirequest.MQTTRequest;
import io.apitestbase.models.teststep.MQTTTeststepProperties;
import io.apitestbase.models.teststep.Teststep;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MQTTTeststepActionRunner extends TeststepActionRunner {
    @Override
    public TeststepActionRunResult run() throws Exception {
        Teststep teststep = getTeststep();
        MQTTTeststepProperties otherProperties = (MQTTTeststepProperties) teststep.getOtherProperties();

        //  validate arguments
        if ("".equals(StringUtils.trimToEmpty(otherProperties.getTopicString()))) {
            throw new IllegalArgumentException("Topic String not specified.");
        }

        Endpoint endpoint = teststep.getEndpoint();
        MqttClient mqttClient = new MqttClient(endpoint.getUrl(), "apitestbase-mqtt-teststep");
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setUserName(endpoint.getUsername());
        connOpts.setPassword(getDecryptedEndpointPassword() == null ? null : getDecryptedEndpointPassword().toCharArray());
        mqttClient.connect(connOpts);

        try {
            MQTTRequest request = (MQTTRequest) teststep.getApiRequest();
            MqttMessage message = new MqttMessage(request.getPayload() == null ? null : request.getPayload().getBytes());
            message.setQos(1);
            mqttClient.publish(otherProperties.getTopicString(), message);
        } finally {
            mqttClient.disconnect();
        }

        return new TeststepActionRunResult();
    }
}
