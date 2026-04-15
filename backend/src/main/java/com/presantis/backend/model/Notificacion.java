package com.presantis.backend.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notificaciones")
public class Notificacion extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    public Usuario usuario;

    @Column(nullable = false, length = 500)
    public String mensaje;

    @Column(nullable = false)
    public boolean leida = false;

    @Column(nullable = false)
    public LocalDateTime fecha = LocalDateTime.now();
}
