package com.presantis.backend.dto;

import com.presantis.backend.model.EstadoSolicitud;
import jakarta.validation.constraints.NotNull;

public class SolicitudRespuestaRequest {

    @NotNull(message = "El estado es obligatorio")
    public EstadoSolicitud estado;

    public String respuesta;
}
