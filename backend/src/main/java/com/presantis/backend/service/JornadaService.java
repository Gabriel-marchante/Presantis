package com.presantis.backend.service;

import com.presantis.backend.model.EstadoJornada;
import com.presantis.backend.model.Jornada;
import com.presantis.backend.model.Ubicacion;
import com.presantis.backend.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class JornadaService {

    @Transactional
    public Jornada iniciarJornada(UUID usuarioId, Ubicacion ubicacion) {
        Usuario usuario = Usuario.findById(usuarioId);
        if (usuario == null) throw new WebApplicationException("Usuario no encontrado", 404);

        // Check if there is already an active jornada
        Optional<Jornada> activa = Jornada.find("usuario = ?1 and estado = ?2", usuario, EstadoJornada.ACTIVA).firstResultOptional();
        if (activa.isPresent()) {
            throw new WebApplicationException("Ya tienes una jornada activa", 400);
        }

        Jornada jornada = new Jornada();
        jornada.usuario = usuario;
        jornada.ubicacionInicio = ubicacion;
        jornada.fechaInicio = LocalDateTime.now();
        jornada.estado = EstadoJornada.ACTIVA;
        jornada.persist();

        return jornada;
    }

    @Transactional
    public Jornada finalizarJornada(UUID usuarioId, Ubicacion ubicacion) {
        Usuario usuario = Usuario.findById(usuarioId);
        if (usuario == null) throw new WebApplicationException("Usuario no encontrado", 404);

        Jornada activa = (Jornada) Jornada.find("usuario = ?1 and estado = ?2", usuario, EstadoJornada.ACTIVA)
                .firstResultOptional()
                .orElseThrow(() -> new WebApplicationException("No tienes ninguna jornada activa", 400));

        activa.ubicacionFin = ubicacion;
        activa.fechaFin = LocalDateTime.now();
        activa.estado = EstadoJornada.FINALIZADA;
        
        return activa;
    }

    public List<Jornada> getHistorial(UUID usuarioId) {
        Usuario usuario = Usuario.findById(usuarioId);
        return Jornada.find("usuario = ?1 order by fechaInicio desc", usuario).list();
    }

    public Optional<Jornada> getJornadaActiva(UUID usuarioId) {
        Usuario usuario = Usuario.findById(usuarioId);
        return Jornada.find("usuario = ?1 and estado = ?2", usuario, EstadoJornada.ACTIVA).firstResultOptional();
    }
}
