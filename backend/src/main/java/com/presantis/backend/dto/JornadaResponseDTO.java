package com.presantis.backend.dto;

import com.presantis.backend.model.EstadoJornada;
import com.presantis.backend.model.Jornada;
import com.presantis.backend.model.Ubicacion;

import java.time.LocalDateTime;
import java.util.UUID;

public class JornadaResponseDTO {
    public UUID id;
    public UUID usuarioId;
    public LocalDateTime fechaInicio;
    public LocalDateTime fechaFin;
    public EstadoJornada estado;
    public Ubicacion ubicacionInicio;
    public Ubicacion ubicacionFin;

    public JornadaResponseDTO() {}

    public JornadaResponseDTO(Jornada jornada) {
        this.id = jornada.id;
        if (jornada.usuario != null) {
            this.usuarioId = jornada.usuario.id;
        }
        this.fechaInicio = jornada.fechaInicio;
        this.fechaFin = jornada.fechaFin;
        this.estado = jornada.estado;
        this.ubicacionInicio = jornada.ubicacionInicio;
        this.ubicacionFin = jornada.ubicacionFin;
    }
}
