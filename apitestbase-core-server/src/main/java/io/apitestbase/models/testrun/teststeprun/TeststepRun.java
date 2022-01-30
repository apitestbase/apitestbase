package io.apitestbase.models.testrun.teststeprun;

import com.fasterxml.jackson.annotation.JsonView;
import io.apitestbase.models.testrun.TestRun;
import io.apitestbase.models.teststep.Teststep;
import io.apitestbase.resources.ResourceJsonViews;

/**
 * Used for test case running.
 */
public class TeststepRun extends TestRun {
    /**
     * To be overridden by subclasses
     * @return
     */
    @JsonView(ResourceJsonViews.TestcaseRunOutlineOnTestcaseEditView.class)
    public Teststep getMetaTeststep() {
        return null;
    }
}
