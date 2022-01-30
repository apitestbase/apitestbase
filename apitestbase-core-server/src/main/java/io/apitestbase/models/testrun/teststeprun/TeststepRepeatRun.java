package io.apitestbase.models.testrun.teststeprun;

import com.fasterxml.jackson.annotation.JsonView;
import io.apitestbase.models.testrun.TestRun;
import io.apitestbase.models.teststep.Teststep;
import io.apitestbase.resources.ResourceJsonViews;

public class TeststepRepeatRun extends TestRun {
    @JsonView(ResourceJsonViews.TestcaseRunOutlineOnTestcaseEditView.class)
    private int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * To be overridden by subclasses
     * @return
     */
    public Teststep getMetaTeststep() {
        return null;
    }
}
