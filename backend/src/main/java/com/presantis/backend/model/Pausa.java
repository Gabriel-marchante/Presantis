package com.presantis.backend.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pausas")
public class Pausa extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jornada_id", nullable = false)
    public Jornada jornada;

    @Column(nullable = false)
    public LocalDateTime inicio;

    public LocalDateTime fin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    public MotivoPausa motivo;
}
