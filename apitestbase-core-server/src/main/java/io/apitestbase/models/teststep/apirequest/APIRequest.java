package io.apitestbase.models.teststep.apirequest;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, property = "minClassName")
public class APIRequest {
}
