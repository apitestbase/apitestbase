package io.apitestbase.views;

import io.dropwizard.views.View;
import io.apitestbase.models.testrun.TeststepRun;

/**
 * Used for displaying single test step run report, by clicking a step in test case run result outline on the test case edit view.
 */
public class TeststepRunView extends View {
    private final TeststepRun teststepRun;
    private GeneralUtilsFreeMarkerAdapter generalUtilsAdapter;

    public TeststepRunView(TeststepRun teststepRun) {
        super("../views/teststep/stepRun.ftl");
        this.teststepRun = teststepRun;
        this.generalUtilsAdapter = new GeneralUtilsFreeMarkerAdapter();
    }

    public TeststepRun getStepRun() {
        return teststepRun;
    }

    public GeneralUtilsFreeMarkerAdapter getGeneralUtilsAdatper() {
        return generalUtilsAdapter;
    }
}
