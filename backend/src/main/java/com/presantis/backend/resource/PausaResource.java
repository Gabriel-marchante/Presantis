package com.presantis.backend.resource;

import com.presantis.backend.dto.PausaRequest;
import com.presantis.backend.model.Pausa;
import com.presantis.backend.service.PausaService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.util.UUID;

@Path("/pausas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"EMPLOYEE", "ADMIN"})
public class PausaResource {

    @Inject
    PausaService pausaService;

    @POST
    @Path("/iniciar")
    public Response iniciar(@Context SecurityContext ctx, @Valid PausaRequest request) {
        UUID usuarioId = UUID.fromString(ctx.getUserPrincipal().getName());
        Pausa pausa = pausaService.iniciarPausa(usuarioId, request.motivo);
        return Response.status(Response.Status.CREATED).entity(pausa).build();
    }

    @POST
    @Path("/finalizar")
    public Response finalizar(@Context SecurityContext ctx) {
        UUID usuarioId = UUID.fromString(ctx.getUserPrincipal().getName());
        Pausa pausa = pausaService.finalizarPausa(usuarioId);
        return Response.ok(pausa).build();
    }
}
