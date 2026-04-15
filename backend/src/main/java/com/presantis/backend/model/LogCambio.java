package com.presantis.backend.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "logs_cambios")
public class LogCambio extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jornada_id", nullable = false)
    public Jornada jornada;

    @Column(name = "campo_modificado", nullable = false, length = 50)
    public String campoModificado;

    @Column(name = "valor_anterior", length = 255)
    public String valorAnterior;

    @Column(name = "valor_nuevo", length = 255)
    public String valorNuevo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modificado_por_id", nullable = false)
    public Usuario modificadoPor;

    @Column(nullable = false)
    public LocalDateTime fecha = LocalDateTime.now();

    @Column(length = 255)
    public String motivo;
}
