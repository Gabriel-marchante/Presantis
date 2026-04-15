package com.presantis.backend.dto;

import com.presantis.backend.model.Ubicacion;
import jakarta.validation.constraints.NotNull;

public class JornadaRequest {
    
    @NotNull(message = "La ubicación es obligatoria")
    public Ubicacion ubicacion;
}
