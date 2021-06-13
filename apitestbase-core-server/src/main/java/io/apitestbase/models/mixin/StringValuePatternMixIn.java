package io.apitestbase.models.mixin;

import com.fasterxml.jackson.annotation.JsonView;
import io.apitestbase.resources.ResourceJsonViews;

/**
 * MixIn for {@link com.github.tomakehurst.wiremock.matching.StringValuePattern}.
 */
@JsonView(ResourceJsonViews.TestcaseExport.class)
public class StringValuePatternMixIn {
}
