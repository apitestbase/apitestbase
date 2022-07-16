package io.apitestbase.core.teststep;

import io.apitestbase.models.endpoint.Endpoint;
import io.apitestbase.models.teststep.JMSDestinationType;
import io.apitestbase.models.teststep.JMSMessageProperty;
import io.apitestbase.models.teststep.JMSTeststepProperties;
import io.apitestbase.models.teststep.Teststep;
import io.apitestbase.models.teststep.apirequest.APIRequest;
import io.apitestbase.models.teststep.apirequest.JMSRequest;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;

public abstract class JMSTeststepActionRunner extends TeststepActionRunner {
    @Override
    public TeststepActionRunResult run() throws Exception {
        Teststep teststep = getTeststep();
        String action = teststep.getAction();
        if (teststep.getAction() == null) {
            throw new Exception("Action not specified.");
        }
        JMSTeststepProperties teststepProperties = (JMSTeststepProperties) teststep.getOtherProperties();
        if (teststepProperties.getDestinationType() == null) {
            throw new Exception("Destination type not specified.");
        }

        TeststepActionRunResult basicTeststepRun = new TeststepActionRunResult();

        APIResponse response = null;
        Endpoint endpoint = teststep.getEndpoint();
        if (JMSDestinationType.QUEUE == teststepProperties.getDestinationType()) {
            response = doQueueAction(teststepProperties, endpoint, action, teststep.getApiRequest());
        } else if (JMSDestinationType.TOPIC == teststepProperties.getDestinationType()) {

        }

        basicTeststepRun.setResponse(response);

        return basicTeststepRun;
    }

    private APIResponse doQueueAction(JMSTeststepProperties teststepOtherProperties, Endpoint endpoint, String action,
                                      APIRequest apiRequest) throws Exception {
        APIResponse response = null;

        switch (action) {
            case Teststep.ACTION_CLEAR:
                break;
            case Teststep.ACTION_CHECK_DEPTH:
                break;
            case Teststep.ACTION_SEND:
                sendMessageToQueue(endpoint, teststepOtherProperties.getQueueName(), apiRequest);
                break;
            case Teststep.ACTION_BROWSE:
                break;
            default:
                throw new IllegalArgumentException("Unrecognized action " + action + ".");
        }

        return response;
    }

    abstract protected Connection createJMSConnection(Endpoint endpoint) throws JMSException;

    private void sendMessageToQueue(Endpoint endpoint, String queueName, APIRequest apiRequest) throws JMSException {
        Connection connection = createJMSConnection(endpoint);
        javax.jms.Session session = null;

        try {
            session = connection.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
            javax.jms.Queue queue = session.createQueue(queueName);
            MessageProducer messageProducer = session.createProducer(queue);
            JMSRequest request = (JMSRequest) apiRequest;
            javax.jms.TextMessage message = session.createTextMessage(request.getBody());
            for (JMSMessageProperty property: request.getProperties()) {
                switch (property.getType()) {
                    case STRING:
                        message.setStringProperty(property.getName(), property.getValue());
                        break;
                    case BOOLEAN:
                        message.setBooleanProperty(property.getName(), Boolean.parseBoolean(property.getValue()));
                        break;
                    case SHORT:
                        message.setShortProperty(property.getName(), Short.parseShort(property.getValue()));
                        break;
                    case INTEGER:
                        message.setIntProperty(property.getName(), Integer.parseInt(property.getValue()));
                        break;
                    case LONG:
                        message.setLongProperty(property.getName(), Long.parseLong(property.getValue()));
                        break;
                    case FLOAT:
                        message.setFloatProperty(property.getName(), Float.parseFloat(property.getValue()));
                        break;
                    case DOUBLE:
                        message.setDoubleProperty(property.getName(), Double.parseDouble(property.getValue()));
                        break;
                    default:
                        throw new IllegalArgumentException("Unrecognized property type " + property.getType() + ".");
                }
            }

            messageProducer.send(message);
        } finally {
            if (session != null) {
                session.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }
}
