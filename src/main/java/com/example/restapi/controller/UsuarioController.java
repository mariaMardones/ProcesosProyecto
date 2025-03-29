package com.example.restapi.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.restapi.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuarios Controller", description = "Endpoints para gestionar los usuarios")
public class UsuarioController {
	
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(
            summary = "Registrar un nuevo usuario",
            description = "Registra un nuevo usuario con los datos proporcionados.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created: Usuario registrado exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Bad Request: Datos inválidos o incompletos"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/registro")
    public ResponseEntity<Void> registrarUsuario(
        @RequestParam("nombre") String nombre,
        @RequestParam("apellido") String apellido,
        @RequestParam("fechaNacimiento") String fechaNacimiento,
        @RequestParam("email") String email,
        @RequestParam("password") String password,
        @RequestParam("tlf") String tlf,
        @RequestParam("rol") String rol
    ) {
        try {
            usuarioService.registro(nombre,email, fechaNacimiento, email,password,tlf,rol);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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
    public ResponseEntity<String> iniciarSesion(
            @RequestParam(name = "email") String email,
            @RequestParam(name = "contraseña") String contraseña) {
        try {
        	Optional<String> token = usuarioService.logIn(email, contraseña);

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
                    @ApiResponse(responseCode = "400", description = "Bad Request: Usuario no tiene un token válido."),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error: Error interno en el servidor.")
            }
    )
    @PostMapping("/logout")
    public ResponseEntity<Void> cerrarSesion(
            @Parameter(name = "token", description = "Token de sesión del usuario", required = true)
            @RequestBody String token) {
        try {
            if (token == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            Optional<Boolean> result = usuarioService.logout(token);
            
            if (result.isPresent() && result.get()) {
            	return new ResponseEntity<>(HttpStatus.OK);	
            } else {
            	return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }  

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
