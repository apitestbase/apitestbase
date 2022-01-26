package io.apitestbase.views;

import io.dropwizard.views.View;
import io.apitestbase.models.testrun.testcaserun.TestcaseRun;

/**
 * Used for displaying test case run report.
 */
public class TestcaseRunView extends View {
    private final TestcaseRun testcaseRun;
    private GeneralUtilsFreeMarkerAdapter generalUtilsAdapter;

    public TestcaseRunView(TestcaseRun testcaseRun) {
        super("../views/testcaseRun.ftl");
        this.testcaseRun = testcaseRun;
        this.generalUtilsAdapter = new GeneralUtilsFreeMarkerAdapter();
    }

    public TestcaseRun getTestcaseRun() {
        return testcaseRun;
    }

    public GeneralUtilsFreeMarkerAdapter getGeneralUtilsAdapter() {
        return generalUtilsAdapter;
    }
}
