package io.apitestbase.views;

import io.apitestbase.models.endpoint.Endpoint;
import io.apitestbase.models.endpoint.EndpointProperties;
import io.apitestbase.models.testrun.teststeprun.TeststepIndividualRun;
import io.apitestbase.models.teststep.Teststep;
import io.dropwizard.views.View;

/**
 * Used for displaying single test step individual run report, by clicking a step data table row in test case run result outline on the test case edit view.
 */
public class TeststepIndividualRunView extends View {
    private final String stepType;
    private final String stepAction;
    private final Endpoint endpoint;
    private final EndpointProperties endpointProperties;
    private final TeststepIndividualRun stepIndividualRun;
    private GeneralUtilsFreeMarkerAdapter generalUtilsAdapter;

    public TeststepIndividualRunView(TeststepIndividualRun stepIndividualRun) {
        super("../views/teststep/stepIndividualRun.ftl");
        Teststep teststep = stepIndividualRun.getAtomicRunResult().getTeststep();
        this.stepType = teststep.getType();
        this.stepAction = teststep.getAction();
        this.endpoint = teststep.getEndpoint();
        this.endpointProperties = teststep.getEndpoint() == null ? null : teststep.getEndpoint().getOtherProperties();
        this.stepIndividualRun = stepIndividualRun;
        this.generalUtilsAdapter = new GeneralUtilsFreeMarkerAdapter();
    }

    public String getStepType() {
        return stepType;
    }

    public String getStepAction() {
        return stepAction;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public EndpointProperties getEndpointProperties() {
        return endpointProperties;
    }

    public TeststepIndividualRun getStepIndividualRun() {
        return stepIndividualRun;
    }

    public GeneralUtilsFreeMarkerAdapter getGeneralUtilsAdapter() {
        return generalUtilsAdapter;
    }
}
