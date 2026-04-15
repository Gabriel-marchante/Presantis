package com.presantis.backend.resource;

import com.presantis.backend.dto.LoginRequest;
import com.presantis.backend.dto.TokenResponse;
import com.presantis.backend.model.Usuario;
import com.presantis.backend.service.AuthService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Optional;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    AuthService authService;

    @POST
    @Path("/login")
    public Response login(@Valid LoginRequest request) {
        Optional<Usuario> userOpt = authService.authenticate(request.email, request.password);

        if (userOpt.isPresent()) {
            Usuario usuario = userOpt.get();
            String token = authService.generateToken(usuario);

            TokenResponse response = new TokenResponse(
                    token,
                    usuario.id,
                    usuario.nombre,
                    usuario.apellidos,
                    usuario.rol.name(),
                    usuario.empresa.nombre
            );
            return Response.ok(response).build();
        }

        return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\":\"Credenciales inválidas\"}").build();
    }

    @POST
    @Path("/logout")
    public Response logout() {
        // En JWT no hay estado de logout como tal en el servidor a no ser que se implemente una blacklist.
        // El frontend debe encargarse de borrar el token.
        return Response.ok().entity("{\"message\":\"Sesión cerrada correctamente\"}").build();
    }
}
