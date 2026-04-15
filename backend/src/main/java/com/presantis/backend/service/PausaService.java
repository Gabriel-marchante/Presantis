package com.presantis.backend.service;

import com.presantis.backend.model.EstadoJornada;
import com.presantis.backend.model.Jornada;
import com.presantis.backend.model.MotivoPausa;
import com.presantis.backend.model.Pausa;
import com.presantis.backend.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;

import java.time.LocalDateTime;
import java.util.UUID;

@ApplicationScoped
public class PausaService {

    @Transactional
    public Pausa iniciarPausa(UUID usuarioId, MotivoPausa motivo) {
        Usuario usuario = Usuario.findById(usuarioId);
        if (usuario == null) throw new WebApplicationException("Usuario no encontrado", 404);

        Jornada activa = (Jornada) Jornada.find("usuario = ?1 and estado = ?2", usuario, EstadoJornada.ACTIVA)
                .firstResultOptional()
                .orElseThrow(() -> new WebApplicationException("No tienes ninguna jornada activa", 400));

        // Check if there's already an active pausa
        long pausasActivas = Pausa.count("jornada = ?1 and fin is null", activa);
        if (pausasActivas > 0) {
            throw new WebApplicationException("Ya tienes una pausa activa", 400);
        }

        Pausa pausa = new Pausa();
        pausa.jornada = activa;
        pausa.inicio = LocalDateTime.now();
        pausa.motivo = motivo;
        pausa.persist();

        return pausa;
    }

    @Transactional
    public Pausa finalizarPausa(UUID usuarioId) {
        Usuario usuario = Usuario.findById(usuarioId);
        if (usuario == null) throw new WebApplicationException("Usuario no encontrado", 404);

        Jornada activa = (Jornada) Jornada.find("usuario = ?1 and estado = ?2", usuario, EstadoJornada.ACTIVA)
                .firstResultOptional()
                .orElseThrow(() -> new WebApplicationException("No tienes ninguna jornada activa", 400));

        Pausa pausaActiva = (Pausa) Pausa.find("jornada = ?1 and fin is null", activa)
                .firstResultOptional()
                .orElseThrow(() -> new WebApplicationException("No tienes ninguna pausa activa", 400));

        pausaActiva.fin = LocalDateTime.now();
        
        return pausaActiva;
    }
}
