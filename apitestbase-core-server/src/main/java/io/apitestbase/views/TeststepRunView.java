package io.apitestbase.views;

import io.apitestbase.models.testrun.TeststepRun;
import io.dropwizard.views.View;

/**
 * Used for displaying single test step run report, by clicking a step in test case run result outline on the test case edit view.
 */
public class TeststepRunView extends View {
    private final TeststepRun stepRun;
    private GeneralUtilsFreeMarkerAdapter generalUtilsAdapter;

    public TeststepRunView(TeststepRun stepRun) {
        super("../views/teststep/stepRun.ftl");
        this.stepRun = stepRun;
        this.generalUtilsAdapter = new GeneralUtilsFreeMarkerAdapter();
    }

    public TeststepRun getStepRun() {
        return stepRun;
    }

    public GeneralUtilsFreeMarkerAdapter getGeneralUtilsAdatper() {
        return generalUtilsAdapter;
    }
}
