package io.github.manoelpiovesan.resources;

import io.github.manoelpiovesan.entities.User;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/users")
public class UserResource {

    @ConfigProperty(name = "config.password.length")
    Integer passwordLength;

    @POST
    @PermitAll
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/create")
    public Response create(User user) {
        validateUser(user);
        User localUser = validateUser(user);

        localUser.password = BcryptUtil.bcryptHash(localUser.password);
        localUser.roles = "user";
        localUser.persist();

        return Response.ok(localUser).build();
    }

    @GET
    @Path("/count")
    @RolesAllowed("admin")
    @Produces(MediaType.TEXT_PLAIN)
    public Response count() {
        return Response.ok(User.count()).build();
    }

    @GET
    @Path("/me")
    @RolesAllowed("user")
    @Produces(MediaType.APPLICATION_JSON)
    public Response me() {
        return Response.ok(User.find("username", "manoel").firstResult())
                       .build();
    }

    @GET
    @PermitAll
    @Produces("text/plain")
    public String hello() {
        return "Permit all!";
    }

    @GET
    @Path("/list")
    @RolesAllowed("admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response list() {
        return Response.ok(User.listAll()).build();
    }

    @GET
    @Path("/admin")
    @RolesAllowed("admin")
    @Produces("text/plain")
    public String admin() {
        return "Admin!";
    }

    private User validateUser(User user) {

        if (user.username == null || user.username.isEmpty()) {
            throw new WebApplicationException("Username cannot be empty", 400);
        }
        if (user.password == null || user.password.isEmpty()) {
            throw new WebApplicationException("Password cannot be empty", 400);
        }

        if (user.password.length() < passwordLength) {
            throw new WebApplicationException(
                    "Password must have at least " + passwordLength +
                    " characters", 400);
        }

        User localUser = User.find("username", user.username).firstResult();
        if (localUser != null) {
            System.out.println("Username already exists");
            throw new WebApplicationException("Username already exists", 400);
        }

        System.out.println("User validated");

        return user;
    }

}
