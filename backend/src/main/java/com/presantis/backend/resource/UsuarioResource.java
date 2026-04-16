package com.presantis.backend.resource;

import com.presantis.backend.model.Usuario;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.util.UUID;

@Path("/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioResource {

    @PUT
    @Path("/me")
    @RolesAllowed({"EMPLOYEE", "ADMIN"})
    @Transactional
    public Response updateProfile(@Context SecurityContext ctx, Usuario request) {
        UUID usuarioId = UUID.fromString(ctx.getUserPrincipal().getName());
        Usuario me = Usuario.findById(usuarioId);
        if (me == null) return Response.status(404).build();

        me.nombre = request.nombre;
        me.apellidos = request.apellidos;
        if(request.email != null && !request.email.isBlank()) {
           me.email = request.email;
        }
        
        return Response.ok(me).build();
    }

    public static class PasswordDTO {
        public String current;
        public String newPassword;
        public String confirm;
    }

    @PUT
    @Path("/me/password")
    @RolesAllowed({"EMPLOYEE", "ADMIN"})
    @Transactional
    public Response changePassword(@Context SecurityContext ctx, PasswordDTO request) {
        UUID usuarioId = UUID.fromString(ctx.getUserPrincipal().getName());
        Usuario me = Usuario.findById(usuarioId);
        if (me == null) return Response.status(404).build();

        // Check if passwords matches (in real life hashed). Simplified implementation for iteration
        if (!request.current.equals(me.passwordHash)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Contraseña actual incorrecta").build();
        }

        me.passwordHash = request.newPassword;
        return Response.ok().build();
    }
}
