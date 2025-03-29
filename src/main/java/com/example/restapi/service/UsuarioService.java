package com.example.restapi.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.example.restapi.model.Usuario;

public class UsuarioService {
    private final Map<Long, Usuario> usuarios = new HashMap<>();
    public static Map<String, Usuario> tokens = new HashMap<>();
    
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
