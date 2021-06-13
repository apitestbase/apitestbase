package io.apitestbase.core.teststep;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, property = "minClassName")
public class APIResponse {
}
