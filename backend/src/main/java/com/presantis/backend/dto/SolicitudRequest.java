package com.presantis.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class SolicitudRequest {

    @NotNull(message = "El ID de jornada es obligatorio")
    public UUID jornadaId;

    @NotBlank(message = "La descripción no puede estar vacía")
    public String descripcion;

    @NotBlank(message = "El motivo no puede estar vacío")
    public String motivo;
}
