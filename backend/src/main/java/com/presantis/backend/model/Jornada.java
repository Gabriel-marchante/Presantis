package com.presantis.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "jornadas")
public class Jornada extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    public Usuario usuario;

    @Column(name = "fecha_inicio", nullable = false)
    public LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    public LocalDateTime fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "ubicacion_inicio", nullable = false, length = 30)
    public Ubicacion ubicacionInicio;

    @Enumerated(EnumType.STRING)
    @Column(name = "ubicacion_fin", length = 30)
    public Ubicacion ubicacionFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    public EstadoJornada estado;
    
    @JsonIgnore
    @OneToMany(mappedBy = "jornada", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<Pausa> pausas = new ArrayList<>();
}
