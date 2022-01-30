package io.apitestbase.views;

import io.apitestbase.models.testrun.teststeprun.TeststepRepeatRun;
import io.dropwizard.views.View;

/**
 * Used for displaying single test step repeat run report, by clicking a step in test case run result outline on the test case edit view.
 */
public class TeststepRepeatRunView extends View {
    private final TeststepRepeatRun stepRepeatRun;
    private GeneralUtilsFreeMarkerAdapter generalUtilsAdapter;

    public TeststepRepeatRunView(TeststepRepeatRun stepRepeatRun) {
        super("../views/teststep/stepRepeatRun.ftl");
        this.stepRepeatRun = stepRepeatRun;
        this.generalUtilsAdapter = new GeneralUtilsFreeMarkerAdapter();
    }

    public TeststepRepeatRun getStepRepeatRun() {
        return stepRepeatRun;
    }

    public GeneralUtilsFreeMarkerAdapter getGeneralUtilsAdapter() {
        return generalUtilsAdapter;
    }
}
