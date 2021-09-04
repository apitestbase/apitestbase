package io.apitestbase.utils;

import org.junit.jupiter.api.Test;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GeneralUtilsTest {
    @Test
    void prettyPrintJSONOrXML_NullInput() throws Exception {
        String input = null;
        String expectedOutput = null;
        assertEquals(expectedOutput, GeneralUtils.prettyPrintJSONOrXML(input));
    }

    /**
     * Only testing very simple JSON, as the method under test is mainly a 'router' to well tested JSON library.
     * @throws TransformerException
     * @throws IOException
     */
    @Test
    void prettyPrintJSONOrXML_ValidJSON() throws Exception {
        String input = "{\"a\":1}";
        String expectedOutput = "{" + System.lineSeparator() +
                "  \"a\" : 1" + System.lineSeparator() +
                "}";
        assertEquals(expectedOutput, GeneralUtils.prettyPrintJSONOrXML(input));
    }

    @Test
    void prettyPrintJSONOrXML_InvalidJSON() throws Exception {
        String input = "{\"a\":1";
        String expectedOutput = input;
        assertEquals(expectedOutput, GeneralUtils.prettyPrintJSONOrXML(input));
    }

    /**
     * Only testing very simple XML, as the method under test is mainly a 'router' to well tested XML library.
     * @throws TransformerException
     * @throws IOException
     */
    @Test
    void prettyPrintJSONOrXML_ValidXML() throws Exception {
        String input = "<root><a>1</a></root>";
        String expectedOutput = "<root>" + System.lineSeparator() +
                "  <a>1</a>" + System.lineSeparator() +
                "</root>" + System.lineSeparator();
        assertEquals(expectedOutput, GeneralUtils.prettyPrintJSONOrXML(input));
    }

    @Test
    void prettyPrintJSONOrXML_InvalidXML() throws Exception {
        String input = "<root>";
        String expectedOutput = input;
        assertEquals(expectedOutput, GeneralUtils.prettyPrintJSONOrXML(input));
    }

    @Test
    void getSqlStatements() {
        String sqlStatement1 = "update aaa set col1 = 'value1'";
        String sqlStatement2 = "update bbb set col2 = 'value2'";
        String sqlScript = sqlStatement1 + ";\n-- some comment\n" + sqlStatement2 + ";";
        List<String> sqlStatements = GeneralUtils.getSqlStatements(sqlScript);
        assertEquals(sqlStatement1, sqlStatements.get(0));
        assertEquals(sqlStatement2, sqlStatements.get(1));
    }
}
