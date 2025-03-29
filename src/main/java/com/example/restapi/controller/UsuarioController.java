package com.example.restapi.controller;
package com.example.restapi.controller;

import java.util.List;
import java.util.Optional;

import com.example.restapi.model.Usuario;
import com.example.restapi.service.UsuarioService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuario")
@Tag(name = "Usuario Controller", description = "API para manejar los usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public List<Usuario> getAllUsuarios() {
        return usuarioService.getAllUsuarios();
    }

    @GetMapping("/buscar")
    public ResponseEntity<Usuario> getUsuarioByEmail(@RequestParam("email") String email) {
        Optional<Usuario> usuario = usuarioService.getUsuarioByEmail(email);
        return usuario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/crear")
    public ResponseEntity<Usuario> crearUsuario(@RequestBody Usuario usuario) {
        try {
            Usuario usuarioNuevo = usuarioService.registrarUsuario(usuario);
            return ResponseEntity.ok(usuarioNuevo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/actualizar")
    public ResponseEntity<Usuario> actualizarUsuario(@RequestParam("email") String email, @RequestBody Usuario usuario) {
        try {
            Usuario usuarioActualizado = usuarioService.actualizarUsuario(email, usuario);
            return ResponseEntity.ok(usuarioActualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/eliminar")
    public ResponseEntity<Void> eliminarUsuario(@RequestParam("email") String email) {
        Optional<Usuario> usuario = usuarioService.getUsuarioByEmail(email);
        if (usuario.isPresent()) {
            usuarioService.eliminarUsuario(email);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> iniciarSesion(@RequestParam("email") String email, @RequestParam("password") String password) {
        try {
            Optional<String> token = usuarioService.logIn(email, password);
            return token.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(401).build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> cerrarSesion(@RequestBody String token) {
        try {
            if (token == null || token.isEmpty()) {
                return ResponseEntity.status(400).build();
            }
            boolean result = usuarioService.logout(token);
            return result ? ResponseEntity.ok().build() : ResponseEntity.status(400).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}