package io.apitestbase.upgrade.operations.db;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.jdbi.v3.core.Jdbi;

import java.util.Base64;
import java.util.List;
import java.util.logging.Logger;

public class AdvancedSystemDBOperation_0_18_4_To_0_18_5_Part1 extends SystemDBOperation {
    private static final Logger LOGGER = Logger.getLogger("Upgrade");

    public AdvancedSystemDBOperation_0_18_4_To_0_18_5_Part1() {
        super(new DefaultArtifactVersion("0.18.4"), new DefaultArtifactVersion("0.18.5"));
    }

    @Override
    public void run(Jdbi jdbi, String newSystemDBURL) {
        LOGGER.info("Start running " + this.getClass().getSimpleName() + " class against " + newSystemDBURL + ". It could take a couple of minutes.");

        List<RequestMap> rows = jdbi.withHandle(handle ->
                handle.createQuery("select id, request from teststep where type = 'MQ' and " +
                                "action in ('Enqueue', 'Publish') and request_type = 'File'")
                        .map((rs, ctx) -> new RequestMap(rs.getString("id"), rs.getBytes("request")))
                        .list());

        for (RequestMap row: rows) {
            LOGGER.info("Processing teststep with id " + row.id + " ...");

            String requestBase64 = row.request == null ? null : Base64.getEncoder().encodeToString(row.request);
            jdbi.withHandle(handle -> handle
                    .createUpdate("update teststep set api_request = '{\"minClassName\":\".MQEnqueueOrPublishFromFileRequest\",' || " +
                            "'\"fileName\":' || COALESCE('\"' || request_filename || '\"', 'null') || ',' || " +
                            "'\"fileContent\":' || COALESCE('\"' || ? || '\"', 'null') || '}' where id = ?")
                    .bind(0, requestBase64)
                    .bind(1, row.id)
                    .execute());
        }

        LOGGER.info("Finished running " + this.getClass().getSimpleName() + " class against " + newSystemDBURL + ".");
    }

    private class RequestMap {
        private String id;
        private byte[] request;

        private RequestMap(String id, byte[] request) {
            this.id = id;
            this.request = request;
        }
    }

    public static void main(String[] args) {
        String username = args[0];
        String password = args[1];
        String systemDBFolderPath = args[2];
        String newSystemDBURL = "jdbc:h2:" + systemDBFolderPath + "\\database\\test;IFEXISTS=TRUE";
        Jdbi jdbi = Jdbi.create(newSystemDBURL, username, password);
        new AdvancedSystemDBOperation_0_18_4_To_0_18_5_Part1().run(jdbi, newSystemDBURL);
    }
}
