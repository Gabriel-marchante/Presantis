package com.presantis.backend.dto;

import java.util.UUID;

public class TokenResponse {
    
    public String token;
    public UUID usuarioId;
    public String nombre;
    public String apellidos;
    public String rol;
    public String empresaNombre;

    public TokenResponse(String token, UUID usuarioId, String nombre, String apellidos, String rol, String empresaNombre) {
        this.token = token;
        this.usuarioId = usuarioId;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.rol = rol;
        this.empresaNombre = empresaNombre;
    }
}
