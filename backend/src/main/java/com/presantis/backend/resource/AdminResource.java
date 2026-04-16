package com.presantis.backend.resource;

import com.presantis.backend.service.AdminService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.UUID;

@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"ADMIN"})
public class AdminResource {

    @Inject
    AdminService adminService;

    // Hardcoded empresaId for simplicity in MVP, should be retrieved from token
    private UUID getEmpresaId() {
        return UUID.fromString("11111111-1111-1111-1111-111111111111");
    }

    @GET
    @Path("/presencia")
    public Response getPresencia() {
        return Response.ok(adminService.getPresenciaRest(getEmpresaId())).build();
    }

    @GET
    @Path("/logs")
    public Response getLogs() {
        return Response.ok(adminService.getLogs(getEmpresaId())).build();
    }

    @GET
    @Path("/usuarios")
    public Response getUsuarios() {
        // En un caso real llamaríamos al service para filtrar por empresa.
        // Aquí usamos Panache para recuperar todo directo como MVP para el Dash
        return Response.ok(com.presantis.backend.model.Usuario.listAll()).build();
    }

    @GET
    @Path("/usuarios/{id}/jornadas")
    public Response getJornadasUsuario(@jakarta.ws.rs.PathParam("id") UUID id) {
        return Response.ok(com.presantis.backend.model.Jornada.find("usuario.id", id).list()).build();
    }

    @GET
    @Path("/dashboard-stats")
    public Response getDashboardStats() {
        // Obtenemos los activos (simplificado)
        long activos = com.presantis.backend.model.Jornada.count("estado", com.presantis.backend.model.EstadoJornada.ACTIVA);
        long solicitudes = 0; // Dummy real value until Solicitud table is fully integrated
        long incidencias = 0; // Dummy real value 
        long horasHoy = activos * 8; // Approximation, we don't have hours logic yet. But we won't mock "empty" ones randomly

        java.util.Map<String, Long> stats = new java.util.HashMap<>();
        stats.put("empleadosActivos", activos);
        stats.put("totalHorasHoy", horasHoy);
        stats.put("solicitudesPendientes", solicitudes);
        stats.put("incidencias", incidencias);

        return Response.ok(stats).build();
    }

    @GET
    @Path("/usuarios/{id}/stats")
    public Response getUsuarioDetallesStats(@jakarta.ws.rs.PathParam("id") UUID id) {
        // En un MVP real esto suma los Diff(fechaFin, fechaInicio) > 8h
        // Como no tenemos lógica compleja de turnos asignados por usuario, simulamos el contador
        // de horas trabajando real.
        long diasTrabajados = com.presantis.backend.model.Jornada.count("usuario.id = ?1 and estado = ?2", id, com.presantis.backend.model.EstadoJornada.FINALIZADA);
        long horasExtra = 0; // Mock until schedule system exists
        long retrasos = 0;   // Mock 
        
        long diasLibres = com.presantis.backend.model.Vacacion.count("usuario.id = ?1 and estado = ?2", id, com.presantis.backend.model.EstadoSolicitud.APROBADA);

        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("diasTrabajados", diasTrabajados);
        stats.put("horasExtra", horasExtra);
        stats.put("retrasos", retrasos);
        stats.put("diasLibresAprobados", diasLibres);

        return Response.ok(stats).build();
    }
}
