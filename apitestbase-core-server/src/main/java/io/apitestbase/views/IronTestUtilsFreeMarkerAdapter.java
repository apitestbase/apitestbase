package io.apitestbase.views;

import io.apitestbase.utils.GeneralUtils;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

public class IronTestUtilsFreeMarkerAdapter {
    public String prettyPrintJSONOrXML(String input) throws XPathExpressionException, TransformerException, IOException {
        return GeneralUtils.prettyPrintJSONOrXML(input);
    }

    public String base64EncodeByteArray(byte[] bytes) {
        return GeneralUtils.base64EncodeByteArray(bytes);
    }
}
