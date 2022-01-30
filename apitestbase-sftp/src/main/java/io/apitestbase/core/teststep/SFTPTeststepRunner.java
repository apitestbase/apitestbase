package io.apitestbase.core.teststep;

import com.jcraft.jsch.*;
import io.apitestbase.models.endpoint.Endpoint;
import io.apitestbase.models.teststep.SFTPTeststepProperties;
import io.apitestbase.models.teststep.Teststep;
import io.apitestbase.models.teststep.apirequest.APIRequest;
import io.apitestbase.models.teststep.apirequest.SftpPutRequest;
import io.apitestbase.models.teststep.apirequest.SftpPutRequestFileFromFile;
import io.apitestbase.models.teststep.apirequest.SftpPutRequestFileFromText;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SFTPTeststepRunner extends TeststepActionRunner {
    @Override
    public TeststepActionRunResult run() throws Exception {
        Teststep teststep = getTeststep();
        TeststepActionRunResult basicTeststepRun = new TeststepActionRunResult();
        SFTPTeststepProperties otherProperties = (SFTPTeststepProperties) teststep.getOtherProperties();
        Endpoint endpoint = teststep.getEndpoint();

        APIRequest apiRequest = teststep.getApiRequest();
        if (apiRequest instanceof SftpPutRequest) {
            put(endpoint, (SftpPutRequest) apiRequest, otherProperties);
        }

        return basicTeststepRun;
    }

    private Session getSession(Endpoint endpoint) throws JSchException {
        JSch jsch = new JSch();
        Session session = jsch.getSession(endpoint.getUsername(), endpoint.getHost(), endpoint.getPort());
        session.setPassword(getDecryptedEndpointPassword());
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();
        return session;
    }

    private void put(Endpoint endpoint, SftpPutRequest sftpPutRequest, SFTPTeststepProperties otherProperties)
            throws JSchException, SftpException, IOException {
        String username = StringUtils.trimToEmpty(endpoint.getUsername());
        String remoteFilePath = StringUtils.trimToEmpty(otherProperties.getRemoteFilePath());
        byte[] fileBytes;

        //  validate arguments
        if ("".equals(username)) {
            throw new IllegalArgumentException("Username not specified in Endpoint.");
        } else if ("".equals(remoteFilePath)) {
            throw new IllegalArgumentException("Target File Path not specified.");
        }
        if (sftpPutRequest instanceof SftpPutRequestFileFromText) {
            SftpPutRequestFileFromText sftpPutRequestFileFromText = (SftpPutRequestFileFromText) sftpPutRequest;
            String fileContent = sftpPutRequestFileFromText.getFileContent();

            //  validate arguments
            if ("".equals(StringUtils.trimToEmpty(fileContent))) {
                throw new IllegalArgumentException("No file content.");
            }

            fileBytes = fileContent.getBytes();
        } else {
            SftpPutRequestFileFromFile sftpPutRequestFileFromFile = (SftpPutRequestFileFromFile) sftpPutRequest;
            fileBytes = sftpPutRequestFileFromFile.getFileContent();

            //  validate arguments
            if (fileBytes == null || fileBytes.length == 0) {
                throw new IllegalArgumentException("No file content.");
            }
        }

        Session session = getSession(endpoint);
        InputStream inputStream = null;
        try {
            ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            inputStream = new ByteArrayInputStream(fileBytes);
            channelSftp.put(inputStream, otherProperties.getRemoteFilePath());
            channelSftp.exit();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }
}
