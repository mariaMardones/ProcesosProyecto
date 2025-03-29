package com.example.restapi.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.example.restapi.model.Usuario;
import com.example.restapi.repository.UsuarioRepository;

public class UsuarioService {
    private final Map<Long, Usuario> usuarios = new HashMap<>();
    public static Map<String, Usuario> tokens = new HashMap<>();
    private UsuarioRepository repository;

    public Optional<String> logIn(String email, String contrasenia) {
        // Verificar si el usuario ya tiene una sesión activa
        if (tokens.values().stream().anyMatch(u -> u.getEmail().equals(email))) {
            return Optional.empty();
        }
        
        // Buscar usuario en la base de datos
        Usuario usuario = repository.findByEmail(email);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado con el email proporcionado.");
        }

        // Validar contraseña (aquí deberías usar BCrypt o similar)
        if (usuario.getPassword().equals(contrasenia)) { // ¡OJO! Esto es inseguro, solo para ejemplo
            String token = generarToken(usuario);
            return Optional.of(token);
        } else {
            throw new IllegalArgumentException("Credenciales inválidas para el email: " + email);
        }
    }
    public String generarToken(Usuario u) {
        String token = Timestamp.from(Instant.now()).toString();
        tokens.put(token, u);
        return token;
    }

    public Optional<Boolean> logout(String token) {
        if (tokens.containsKey(token)) {
            tokens.remove(token);
            return Optional.of(true);
        } else {
            return Optional.empty();
        }

    }
        
    public static Map<String, Usuario> getTokens() {
        return tokens;
    }

    public Map<String, Usuario> obtenerTokens() {
        return tokens;
    }
    
    


}
