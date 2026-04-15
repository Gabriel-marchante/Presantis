package com.presantis.backend.service;

import com.presantis.backend.model.EstadoJornada;
import com.presantis.backend.model.Jornada;
import com.presantis.backend.model.LogCambio;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class AdminService {

    public List<Jornada> getPresenciaRest(UUID empresaId) {
        // Devuelve las jornadas activas de los usuarios de esa empresa
        return Jornada.find("estado = ?1 and usuario.empresa.id = ?2", EstadoJornada.ACTIVA, empresaId).list();
    }

    public List<LogCambio> getLogs(UUID empresaId) {
        return LogCambio.find("jornada.usuario.empresa.id = ?1 order by fecha desc", empresaId).list();
    }
}
