package com.presantis.backend.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "festivos_dias")
public class FestivoDia extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public UUID id;

    @Column(nullable = false)
    public LocalDate fecha;

    @Column(nullable = false, length = 100)
    public String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    public TipoFestivo tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    public Empresa empresa; // Can be null if it's a NACIONAL holiday
}
