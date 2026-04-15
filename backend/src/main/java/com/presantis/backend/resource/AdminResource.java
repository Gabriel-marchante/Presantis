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
}
