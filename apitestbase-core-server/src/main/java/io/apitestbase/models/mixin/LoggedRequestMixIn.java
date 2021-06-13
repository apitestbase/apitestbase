package io.apitestbase.models.mixin;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import io.apitestbase.resources.ResourceJsonViews;

import java.util.Date;

/**
 * MixIn for {@link com.github.tomakehurst.wiremock.verification.LoggedRequest}.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoggedRequestMixIn {
    @JsonView({ResourceJsonViews.MockServerStubRequestList.class, ResourceJsonViews.MockServerUnmatchedRequestList.class})
    Date loggedDate;
}
