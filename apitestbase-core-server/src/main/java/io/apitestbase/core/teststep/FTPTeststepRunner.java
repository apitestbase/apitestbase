package io.apitestbase.core.teststep;

import io.apitestbase.models.endpoint.Endpoint;
import io.apitestbase.models.endpoint.FTPEndpointProperties;
import io.apitestbase.models.teststep.FTPTeststepProperties;
import io.apitestbase.models.teststep.Teststep;
import io.apitestbase.models.teststep.apirequest.APIRequest;
import io.apitestbase.models.teststep.apirequest.FtpPutRequest;
import io.apitestbase.models.teststep.apirequest.FtpPutRequestFileFromFile;
import io.apitestbase.models.teststep.apirequest.FtpPutRequestFileFromText;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ProtocolCommandEvent;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.util.TrustManagerUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class FTPTeststepRunner extends TeststepActionRunner {
    @Override
    public TeststepActionRunResult run() throws Exception {
        Teststep teststep = getTeststep();
        TeststepActionRunResult basicTeststepRun = new TeststepActionRunResult();
        FTPTeststepProperties otherProperties = (FTPTeststepProperties) teststep.getOtherProperties();
        Endpoint endpoint = teststep.getEndpoint();

        APIRequest apiRequest = teststep.getApiRequest();
        if (apiRequest instanceof FtpPutRequest) {
            put(endpoint, (FtpPutRequest) apiRequest, otherProperties);
        }

        return basicTeststepRun;
    }

    private void put(Endpoint endpoint, FtpPutRequest ftpPutRequest, FTPTeststepProperties otherProperties)
            throws IOException {
        String username = StringUtils.trimToEmpty(endpoint.getUsername());
        String remoteFilePath = StringUtils.trimToEmpty(otherProperties.getRemoteFilePath());
        byte[] fileBytes;

        //  validate arguments
        if ("".equals(username)) {
            throw new IllegalArgumentException("Username not specified in Endpoint.");
        } else if ("".equals(remoteFilePath)) {
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
        } else {
            FtpPutRequestFileFromFile ftpPutRequestFileFromFile = (FtpPutRequestFileFromFile) ftpPutRequest;
            fileBytes = ftpPutRequestFileFromFile.getFileContent();

            //  validate arguments
            if (fileBytes == null || fileBytes.length == 0) {
                throw new IllegalArgumentException("No file content.");
            }
        }

        FTPEndpointProperties endpointProperties = (FTPEndpointProperties) endpoint.getOtherProperties();
        String password = getDecryptedEndpointPassword();
        FTPClient ftpClient;
        if (endpointProperties.isUseSSL()) {
            ftpClient = new FTPSClient();
            ((FTPSClient) ftpClient).setTrustManager(TrustManagerUtils.getAcceptAllTrustManager());
        } else {
            ftpClient = new FTPClient();
        }
        ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
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
        try {
            ftpClient.connect(endpoint.getHost(), endpoint.getPort());
            ftpClient.login(username, password);
            ftpClient.enterLocalPassiveMode();
            if (endpointProperties.isUseSSL()) {
                ((FTPSClient) ftpClient).execPROT("P");
            }
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.storeFile(remoteFilePath, new ByteArrayInputStream(fileBytes));
        } finally {
            ftpClient.disconnect();
        }
    }
}
