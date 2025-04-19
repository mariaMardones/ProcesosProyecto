package com.deustocoches.controller;

import java.util.List;
import java.util.Optional;

import com.deustocoches.model.Usuario;
import com.deustocoches.service.UsuarioService;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/usuario")
@Tag(name = "Usuario Controller", description = "API para manejar los usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public List<Usuario> listarUsuariosResgistrados() {
        return usuarioService.listarUsuariosResgistrados();
    }

    @GetMapping("/buscar")
    public ResponseEntity<Usuario> getUsuarioByEmail(@RequestParam("email") String email) {
        Optional<Usuario> usuario = usuarioService.getUsuarioByEmail(email);
        return usuario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
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

    @PostMapping("/registrar")
    public ResponseEntity<Usuario> RegistrarUsuario(@RequestBody Usuario usuario) {
        try {
            Usuario usuarioNuevo = usuarioService.registrarUsuario(usuario);
            return ResponseEntity.ok(usuarioNuevo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @Operation(
            summary = "Iniciar sesión",
            description = "Permite a un usuario iniciar sesión verificando si tiene un token asignado. Si no, genera uno nuevo.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK: Inicio de sesión exitoso, se devuelve el token."),
                    @ApiResponse(responseCode = "400", description = "Bad Request: Usuario no registrado o datos incorrectos."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized: Invalid credentials, login failed"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error: Error interno en el servidor.")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<String> iniciarSesion(@RequestParam("email") String email, @RequestParam("password") String password) {
        try {
        	Optional<String> token = usuarioService.logIn(email, password);

            if (token.isPresent()) {
            	return new ResponseEntity<>(token.get(), HttpStatus.OK);
            } else {
            	return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @Operation(
            summary = "Cerrar sesión",
            description = "Permite a un usuario cerrar sesión eliminando su token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK: Sesión cerrada correctamente."),
                    @ApiResponse(responseCode = "401", description = "Unautorized: Invalid token, logout failed."),
            }
    )
    @PostMapping("/logout")
    public ResponseEntity<Void> cerrarSesion(@RequestParam("token") String token) {

        Optional<Boolean> result = usuarioService.logout(token);
        
        if (result.isPresent() && result.get()) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
    
    @PutMapping("/{id}/bloquear")
    public ResponseEntity<Usuario> bloquearUsuario(@PathVariable Long id) {
        try {
            Usuario usuarioBloqueado = usuarioService.bloquearUsuario(id);
            return ResponseEntity.ok(usuarioBloqueado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/desbloquear")
    public ResponseEntity<Usuario> desbloquearUsuario(@PathVariable Long id) {
        try {
            Usuario usuarioDesbloqueado = usuarioService.desbloquearUsuario(id);
            return ResponseEntity.ok(usuarioDesbloqueado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}