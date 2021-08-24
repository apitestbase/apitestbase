package io.apitestbase.models.teststep.apirequest;

public interface APIRequestFile {
    String getFileName();
    void setFileName(String fileName);
    byte[] getFileContent();
    void setFileContent(byte[] fileContent);
}
