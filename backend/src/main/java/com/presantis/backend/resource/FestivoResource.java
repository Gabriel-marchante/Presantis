package com.presantis.backend.resource;

import com.presantis.backend.model.FestivoDia;
import com.presantis.backend.model.TipoFestivo;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("/festivos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FestivoResource {

    @GET
    @RolesAllowed({"EMPLOYEE", "ADMIN"})
    public Response getAll() {
        List<FestivoDia> festivos = FestivoDia.listAll();
        return Response.ok(festivos).build();
    }

    @POST
    @RolesAllowed({"ADMIN"})
    @Transactional
    public Response crear(FestivoDia request) {
        if (request.tipo == null) request.tipo = TipoFestivo.EMPRESA;
        request.persist();
        return Response.status(Response.Status.CREATED).entity(request).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"ADMIN"})
    @Transactional
    public Response borrar(@PathParam("id") UUID id) {
        boolean eliminada = FestivoDia.deleteById(id);
        if (eliminada) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
