package com.presantis.backend.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "empresas")
public class Empresa extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public UUID id;

    @Column(nullable = false, length = 100)
    public String nombre;

    @Column(nullable = false, length = 20, unique = true)
    public String cif;
}
