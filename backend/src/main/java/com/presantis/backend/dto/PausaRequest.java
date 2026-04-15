package com.presantis.backend.dto;

import com.presantis.backend.model.MotivoPausa;
import jakarta.validation.constraints.NotNull;

public class PausaRequest {
    
    @NotNull(message = "El motivo es obligatorio")
    public MotivoPausa motivo;
}
