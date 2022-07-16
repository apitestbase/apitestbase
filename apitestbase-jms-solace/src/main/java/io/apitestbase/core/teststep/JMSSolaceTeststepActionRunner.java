package io.apitestbase.core.teststep;

import com.solacesystems.jcsmp.*;
import com.solacesystems.jms.SolConnectionFactory;
import com.solacesystems.jms.SolJmsUtility;
import io.apitestbase.models.endpoint.Endpoint;
import io.apitestbase.models.endpoint.JMSSolaceEndpointProperties;

import javax.jms.Connection;

public class JMSSolaceTeststepActionRunner extends JMSTeststepActionRunner {
    private JCSMPSession createJCSMPSession(Endpoint endpoint) throws InvalidPropertiesException {
        JMSSolaceEndpointProperties endpointProperties = (JMSSolaceEndpointProperties) endpoint.getOtherProperties();
        JCSMPProperties properties = new JCSMPProperties();
        properties.setProperty(JCSMPProperties.HOST, endpoint.getHost() + ":" + endpoint.getPort());
        properties.setProperty(JCSMPProperties.USERNAME, endpoint.getUsername());
        properties.setProperty(JCSMPProperties.PASSWORD, getDecryptedEndpointPassword());
        properties.setProperty(JCSMPProperties.VPN_NAME,  endpointProperties.getVpn());
        return JCSMPFactory.onlyInstance().createSession(properties);
    }

    @Override
    protected Connection createJMSConnection(Endpoint endpoint) throws Exception {
        JMSSolaceEndpointProperties endpointProperties = (JMSSolaceEndpointProperties) endpoint.getOtherProperties();
        SolConnectionFactory connectionFactory = SolJmsUtility.createConnectionFactory();
        connectionFactory.setHost(endpoint.getHost());
        connectionFactory.setPort(endpoint.getPort());
        connectionFactory.setVPN(endpointProperties.getVpn());
        connectionFactory.setUsername(endpoint.getUsername());
        connectionFactory.setPassword(getDecryptedEndpointPassword());
        connectionFactory.setBrowserTimeoutInMS(200);    //  only needed for using JMS QueueBrowser
        return connectionFactory.createConnection();
    }

    @Override
    protected JMSClearQueueResponse clearQueue(Endpoint endpoint, String queueName) throws JCSMPException {
        JMSClearQueueResponse response = new JMSClearQueueResponse();
        int clearedMessagesCount = 0;
        JCSMPSession session = createJCSMPSession(endpoint);
        try {
            Queue queue = JCSMPFactory.onlyInstance().createQueue(queueName);

            ConsumerFlowProperties consumerFlowProperties = new ConsumerFlowProperties();
            consumerFlowProperties.setEndpoint(queue);

            FlowReceiver receiver = session.createFlow(null, consumerFlowProperties, null);
            receiver.start();
            BytesXMLMessage msg;

            do {
                msg = receiver.receive(250);      //  this timeout is only affecting the time wait after fetching the last message, or when the queue is empty
                if (msg != null) {
                    clearedMessagesCount++;
                }
            } while (msg != null);

            receiver.close();
        } finally {
            if (session != null) {
                session.closeSession();
            }
        }

        response.setClearedMessagesCount(clearedMessagesCount);

        return response;
    }

    @Override
    protected JMSCheckQueueDepthResponse checkDepth(Endpoint endpoint, String queueName) throws JCSMPException {
        JMSCheckQueueDepthResponse response = new JMSCheckQueueDepthResponse();
        int queueDepth = 0;
        JCSMPSession session = createJCSMPSession(endpoint);
        try {
            Queue queue = JCSMPFactory.onlyInstance().createQueue(queueName);

            BrowserProperties properties = new BrowserProperties();
            properties.setEndpoint(queue);
            properties.setWaitTimeout(250);                   //  50 is too small and often causes incomplete depth check
            Browser browser = session.createBrowser(properties);
            BytesXMLMessage message;
            do {
                message = browser.getNext();
                if (message != null) {
                    queueDepth++;
                }
            } while (message != null);
        } finally {
            if (session != null) {
                session.closeSession();
            }
        }

        response.setQueueDepth(queueDepth);

        return response;
    }
}