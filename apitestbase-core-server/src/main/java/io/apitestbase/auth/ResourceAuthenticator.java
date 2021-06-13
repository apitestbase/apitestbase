package io.apitestbase.auth;

import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import io.apitestbase.db.UserDAO;
import io.apitestbase.models.User;
import io.apitestbase.utils.PasswordUtils;

import java.util.Optional;

public class ResourceAuthenticator implements Authenticator<BasicCredentials, SimplePrincipal> {
    private UserDAO userDAO;

    public ResourceAuthenticator(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public Optional<SimplePrincipal> authenticate(BasicCredentials credentials) {
        User user = userDAO.findByUsername(credentials.getUsername());
        if (user != null && user.getPassword().equals(
                PasswordUtils.hashPassword(credentials.getPassword(), user.getSalt()))) {
            SimplePrincipal principal = new SimplePrincipal(credentials.getUsername());
            principal.getRoles().addAll(user.getRoles());
            return Optional.of(principal);
        }
        return Optional.empty();
    }
}