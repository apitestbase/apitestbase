package io.apitestbase.views;

import io.apitestbase.models.testrun.teststeprun.TeststepIndividualRun;
import io.apitestbase.models.teststep.Teststep;
import io.dropwizard.views.View;

/**
 * Used for displaying single test step individual run report, by clicking a step data table row in test case run result outline on the test case edit view.
 */
public class TeststepIndividualRunView extends View {
    private final String stepAction;
    private final TeststepIndividualRun stepIndividualRun;
    private GeneralUtilsFreeMarkerAdapter generalUtilsAdapter;

    public TeststepIndividualRunView(TeststepIndividualRun stepIndividualRun) {
        super("../views/teststep/stepIndividualRun.ftl");
        Teststep teststep = stepIndividualRun.getAtomicRunResult().getTeststep();
        this.stepAction = teststep.getAction();
        this.stepIndividualRun = stepIndividualRun;
        this.generalUtilsAdapter = new GeneralUtilsFreeMarkerAdapter();
    }

    public String getStepAction() {
        return stepAction;
    }

    public TeststepIndividualRun getStepIndividualRun() {
        return stepIndividualRun;
    }

    public GeneralUtilsFreeMarkerAdapter getGeneralUtilsAdapter() {
        return generalUtilsAdapter;
    }
}
