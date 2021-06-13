package io.apitestbase.models.mixin;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import io.apitestbase.resources.ResourceJsonViews;

/**
 * MixIn for {@link ResponseDefinition}.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonView(ResourceJsonViews.TestcaseExport.class)
public class ResponseDefinitionMixIn {
}
