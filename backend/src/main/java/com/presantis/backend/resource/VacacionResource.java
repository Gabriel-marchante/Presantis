package com.presantis.backend.resource;

import com.presantis.backend.model.Vacacion;
import com.presantis.backend.model.Usuario;
import com.presantis.backend.model.EstadoSolicitud;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

@Path("/vacaciones")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VacacionResource {

    @POST
    @RolesAllowed({"EMPLOYEE", "ADMIN"})
    @Transactional
    public Response crear(@Context SecurityContext ctx, Vacacion request) {
        UUID usuarioId = UUID.fromString(ctx.getUserPrincipal().getName());
        Usuario usuario = Usuario.findById(usuarioId);
        if (usuario == null) return Response.status(Response.Status.NOT_FOUND).build();

        request.usuario = usuario;
        request.estado = EstadoSolicitud.PENDIENTE;
        request.fechaSolicitud = LocalDateTime.now();
        request.persist();
        return Response.status(Response.Status.CREATED).entity(request).build();
    }

    @GET
    @Path("/mias")
    @RolesAllowed({"EMPLOYEE", "ADMIN"})
    public Response getMias(@Context SecurityContext ctx) {
        UUID usuarioId = UUID.fromString(ctx.getUserPrincipal().getName());
        Usuario usuario = Usuario.findById(usuarioId);
        List<Vacacion> vacaciones = Vacacion.find("usuario = ?1 order by fechaSolicitud desc", usuario).list();
        return Response.ok(vacaciones).build();
    }

    @GET
    @Path("/todas")
    @RolesAllowed({"ADMIN"})
    public Response getTodas() {
        // En un entorno real se filtraría por empresaId del token
        List<Vacacion> vacaciones = Vacacion.listAll();
        return Response.ok(vacaciones).build();
    }

    @PATCH
    @Path("/{id}/responder")
    @RolesAllowed({"ADMIN"})
    @Transactional
    public Response responder(@PathParam("id") UUID id, Vacacion request) {
        Vacacion vacacion = Vacacion.findById(id);
        if (vacacion == null) return Response.status(Response.Status.NOT_FOUND).build();
        
        vacacion.estado = request.estado;
        return Response.ok(vacacion).build();
    }
}
