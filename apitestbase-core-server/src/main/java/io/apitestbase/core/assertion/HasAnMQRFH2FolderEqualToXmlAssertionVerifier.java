package io.apitestbase.core.assertion;

import io.apitestbase.models.TestResult;
import io.apitestbase.models.assertion.AssertionVerificationResult;
import io.apitestbase.models.assertion.HasAnMQRFH2FolderEqualToXmlAssertionProperties;
import io.apitestbase.models.teststep.MQRFH2Folder;
import io.apitestbase.models.teststep.MQRFH2Header;
import io.apitestbase.utils.XMLUtils;

public class HasAnMQRFH2FolderEqualToXmlAssertionVerifier extends AssertionVerifier {
    /**
     * @param inputs contains only one argument: the {@link MQRFH2Header label} object that the assertion is verified against
     * @return
     */
    public AssertionVerificationResult verify(Object... inputs) {
        AssertionVerificationResult result = new AssertionVerificationResult();
        HasAnMQRFH2FolderEqualToXmlAssertionProperties assertionProperties =
                (HasAnMQRFH2FolderEqualToXmlAssertionProperties) getAssertion().getOtherProperties();

        //  validate arguments
        if (assertionProperties.getXml() == null) {
            throw new IllegalArgumentException("XML is null.");
        } else if (inputs[0] == null) {
            throw new IllegalArgumentException("There is no MQRFH2 header.");
        }

        result.setResult(TestResult.FAILED);
        MQRFH2Header mqrfh2Header = (MQRFH2Header) inputs[0];
        for (MQRFH2Folder mqrfh2Folder: mqrfh2Header.getFolders()) {
            String differencesStr = XMLUtils.compareXML(
                    assertionProperties.getXml(), mqrfh2Folder.getString(), false);
            if (differencesStr.length() == 0) {
                result.setResult(TestResult.PASSED);
                break;
            }
        }

        return result;
    }
}
