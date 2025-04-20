package com.deustocoches.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.deustocoches.model.Usuario;
import com.deustocoches.repository.UsuarioRepository;

@Service
public class UsuarioService {
    
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
        usuarioExistente.setBloqueado(usuarioActualizado.isBloqueado());

        return repository.save(usuarioExistente);
    }

    public void eliminarUsuario(String email) {
        Usuario usuario = repository.findByEmail(email);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado con el email proporcionado.");
        }
        repository.delete(usuario);
    }

    public Optional<String> logIn(String email, String password) {
        String token;
        if (tokens.values().stream().anyMatch(u -> u.getEmail().equals(email))) {
            return Optional.empty();
        }
        
        Usuario usuario = repository.findByEmail(email);

        if (usuario == null ) {
            throw new IllegalArgumentException("Usuario no encontrado con el email proporcionado.");
        } 
        else if (usuario.isBloqueado()) {
            throw new IllegalArgumentException("Usuario bloqueado.");
        }
        else if (!usuario.getPassword().equals(password)) {
            throw new IllegalArgumentException("Contraseña incorrecta.");
        } 
        else {
            token = generarToken(usuario);
            return Optional.of(token);
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
    
    public Usuario bloquearUsuario(String email) {
        Usuario usuario = repository.findByEmail(email);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado con el email proporcionado.");
        }
        usuario.setBloqueado(true);
        return actualizarUsuario(usuario.getEmail(), usuario);
    }

    public Usuario desbloquearUsuario(String email) {
        Usuario usuario = repository.findByEmail(email);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado con el email proporcionado.");
        }
        usuario.setBloqueado(false);
        return actualizarUsuario(usuario.getEmail(), usuario);
    }

}
