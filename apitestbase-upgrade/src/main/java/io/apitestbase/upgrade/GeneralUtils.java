package io.apitestbase.upgrade;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class GeneralUtils {
    public static String getResourceAsText(String resourcePath) throws IOException {
        String result;
        //  read text file from inside a jar file
        try (InputStream is = GeneralUtils.class.getClassLoader().getResourceAsStream(resourcePath)) {
            result = IOUtils.toString(is, StandardCharsets.UTF_8.name());
        }
        return result;
    }
}
