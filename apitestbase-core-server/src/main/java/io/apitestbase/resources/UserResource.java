package io.apitestbase.resources;

import io.apitestbase.APITestBaseConstants;
import io.apitestbase.auth.SimplePrincipal;
import io.apitestbase.db.UserDAO;
import io.apitestbase.models.User;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

/**
 * This resource is only registered in team mode.
 */
@Path("/users") @Produces({ MediaType.APPLICATION_JSON })
public class UserResource {
    private final UserDAO userDAO;

    public UserResource(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Return HTTP 200 if user is authenticated; return 401 otherwise.
     */
    @GET @Path("authenticated")
    @PermitAll
    public User authenticated(@Context SecurityContext context) {
        SimplePrincipal principal = (SimplePrincipal) context.getUserPrincipal();
        return userDAO.findByUsername(principal.getName());
    }

    @GET
    @RolesAllowed(APITestBaseConstants.USER_ROLE_ADMIN)
    public List<User> findAll() {
        return userDAO.findAll();
    }

    @POST
    @RolesAllowed(APITestBaseConstants.USER_ROLE_ADMIN)
    public User create(User user) {
        return userDAO.insert(user.getUsername());
    }

    @DELETE @Path("{userId}")
    @RolesAllowed(APITestBaseConstants.USER_ROLE_ADMIN)
    public void delete(@PathParam("userId") long userId) {
        User user = userDAO.findById(userId);
        if (user != null && APITestBaseConstants.SYSADMIN_USER.equals(user.getUsername())) {
            throw new RuntimeException("Can not delete " + APITestBaseConstants.SYSADMIN_USER);
        }

        userDAO.deleteById(userId);
    }

    @PUT @Path("{userId}/password")
    @PermitAll
    public void updatePassword(@PathParam("userId") long userId, @QueryParam("newPassword") String newPassword,
                               @Context SecurityContext context) {
        SimplePrincipal principal = (SimplePrincipal) context.getUserPrincipal();
        User user = userDAO.findByUsername(principal.getName());
        if (user.getId() == userId) {
            userDAO.updatePassword(userId, newPassword);
        } else {
            throw new RuntimeException("You can't change other user's password.");
        }
    }
}
