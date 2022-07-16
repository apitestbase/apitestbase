package io.apitestbase.core.teststep;

import io.apitestbase.models.endpoint.Endpoint;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

public class JMSActiveMQTeststepActionRunner extends JMSTeststepActionRunner {
    @Override
    protected Connection createJMSConnection(Endpoint endpoint) throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(endpoint.getConstructedUrl());

        return connectionFactory.createConnection();
    }
}