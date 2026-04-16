package com.presantis.backend.service;

import com.presantis.backend.model.Usuario;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import org.mindrot.jbcrypt.BCrypt;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

@ApplicationScoped
public class AuthService {

    public Optional<Usuario> authenticate(String email, String password) {
        Usuario usuario = Usuario.find("email", email).firstResult();
        if (usuario != null && usuario.activo) {
            if (BCrypt.checkpw(password, usuario.passwordHash)) {
                return Optional.of(usuario);
            }
        }
        return Optional.empty();
    }

    public String generateToken(Usuario usuario) {
        return Jwt.issuer("https://presantis.com/")
                .upn(usuario.id.toString())
                .subject(usuario.email)
                .groups(new HashSet<>(Arrays.asList(usuario.rol.name())))
                .claim("empresaId", usuario.empresa.id.toString())
                .expiresIn(Duration.ofHours(24))
                .sign();
    }

    public String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }
}
