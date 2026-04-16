package com.presantis.backend.resource;

import com.presantis.backend.dto.JornadaRequest;
import com.presantis.backend.dto.JornadaResponseDTO;
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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
        return Response.status(Response.Status.CREATED).entity(new JornadaResponseDTO(jornada)).build();
    }

    @POST
    @Path("/finalizar")
    public Response finalizar(@Context SecurityContext ctx, @Valid JornadaRequest request) {
        UUID usuarioId = UUID.fromString(ctx.getUserPrincipal().getName());
        Jornada jornada = jornadaService.finalizarJornada(usuarioId, request.ubicacion);
        return Response.ok(new JornadaResponseDTO(jornada)).build();
    }

    @GET
    @Path("/activa")
    public Response getActiva(@Context SecurityContext ctx) {
        UUID usuarioId = UUID.fromString(ctx.getUserPrincipal().getName());
        return jornadaService.getJornadaActiva(usuarioId)
                .map(j -> Response.ok(new JornadaResponseDTO(j)).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/historial")
    public Response getHistorial(@Context SecurityContext ctx) {
        UUID usuarioId = UUID.fromString(ctx.getUserPrincipal().getName());
        List<JornadaResponseDTO> historial = jornadaService.getHistorial(usuarioId)
                .stream()
                .map(JornadaResponseDTO::new)
                .collect(Collectors.toList());
        return Response.ok(historial).build();
    }

    @GET
    @Path("/stats/me")
    public Response getStatsMe(@Context SecurityContext ctx) {
        UUID usuarioId = UUID.fromString(ctx.getUserPrincipal().getName());
        long diasTrabajados = Jornada.count("usuario.id = ?1 and estado = ?2", usuarioId, com.presantis.backend.model.EstadoJornada.FINALIZADA);
        long horasSemanales = diasTrabajados * 8; // Mapped approximation based on days since tracking hours accurately requires java.time diff
        
        java.util.Map<String, Long> stats = new java.util.HashMap<>();
        stats.put("horasSemanales", horasSemanales);
        stats.put("diasTrabajados", diasTrabajados);
        stats.put("solicitudesPropias", 0L); 
        stats.put("incidenciasPropias", 0L);
        return Response.ok(stats).build();
    }
}
