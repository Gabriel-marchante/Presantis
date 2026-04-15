package com.presantis.backend.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "vacaciones")
public class Vacacion extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    public Usuario usuario;

    @Column(name = "fecha_inicio", nullable = false)
    public LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    public LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    public TipoVacacion tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    public EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;

    @Column(length = 500)
    public String motivo;
    
    @Column(name = "fecha_solicitud", nullable = false)
    public LocalDateTime fechaSolicitud = LocalDateTime.now();
}
