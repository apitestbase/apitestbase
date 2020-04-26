package io.irontest.core.runner;

import io.irontest.models.endpoint.Endpoint;
import io.irontest.models.endpoint.FTPEndpointProperties;
import io.irontest.models.teststep.APIRequest;
import io.irontest.models.teststep.FtpPutRequest;
import io.irontest.models.teststep.FtpPutRequestFileFromText;
import io.irontest.models.teststep.Teststep;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ProtocolCommandEvent;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;

public class FTPTeststepRunner extends TeststepRunner {
    protected BasicTeststepRun run(Teststep teststep) throws Exception {
        BasicTeststepRun basicTeststepRun = new BasicTeststepRun();
        Endpoint endpoint = teststep.getEndpoint();

        APIRequest apiRequest = teststep.getApiRequest();
        if (apiRequest instanceof FtpPutRequest) {
            put(endpoint, (FtpPutRequest) apiRequest);
        }

        return basicTeststepRun;
    }

    private void put(Endpoint endpoint, FtpPutRequest ftpPutRequest) throws IOException {
        String targetFilePath = StringUtils.trimToEmpty(ftpPutRequest.getTargetFilePath());
        byte[] fileBytes = null;

        //  validate arguments
        if ("".equals(targetFilePath)) {
            throw new IllegalArgumentException("Target File Path not specified.");
        }
        if (ftpPutRequest instanceof FtpPutRequestFileFromText) {
            FtpPutRequestFileFromText ftpPutRequestFileFromText = (FtpPutRequestFileFromText) ftpPutRequest;
            String fileContent = ftpPutRequestFileFromText.getFileContent();

            //  validate arguments
            if ("".equals(StringUtils.trimToEmpty(fileContent))) {
                throw new IllegalArgumentException("No file content.");
            }

            fileBytes = fileContent.getBytes();
        }

        FTPEndpointProperties endpointProperties = (FTPEndpointProperties) endpoint.getOtherProperties();
        String username = endpoint.getUsername() == null ? "anonymous" : endpoint.getUsername();
        String password = endpoint.getUsername() == null ?
                System.getProperty("user.name") + "@" + InetAddress.getLocalHost().getHostName() :
                endpoint.getPassword();
        FTPClient ftpClient = new FTPClient();
        ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        try {
            ftpClient.connect(endpointProperties.getHost(), endpointProperties.getPort());
            ftpClient.login(username, password);

            ftpClient.addProtocolCommandListener(new ProtocolCommandListener() {
                @Override
                public void protocolCommandSent(ProtocolCommandEvent event) {}

                @Override
                public void protocolReplyReceived(ProtocolCommandEvent event) {
                    if (FTPReply.isNegativePermanent(event.getReplyCode())) {
                        throw new RuntimeException("Failed to put the file. " + event.getMessage());
                    }
                }
            });
            ftpClient.storeFile(targetFilePath, new ByteArrayInputStream(fileBytes));
        } finally {
            ftpClient.disconnect();
        }
    }
}