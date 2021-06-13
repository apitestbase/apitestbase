package io.apitestbase.auth;

import io.dropwizard.auth.Authorizer;

public class ResourceAuthorizer implements Authorizer<SimplePrincipal> {
    @Override
    public boolean authorize(SimplePrincipal principal, String role) {
        return principal.getRoles().contains(role);
    }
}
