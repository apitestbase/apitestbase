package io.apitestbase.resources;

import com.fasterxml.jackson.annotation.JsonView;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.apitestbase.core.testcase.DataDrivenTestcaseRunner;
import io.apitestbase.core.testcase.RegularTestcaseRunner;
import io.apitestbase.core.testcase.TestcaseRunner;
import io.apitestbase.db.TestcaseDAO;
import io.apitestbase.db.TestcaseRunDAO;
import io.apitestbase.db.TeststepRunDAO;
import io.apitestbase.db.UtilsDAO;
import io.apitestbase.models.Testcase;
import io.apitestbase.models.testrun.TestcaseRun;
import io.apitestbase.models.testrun.TeststepRun;
import io.apitestbase.views.TestcaseRunView;
import io.apitestbase.views.TeststepRunView;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("/") @Produces({ MediaType.APPLICATION_JSON })
public class TestcaseRunResource {
    private final TestcaseDAO testcaseDAO;
    private final UtilsDAO utilsDAO;
    private final TestcaseRunDAO testcaseRunDAO;
    private final TeststepRunDAO teststepRunDAO;
    private WireMockServer wireMockServer;

    public TestcaseRunResource(TestcaseDAO testcaseDAO, UtilsDAO utilsDAO, TestcaseRunDAO testcaseRunDAO,
                               TeststepRunDAO teststepRunDAO, WireMockServer wireMockServer) {
        this.testcaseDAO = testcaseDAO;
        this.utilsDAO = utilsDAO;
        this.testcaseRunDAO = testcaseRunDAO;
        this.teststepRunDAO = teststepRunDAO;
        this.wireMockServer = wireMockServer;
    }

    @POST @Path("testcaseruns") @PermitAll
    @JsonView(ResourceJsonViews.TestcaseRunResultOnTestcaseEditView.class)
    public TestcaseRun create(@QueryParam("testcaseId") long testcaseId) throws IOException {
        Testcase testcase = testcaseDAO.findById_Complete(testcaseId);
        TestcaseRunner testcaseRunner;
        if (testcase.getDataTable().getRows().isEmpty()) {
            testcaseRunner = new RegularTestcaseRunner(testcase, utilsDAO, testcaseRunDAO, wireMockServer);
        } else {
            testcaseRunner = new DataDrivenTestcaseRunner(testcase, utilsDAO, testcaseRunDAO, wireMockServer);
        }
        return testcaseRunner.run();
    }

    @GET @Path("testcaseruns/{testcaseRunId}/htmlreport") @Produces(MediaType.TEXT_HTML)
    public TestcaseRunView getHTMLReportByTestcaseRunId(@PathParam("testcaseRunId") long testcaseRunId) {
        TestcaseRun testcaseRun = testcaseRunDAO.findById(testcaseRunId);
        return new TestcaseRunView(testcaseRun);
    }

    @GET @Path("teststepruns/{stepRunId}/htmlreport") @Produces(MediaType.TEXT_HTML)
    public TeststepRunView getStepRunHTMLReportById(@PathParam("stepRunId") long stepRunId) {
        TeststepRun sstepRun = teststepRunDAO.findById(stepRunId);
        return new TeststepRunView(sstepRun);
    }

    @GET @Path("testcaseruns/lastrun/htmlreport") @Produces(MediaType.TEXT_HTML)
    public Object getTestcaseLastRunHTMLReport(@QueryParam("testcaseId") long testcaseId) {
        TestcaseRun testcaseRun = testcaseRunDAO.findLastByTestcaseId(testcaseId);
        if (testcaseRun == null) {
            return "The test case has never been run.";
        } else {
            return new TestcaseRunView(testcaseRun);
        }
    }
}