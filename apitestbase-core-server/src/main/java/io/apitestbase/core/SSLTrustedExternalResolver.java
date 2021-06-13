package io.apitestbase.core;

import com.predic8.xml.util.ExternalResolver;
import io.apitestbase.core.teststep.HTTPAPIResponse;
import io.apitestbase.models.HTTPMethod;
import io.apitestbase.utils.GeneralUtils;

import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;

public class SSLTrustedExternalResolver extends ExternalResolver {
    public StringReader resolveViaHttp(String url) throws Exception {
        URI uri = new URI(url);
        uri = uri.normalize();
        HTTPAPIResponse response = GeneralUtils.invokeHTTPAPI(uri.toString(), null, null, HTTPMethod.GET,
                new ArrayList<>(), null);
        return new StringReader(response.getHttpBody());
    }
}
