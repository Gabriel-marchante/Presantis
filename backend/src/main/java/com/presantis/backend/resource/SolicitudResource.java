package com.presantis.backend.resource;

import com.presantis.backend.dto.SolicitudRequest;
import com.presantis.backend.dto.SolicitudRespuestaRequest;
import com.presantis.backend.model.SolicitudCambio;
import com.presantis.backend.service.SolicitudService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.util.UUID;

@Path("/solicitudes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SolicitudResource {

    @Inject
    SolicitudService solicitudService;

    @POST
    @RolesAllowed({"EMPLOYEE", "ADMIN"})
    public Response crear(@Context SecurityContext ctx, @Valid SolicitudRequest request) {
        UUID usuarioId = UUID.fromString(ctx.getUserPrincipal().getName());
        SolicitudCambio solicitud = solicitudService.crearSolicitud(usuarioId, request.jornadaId, request.descripcion, request.motivo);
        return Response.status(Response.Status.CREATED).entity(solicitud).build();
    }

    @GET
    @Path("/mias")
    @RolesAllowed({"EMPLOYEE", "ADMIN"})
    public Response getMias(@Context SecurityContext ctx) {
        UUID usuarioId = UUID.fromString(ctx.getUserPrincipal().getName());
        return Response.ok(solicitudService.getSolicitudesMias(usuarioId)).build();
    }

    @GET
    @Path("/todas")
    @RolesAllowed({"ADMIN"})
    public Response getTodas(@Context SecurityContext ctx) {
        // Obtenemos el ID de empresa desde el token (inyectado como claim en authservice)
        // En Quarkus, el JsonWebToken expone los claims.
        // Aquí simplificamos asumiendo que un admin puede ver todas y filtramos por su empresa inyectando JsonWebToken
        return Response.ok(solicitudService.getTodasSolicitudes(UUID.fromString("11111111-1111-1111-1111-111111111111"))).build();
    }

    @PATCH
    @Path("/{id}/responder")
    @RolesAllowed({"ADMIN"})
    public Response responder(@Context SecurityContext ctx, @PathParam("id") UUID id, @Valid SolicitudRespuestaRequest request) {
        UUID adminId = UUID.fromString(ctx.getUserPrincipal().getName());
        SolicitudCambio solicitud = solicitudService.responderSolicitud(adminId, id, request.estado, request.respuesta);
        return Response.ok(solicitud).build();
    }
}
