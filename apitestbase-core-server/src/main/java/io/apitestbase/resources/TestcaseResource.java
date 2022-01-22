package io.apitestbase.resources;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.annotation.JacksonFeatures;
import io.apitestbase.db.TestcaseDAO;
import io.apitestbase.db.TeststepDAO;
import io.apitestbase.models.Testcase;
import io.apitestbase.models.teststep.Teststep;
import io.apitestbase.utils.GeneralUtils;
import io.dropwizard.jersey.PATCH;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/") @Produces({ MediaType.APPLICATION_JSON })
public class TestcaseResource {
    private final TestcaseDAO testcaseDAO;
    private final TeststepDAO teststepDAO;

    public TestcaseResource(TestcaseDAO testcaseDAO, TeststepDAO teststepDAO) {
        this.testcaseDAO = testcaseDAO;
        this.teststepDAO = teststepDAO;
    }

    @PUT @Path("testcases/{testcaseId}")
    @PermitAll
    public Testcase update_TestcaseEditView(Testcase testcase) {
        testcaseDAO.update(testcase);
        return testcaseDAO.findById_TestcaseEditView(testcase.getId());
    }

    @GET @Path("testcases/{testcaseId}")
    public Testcase findById_TestcaseEditView(@PathParam("testcaseId") long testcaseId) {
        return testcaseDAO.findById_TestcaseEditView(testcaseId);
    }

    @GET @Path("testcases/{testcaseId}/export")
    @JsonView(ResourceJsonViews.TestcaseExport.class)
    @JacksonFeatures(serializationEnable = { SerializationFeature.INDENT_OUTPUT })
    public Testcase export(@PathParam("testcaseId") long testcaseId) {
        Testcase testcase = testcaseDAO.findById_Complete(testcaseId);

        //  clear unnecessary content
        if (GeneralUtils.dataTableIsEmpty(testcase.getDataTable())) {
            testcase.setDataTable(null);
        }
        for (Teststep teststep: testcase.getTeststeps()) {
            if (GeneralUtils.dataTableIsEmpty(teststep.getDataTable())) {
                teststep.setDataTable(null);
            }
        }

        return testcase;
    }

    @PATCH @Path("testcases/{testcaseId}/moveStep")
    @PermitAll
    public Testcase moveStep(Testcase testcase) {
        List<Teststep> teststeps = testcase.getTeststeps();
        teststepDAO.moveInTestcase(testcase.getId(), teststeps.get(0).getSequence(), teststeps.get(1).getSequence());
        return testcaseDAO.findById_TestcaseEditView(testcase.getId());
    }

    /**
     * Clone/copy test case in the same system database.
     * @param testcaseId
     * @param targetFolderId
     * @return the new test case (containing ID only)
     */
    @POST @Path("testcases/{testcaseId}/duplicate")
    @PermitAll
    public Testcase duplicate(@PathParam("testcaseId") long testcaseId,
                          @QueryParam("targetFolderId") long targetFolderId) {
        Testcase testcase = new Testcase();
        testcase.setId(testcaseDAO.duplicate(testcaseId, targetFolderId));
        return testcase;
    }
}
