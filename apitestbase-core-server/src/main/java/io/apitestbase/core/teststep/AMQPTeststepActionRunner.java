package io.apitestbase.core.teststep;

import com.ibm.mqlight.api.*;
import io.apitestbase.models.teststep.AMQPTeststepProperties;
import io.apitestbase.models.teststep.Teststep;
import io.apitestbase.models.teststep.apirequest.AMQPRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class AMQPTeststepActionRunner extends TeststepActionRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(AMQPTeststepActionRunner.class);

    @Override
    public TeststepActionRunResult run() throws Exception {
        Teststep teststep = getTeststep();
        AMQPTeststepProperties otherProperties = (AMQPTeststepProperties) teststep.getOtherProperties();

        //  validate arguments
        if ("".equals(StringUtils.trimToEmpty(otherProperties.getNodeAddress()))) {
            throw new IllegalArgumentException("Target Address not specified.");
        }

        List<Exception> exceptionListDuringSending = new ArrayList<>();

        AMQPRequest amqpRequest = (AMQPRequest) teststep.getApiRequest();

        NonBlockingClient theClient = NonBlockingClient.create(teststep.getEndpoint().getUrl(), new NonBlockingClientAdapter<Void>() {
            public void onStarted(NonBlockingClient client, Void context) {
                client.send(otherProperties.getNodeAddress(), amqpRequest.getBody(), null, new CompletionListener() {
                    public void onSuccess(NonBlockingClient client, Object context) {
                        client.stop(null, null);
                    }
                    public void onError(NonBlockingClient client, Object context, Exception exception) {
                        Exception wrappedException = new Exception(
                                "NonBlockingClient is started, but failed to send message to AMQP service. " +
                                        exception.getMessage(), exception);
                        LOGGER.error("", wrappedException);
                        exceptionListDuringSending.add(wrappedException);
                        client.stop(null, null);
                    }
                }, null);
            }

            public void onRetrying(NonBlockingClient client, Void context, ClientException clientException) {
                if (clientException != null) {
                    Exception wrappedException = new Exception(
                            "Unhandled exception in NonBlockingClientAdapter.onRetrying() method. " +
                                    clientException.getMessage(), clientException);
                    LOGGER.error("", wrappedException);
                    exceptionListDuringSending.add(wrappedException);
                    client.stop(null, null);
                }
            }

            public void onStopped(NonBlockingClient client, Void context, ClientException clientException) {
                if (clientException != null) {
                    Exception wrappedException = new Exception(
                            "Unhandled exception in NonBlockingClientAdapter.onStopped() method. "  +
                                    clientException.getMessage(), clientException);
                    LOGGER.error("", wrappedException);
                    exceptionListDuringSending.add(wrappedException);
                }
            }
        }, null);

        while (theClient.getState() != ClientState.STOPPED) {
            LOGGER.info("Waiting for the AMQP client to stop ...");
            Thread.sleep(1000);
        }

        if (!exceptionListDuringSending.isEmpty()) {
            throw exceptionListDuringSending.get(0);    //  throw the first encountered exception
        }

        return new TeststepActionRunResult();
    }
}
