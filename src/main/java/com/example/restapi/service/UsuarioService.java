package com.example.restapi.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.restapi.model.Usuario;
import com.example.restapi.repository.UsuarioRepository;

@Service
public class UsuarioService {
    
//    private final Map<Long, Usuario> usuarios = new HashMap<>();
    public static Map<String, Usuario> tokens = new HashMap<>();

    @Autowired
    private UsuarioRepository repository;
    
    public List<Usuario> listarUsuariosResgistrados() {
        return repository.findAll();
    }

    public Optional<Usuario> getUsuarioByEmail(String email) {
        return Optional.ofNullable(repository.findByEmail(email));
    }

    public Usuario registrarUsuario(Usuario usuario) {
        if (repository.findByEmail(usuario.getEmail()) != null) {
            throw new IllegalArgumentException("El email ya está en uso.");
        }
        return repository.save(usuario);
    }

    public Usuario actualizarUsuario(String email, Usuario usuarioActualizado) {
        Usuario usuarioExistente = repository.findByEmail(email);
        if (usuarioExistente == null) {
            throw new IllegalArgumentException("No se encontró el usuario con el email proporcionado.");
        }
        usuarioExistente.setNombre(usuarioActualizado.getNombre());
        usuarioExistente.setApellido(usuarioActualizado.getApellido());
        usuarioExistente.setFechaNacimiento(usuarioActualizado.getFechaNacimiento());
        usuarioExistente.setPassword(usuarioActualizado.getPassword());
        usuarioExistente.setTlf(usuarioActualizado.getTlf());
        usuarioExistente.setRol(usuarioActualizado.getRol());

        return repository.save(usuarioExistente);
    }

    public void eliminarUsuario(String email) {
        Usuario usuario = repository.findByEmail(email);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado con el email proporcionado.");
        }
        repository.delete(usuario);
    }

    public Optional<String> logIn(String email, String contrasenia) {
        if (tokens.values().stream().anyMatch(u -> u.getEmail().equals(email))) {
            return Optional.empty();
        }
        
        Usuario usuario = repository.findByEmail(email);
        if (usuario == null || !usuario.getPassword().equals(contrasenia)) { // Usa BCrypt en una versión real
            throw new IllegalArgumentException("Credenciales inválidas.");
        }

        String token = generarToken(usuario);
        return Optional.of(token);
    }

    public String generarToken(Usuario u) {
        String token = Timestamp.from(Instant.now()).toString();
        tokens.put(token, u);
        return token;
    }

    public boolean logout(String token) {
        return tokens.remove(token) != null;
    }

    public static Map<String, Usuario> getTokens() {
        return tokens;
    }
}
