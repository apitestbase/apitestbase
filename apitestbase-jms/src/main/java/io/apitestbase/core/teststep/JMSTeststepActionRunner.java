package io.apitestbase.core.teststep;

import io.apitestbase.models.endpoint.Endpoint;
import io.apitestbase.models.teststep.*;
import io.apitestbase.models.teststep.apirequest.APIRequest;
import io.apitestbase.models.teststep.apirequest.JMSMessagePropertyType;
import io.apitestbase.models.teststep.apirequest.JMSRequest;

import javax.jms.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;

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
            doTopicAction(endpoint, teststepProperties.getTopicString(), teststep.getApiRequest());
        }

        basicTeststepRun.setResponse(response);

        return basicTeststepRun;
    }

    abstract protected Connection createJMSConnection(Endpoint endpoint) throws Exception;

    private APIResponse doQueueAction(JMSTeststepProperties teststepOtherProperties, Endpoint endpoint, String action,
                                      APIRequest apiRequest) throws Exception {
        APIResponse response = null;

        switch (action) {
            case Teststep.ACTION_CLEAR:
                response = clearQueue(endpoint, teststepOtherProperties.getQueueName());
                break;
            case Teststep.ACTION_CHECK_DEPTH:
                response = checkDepth(endpoint, teststepOtherProperties.getQueueName());
                break;
            case Teststep.ACTION_SEND:
                sendMessageToQueue(endpoint, teststepOtherProperties.getQueueName(), apiRequest);
                break;
            case Teststep.ACTION_BROWSE:
                int browseMessageIndex = Integer.valueOf(teststepOtherProperties.getBrowseMessageIndex());
                if (browseMessageIndex < 1) {
                    throw new IllegalArgumentException("Message index must be a positive integer");
                }
                response = browseQueue(endpoint, teststepOtherProperties.getQueueName(),
                        browseMessageIndex);
                break;
            default:
                throw new IllegalArgumentException("Unrecognized action " + action + ".");
        }

        return response;
    }

    private void doTopicAction(Endpoint endpoint, String topicString, APIRequest apiRequest) throws Exception {
        try (Connection connection = createJMSConnection(endpoint);
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);) {
            Topic topic = session.createTopic(topicString);
            MessageProducer messageProducer = session.createProducer(topic);
            JMSRequest request = (JMSRequest) apiRequest;
            TextMessage message = session.createTextMessage(request.getBody());
            for (JMSMessageProperty property: request.getProperties()) {
                message.setStringProperty(property.getName(), property.getValue());
            }

            messageProducer.send(message);
        }
    }

    protected APIResponse checkDepth(Endpoint endpoint, String queueName) throws Exception {
        JMSCheckQueueDepthResponse response = new JMSCheckQueueDepthResponse();
        int queueDepth = 0;

        try (Connection connection = createJMSConnection(endpoint);
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {
            Queue queue = session.createQueue(queueName);
            QueueBrowser browser = session.createBrowser(queue);

            Enumeration<Message> messages = browser.getEnumeration();
            while (messages.hasMoreElements()) {
                messages.nextElement();
                queueDepth++;
            }
        }

        response.setQueueDepth(queueDepth);

        return response;
    }

    protected APIResponse clearQueue(Endpoint endpoint, String queueName) throws Exception {
        JMSClearQueueResponse response = new JMSClearQueueResponse();
        int clearedMessagesCount = 0;

        try (Connection connection = createJMSConnection(endpoint);
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                MessageConsumer consumer = session.createConsumer(session.createQueue(queueName))) {
            Message message;
            do {
                message = consumer.receive(1000);
                if (message != null) {
                    clearedMessagesCount++;
                }
            } while (message != null);
        }

        response.setClearedMessagesCount(clearedMessagesCount);
        return response;
    }

    private void sendMessageToQueue(Endpoint endpoint, String queueName, APIRequest apiRequest) throws Exception {
        try (Connection connection = createJMSConnection(endpoint);
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {
            Queue queue = session.createQueue(queueName);
            MessageProducer messageProducer = session.createProducer(queue);
            JMSRequest request = (JMSRequest) apiRequest;
            TextMessage message = session.createTextMessage(request.getBody());
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
        }
    }

    private APIResponse browseQueue(Endpoint endpoint, String queueName, int browseMessageIndex) throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        JMSBrowseQueueResponse response = null;

        try (Connection connection = createJMSConnection(endpoint);
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {
            Queue queue = session.createQueue(queueName);

            QueueBrowser browser = session.createBrowser(queue);
            Enumeration<Message> messages = browser.getEnumeration();

            int index = 0;
            while (messages.hasMoreElements()) {
                index++;
                Message message = messages.nextElement();
                if (index == browseMessageIndex) {
                    response = new JMSBrowseQueueResponse();
                    response.setBrowseMessageIndex(browseMessageIndex);

                    //  set header
                    Map<String, String> header = response.getHeader();
                    header.put(JMSConstants.JMS_MESSAGE_ID, message.getJMSMessageID());
                    header.put(JMSConstants.JMS_CORRELATION_ID, message.getJMSCorrelationID());
                    header.put(JMSConstants.JMS_TIMESTAMP,
                            dateFormat.format(new Date(message.getJMSTimestamp())));
                    header.put(JMSConstants.JMS_TYPE, message.getJMSType());
                    header.put(JMSConstants.JMS_DESTINATION, message.getJMSDestination().toString());
                    String jmsDeliveryMode = JMSConstants.UNKNOWN;
                    if (message.getJMSDeliveryMode() == DeliveryMode.PERSISTENT) {
                        jmsDeliveryMode = JMSConstants.PERSISTENT;
                    } else if (message.getJMSDeliveryMode() == DeliveryMode.NON_PERSISTENT) {
                        jmsDeliveryMode = JMSConstants.NON_PERSISTENT;
                    }
                    header.put(JMSConstants.JMS_DELIVERY_MODE, jmsDeliveryMode);
                    header.put(JMSConstants.JMS_EXPIRATION, message.getJMSExpiration() == 0 ?
                            null : dateFormat.format(new Date(message.getJMSExpiration())));
                    header.put(JMSConstants.JMS_PRIORITY, Integer.toString(message.getJMSPriority()));
                    header.put(JMSConstants.JMS_REDELIVERED, Boolean.toString(message.getJMSRedelivered()));
                    header.put(JMSConstants.JMS_REPLY_TO, message.getJMSReplyTo() == null ?
                            null : message.getJMSReplyTo().toString());

                    //  set properties
                    Enumeration propertyNames = message.getPropertyNames();
                    while (propertyNames.hasMoreElements()) {
                        String propertyName = (String) propertyNames.nextElement();
                        System.out.println();
                        JMSMessageProperty jmsMessageProperty = new JMSMessageProperty(propertyName,
                                message.getStringProperty(propertyName), JMSMessagePropertyType.getByText(
                                message.getObjectProperty(propertyName).getClass().getSimpleName()));
                        response.getProperties().add(jmsMessageProperty);
                    }

                    //  set body
                    String body;
                    if (message instanceof TextMessage) {
                        body = ((TextMessage) message).getText();
                    } else if (message instanceof BytesMessage) {
                        BytesMessage bytesMessage = (BytesMessage) message;
                        byte[] bytes = new byte[(int) bytesMessage.getBodyLength()];
                        bytesMessage.readBytes(bytes);
                        body = new String(bytes);
                    } else if (message instanceof MapMessage) {
                        StringBuffer sb = new StringBuffer();
                        MapMessage mapMessage = (MapMessage) message;
                        Enumeration mapNames = mapMessage.getMapNames();
                        while (mapNames.hasMoreElements()) {
                            String mapName = (String) mapNames.nextElement();
                            sb.append(mapName).append(":").append(mapMessage.getObject(mapName)).append("\n");
                        }
                        body = sb.toString();
                    } else {
                        throw new RuntimeException("Message type " + message.getClass() + " currently unsupported.");
                    }
                    response.setBody(body);

                    break;
                }
            }
        }

        return response;
    }
}
