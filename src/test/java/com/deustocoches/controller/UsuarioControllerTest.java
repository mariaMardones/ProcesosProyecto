package com.deustocoches.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.deustocoches.model.TipoRol;
import com.deustocoches.model.Usuario;
import com.deustocoches.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("deprecation") // Suprimir advertencia de MockBean
@WebMvcTest(UsuarioController.class)
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Juan");
        usuario.setApellido("García");
        usuario.setEmail("juan.garcia@example.com");
        usuario.setPassword("password123");
        usuario.setFechaNacimiento("01/01/1990");
        usuario.setTlf("666777888");
        usuario.setRol(TipoRol.CLIENTE);
        usuario.setBloqueado(false);
    }

    @Test
    void testListarUsuarios() throws Exception {
        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setNombre("Ana");
        usuario2.setEmail("ana@example.com");

        when(usuarioService.listarUsuariosResgistrados())
                .thenReturn(Arrays.asList(usuario, usuario2));

        mockMvc.perform(get("/api/usuario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Juan"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].nombre").value("Ana"));

        verify(usuarioService, times(1)).listarUsuariosResgistrados();
    }

    @Test
    void testObtenerUsuarioPorEmail() throws Exception {
        when(usuarioService.getUsuarioByEmail("juan.garcia@example.com"))
                .thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/api/usuario/buscar")
                .param("email", "juan.garcia@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.email").value("juan.garcia@example.com"));

        verify(usuarioService, times(1)).getUsuarioByEmail("juan.garcia@example.com");
    }

    @Test
    void testObtenerUsuarioNoExistente() throws Exception {
        when(usuarioService.getUsuarioByEmail("noexiste@example.com"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/usuario/buscar")
                .param("email", "noexiste@example.com"))
                .andExpect(status().isNotFound());

        verify(usuarioService, times(1)).getUsuarioByEmail("noexiste@example.com");
    }

    @Test
    void testRegistrarUsuario() throws Exception {
        when(usuarioService.registrarUsuario(any(Usuario.class)))
                .thenReturn(usuario);

        mockMvc.perform(post("/api/usuario/registrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.email").value("juan.garcia@example.com"));

        verify(usuarioService, times(1)).registrarUsuario(any(Usuario.class));
    }

    @Test
    void testRegistrarUsuarioConError() throws Exception {
        when(usuarioService.registrarUsuario(any(Usuario.class)))
                .thenThrow(new RuntimeException("Email ya existe"));

        mockMvc.perform(post("/api/usuario/registrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isBadRequest());

        verify(usuarioService, times(1)).registrarUsuario(any(Usuario.class));
    }

    @Test
    void testEliminarUsuario() throws Exception {
        when(usuarioService.getUsuarioByEmail("juan.garcia@example.com"))
                .thenReturn(Optional.of(usuario));

        mockMvc.perform(delete("/api/usuario/eliminar")
                .param("email", "juan.garcia@example.com"))
                .andExpect(status().isNoContent());

        verify(usuarioService, times(1)).getUsuarioByEmail("juan.garcia@example.com");
        verify(usuarioService, times(1)).eliminarUsuario("juan.garcia@example.com");
    }

    @Test
    void testEliminarUsuarioNoExistente() throws Exception {
        when(usuarioService.getUsuarioByEmail("noexiste@example.com"))
                .thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/usuario/eliminar")
                .param("email", "noexiste@example.com"))
                .andExpect(status().isNotFound());

        verify(usuarioService, times(1)).getUsuarioByEmail("noexiste@example.com");
        verify(usuarioService, times(0)).eliminarUsuario("noexiste@example.com");
    }

    @Test
    void testIniciarSesion() throws Exception {
        String tokenEsperado = "abc123token";
        when(usuarioService.logIn("juan.garcia@example.com", "password123"))
                .thenReturn(Optional.of(tokenEsperado));

        mockMvc.perform(post("/api/usuario/login")
                .param("email", "juan.garcia@example.com")
                .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(content().string(tokenEsperado));

        verify(usuarioService, times(1)).logIn("juan.garcia@example.com", "password123");
    }

    @Test
    void testIniciarSesionFallido() throws Exception {
        when(usuarioService.logIn("juan.garcia@example.com", "wrongpassword"))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/api/usuario/login")
                .param("email", "juan.garcia@example.com")
                .param("password", "wrongpassword"))
                .andExpect(status().isUnauthorized());

        verify(usuarioService, times(1)).logIn("juan.garcia@example.com", "wrongpassword");
    }

    @Test
    void testIniciarSesionConExcepcion() throws Exception {
        when(usuarioService.logIn("juan.garcia@example.com", "password123"))
                .thenThrow(new IllegalArgumentException("Error en formato"));

        mockMvc.perform(post("/api/usuario/login")
                .param("email", "juan.garcia@example.com")
                .param("password", "password123"))
                .andExpect(status().isBadRequest());

        verify(usuarioService, times(1)).logIn("juan.garcia@example.com", "password123");
    }

    @Test
    void testCerrarSesion() throws Exception {
        String token = "abc123token";
        when(usuarioService.logout(token))
                .thenReturn(Optional.of(true));

        mockMvc.perform(post("/api/usuario/logout")
                .param("token", token))
                .andExpect(status().isOk());

        verify(usuarioService, times(1)).logout(token);
    }

    @Test
    void testCerrarSesionFallido() throws Exception {
        String token = "invalid-token";
        when(usuarioService.logout(token))
                .thenReturn(Optional.of(false));

        mockMvc.perform(post("/api/usuario/logout")
                .param("token", token))
                .andExpect(status().isUnauthorized());

        verify(usuarioService, times(1)).logout(token);
    }

    @Test
    void testBloquearUsuario() throws Exception {
        Usuario usuarioBloqueado = new Usuario();
        usuarioBloqueado.setId(1L);
        usuarioBloqueado.setNombre("Juan");
        usuarioBloqueado.setEmail("juan.garcia@example.com");
        usuarioBloqueado.setBloqueado(true);

        when(usuarioService.bloquearUsuario("juan.garcia@example.com"))
                .thenReturn(usuarioBloqueado);

        mockMvc.perform(put("/api/usuario/bloquear")
                .param("email", "juan.garcia@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bloqueado").value(true));

        verify(usuarioService, times(1)).bloquearUsuario("juan.garcia@example.com");
    }

    @Test
    void testDesbloquearUsuario() throws Exception {
        Usuario usuarioDesbloqueado = new Usuario();
        usuarioDesbloqueado.setId(1L);
        usuarioDesbloqueado.setNombre("Juan");
        usuarioDesbloqueado.setEmail("juan.garcia@example.com");
        usuarioDesbloqueado.setBloqueado(false);

        when(usuarioService.desbloquearUsuario("juan.garcia@example.com"))
                .thenReturn(usuarioDesbloqueado);

        mockMvc.perform(put("/api/usuario/desbloquear")
                .param("email", "juan.garcia@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bloqueado").value(false));

        verify(usuarioService, times(1)).desbloquearUsuario("juan.garcia@example.com");
    }
}