package io.irontest.core.runner;

import io.irontest.models.Endpoint;
import io.irontest.models.Teststep;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by Trevor Li on 7/14/15.
 */
public class SOAPTeststepRunner extends TeststepRunner {
    protected SOAPAPIResponse run(Teststep teststep) throws Exception {
        final SOAPAPIResponse apiResponse = new SOAPAPIResponse();

        Endpoint endpoint = teststep.getEndpoint();

        //  build http client instance
        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        if (!"".equals(StringUtils.trimToEmpty(endpoint.getUsername()))) {
            CredentialsProvider provider = new BasicCredentialsProvider();
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
                    endpoint.getUsername(), endpoint.getPassword());
            provider.setCredentials(AuthScope.ANY, credentials);
            httpClientBuilder.setDefaultCredentialsProvider(provider);
        }
        CloseableHttpClient httpclient = httpClientBuilder.build();

        HttpPost httpPost = new HttpPost(endpoint.getUrl());
        httpPost.setEntity(new StringEntity((String) teststep.getRequest()));
        ResponseHandler<Void> responseHandler = new ResponseHandler<Void>() {
            public Void handleResponse(final HttpResponse httpResponse) throws IOException {
                String contentType = httpResponse.getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue();
                apiResponse.setHttpResponseContentType(contentType);
                HttpEntity entity = httpResponse.getEntity();
                apiResponse.setHttpResponseBody(entity != null ? EntityUtils.toString(entity) : null);
                return null;
            }
        };
        httpclient.execute(httpPost, responseHandler);

        return apiResponse;
    }
}
