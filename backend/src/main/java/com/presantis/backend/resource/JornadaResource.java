package com.presantis.backend.resource;

import com.presantis.backend.dto.JornadaRequest;
import com.presantis.backend.model.Jornada;
import com.presantis.backend.service.JornadaService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.util.UUID;

@Path("/jornadas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"EMPLOYEE", "ADMIN"})
public class JornadaResource {

    @Inject
    JornadaService jornadaService;

    @POST
    @Path("/iniciar")
    public Response iniciar(@Context SecurityContext ctx, @Valid JornadaRequest request) {
        UUID usuarioId = UUID.fromString(ctx.getUserPrincipal().getName());
        Jornada jornada = jornadaService.iniciarJornada(usuarioId, request.ubicacion);
        return Response.status(Response.Status.CREATED).entity(jornada).build();
    }

    @POST
    @Path("/finalizar")
    public Response finalizar(@Context SecurityContext ctx, @Valid JornadaRequest request) {
        UUID usuarioId = UUID.fromString(ctx.getUserPrincipal().getName());
        Jornada jornada = jornadaService.finalizarJornada(usuarioId, request.ubicacion);
        return Response.ok(jornada).build();
    }

    @GET
    @Path("/activa")
    public Response getActiva(@Context SecurityContext ctx) {
        UUID usuarioId = UUID.fromString(ctx.getUserPrincipal().getName());
        return jornadaService.getJornadaActiva(usuarioId)
                .map(j -> Response.ok(j).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/historial")
    public Response getHistorial(@Context SecurityContext ctx) {
        UUID usuarioId = UUID.fromString(ctx.getUserPrincipal().getName());
        return Response.ok(jornadaService.getHistorial(usuarioId)).build();
    }
}
