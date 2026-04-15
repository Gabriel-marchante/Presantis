package com.presantis.backend.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "solicitudes_cambio")
public class SolicitudCambio extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    public Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jornada_id", nullable = false)
    public Jornada jornada;

    @Column(nullable = false, length = 500)
    public String descripcion;

    @Column(nullable = false, length = 255)
    public String motivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    public EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;

    @Column(name = "fecha_solicitud", nullable = false)
    public LocalDateTime fechaSolicitud = LocalDateTime.now();

    @Column(name = "fecha_respuesta")
    public LocalDateTime fechaRespuesta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    public Usuario admin;
}
