package io.apitestbase.common;

public interface Constants {
    String PROMPT_TEXT_WHEN_SYSTEM_DB_VERSION_IS_BIGGER_THAN_JAR_VERSION =
            "The system database version %1$s is bigger than the jar file version %2$s. Please%n" +
                    "  download and build the latest version of API Test Base,%n" +
                    "  copy the jar file from the dist folder to your current <IronTest_Home> directory, and%n" +
                    "  start the new version of API Test Base in your current <APITestBase_Home> (like by running the start.bat).";
}
