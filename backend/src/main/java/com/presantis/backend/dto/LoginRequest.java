package com.presantis.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    
    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "Formato de email inválido")
    public String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    public String password;
}
