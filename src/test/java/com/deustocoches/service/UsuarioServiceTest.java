package com.deustocoches.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.deustocoches.model.TipoRol;
import com.deustocoches.model.Usuario;
import com.deustocoches.repository.UsuarioRepository;

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegistrarUsuario() {
        // Preparar datos de prueba
        Usuario usuario = new Usuario();
        usuario.setNombre("Test");
        usuario.setApellido("User");
        usuario.setEmail("test@example.com");
        usuario.setPassword("password123");
        usuario.setTlf("123456789");
        usuario.setRol(TipoRol.CLIENTE);

        // Configurar comportamiento del mock
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Ejecutar el método a probar
        Usuario resultado = usuarioService.registrarUsuario(usuario);

        // Verificar resultados
        assertNotNull(resultado);
        assertEquals("Test", resultado.getNombre());
        assertEquals("test@example.com", resultado.getEmail());

        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void testActualizarUsuarioInexistente() {
        // Datos de prueba
        String emailInexistente = "noexiste@example.com";
        Usuario usuario = new Usuario();

        // Configurar mock
        when(usuarioRepository.findByEmail(emailInexistente)).thenReturn(null);

        // Verificar excepción
        Exception excepcion = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.actualizarUsuario(emailInexistente, usuario);
        });

        assertEquals("No se encontró el usuario con el email proporcionado.", excepcion.getMessage());
        verify(usuarioRepository).findByEmail(emailInexistente);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testEliminarUsuarioInexistente() {
        // Configurar mock
        when(usuarioRepository.findByEmail("noexiste@example.com")).thenReturn(null);

        // Verificar excepción
        Exception excepcion = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.eliminarUsuario("noexiste@example.com");
        });

        assertEquals("Usuario no encontrado con el email proporcionado.", excepcion.getMessage());
        verify(usuarioRepository).findByEmail("noexiste@example.com");
        verify(usuarioRepository, never()).delete(any(Usuario.class));
    }

    @Test
    void testLogIn() {
        // Datos de prueba
        String email = "usuario@ejemplo.com";
        String password = "contraseña123";
        
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setPassword(password);
        usuario.setNombre("Usuario");
        usuario.setApellido("Prueba");
        
        // Asegurarnos de que el usuario no existe en los tokens
        UsuarioService.tokens.clear();
        
        // Configurar mock
        when(usuarioRepository.findByEmail(email)).thenReturn(usuario);
        
        // Ejecutar método
        Optional<String> tokenOpt = usuarioService.logIn(email, password);
        
        // Verificar resultado
        assertTrue(tokenOpt.isPresent());
        String token = tokenOpt.get();
        assertNotNull(token);
        assertTrue(token.length() > 10);
        verify(usuarioRepository).findByEmail(email);
    }

    @Test
    void testLogInWithInvalidCredentials() {
        // Datos de prueba
        String email = "usuario@ejemplo.com";
        String wrongPassword = "contraseñaIncorrecta";
        
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setPassword("contraseña123"); // Contraseña real
        
        // Asegúrate de que los tokens estén limpios
        UsuarioService.tokens.clear();
        
        // Configurar mock correctamente
        when(usuarioRepository.findByEmail(email)).thenReturn(usuario);
        
        // Ejecutar y verificar
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.logIn(email, wrongPassword);
        });
        
        assertEquals("Contraseña incorrecta.", exception.getMessage());
        verify(usuarioRepository).findByEmail(email);
    }

    @Test
    void testLogInWithNonExistentUser() {
        // Datos de prueba
        String email = "noexiste@ejemplo.com";
        
        // Configurar mock para usuario no existente
        when(usuarioRepository.findByEmail(email)).thenReturn(null);
        
        // Ejecutar y verificar que lanza excepción
        Exception exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.logIn(email, "cualquiercontraseña");
        });
        
        assertEquals("Usuario no encontrado con el email proporcionado.", exception.getMessage());
        verify(usuarioRepository).findByEmail(email);
    }

    @Test
    void testActualizarUsuarioWithInvalidEmail() {
        // Datos de prueba
        String emailInexistente = "noexiste@ejemplo.com";
        Usuario nuevosDatos = new Usuario();
        
        // Configurar mock
        when(usuarioRepository.findByEmail(emailInexistente)).thenReturn(null);
        
        // Ejecutar y verificar
        Exception exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.actualizarUsuario(emailInexistente, nuevosDatos);
        });
        
        assertEquals("No se encontró el usuario con el email proporcionado.", exception.getMessage());
        verify(usuarioRepository).findByEmail(emailInexistente);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testBloquearUsuario() {
        // Datos de prueba
        String email = "usuario@ejemplo.com";
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setBloqueado(false);
        
        Usuario usuarioBloqueado = new Usuario();
        usuarioBloqueado.setEmail(email);
        usuarioBloqueado.setBloqueado(true);
        
        // Configurar mock
        when(usuarioRepository.findByEmail(email)).thenReturn(usuario);
        when(usuarioRepository.save(usuario)).thenReturn(usuarioBloqueado);
        
        // Ejecutar
        Usuario resultado = usuarioService.bloquearUsuario(email);
        
        // Verificar
        assertNotNull(resultado);
        assertTrue(resultado.isBloqueado());
        verify(usuarioRepository, times(2)).findByEmail(email);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void testBloquearUsuarioInexistente() {
        // Configurar mock
        when(usuarioRepository.findByEmail("noexiste@ejemplo.com")).thenReturn(null);
        
        // Ejecutar y verificar
        Exception exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.bloquearUsuario("noexiste@ejemplo.com");
        });
        
        assertEquals("Usuario no encontrado con el email proporcionado.", exception.getMessage());
        verify(usuarioRepository).findByEmail("noexiste@ejemplo.com");
    }

    @Test
    void testDesbloquearUsuario() {
        // Datos de prueba
        String email = "usuario@ejemplo.com";
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setBloqueado(true);
        
        Usuario usuarioDesbloqueado = new Usuario();
        usuarioDesbloqueado.setEmail(email);
        usuarioDesbloqueado.setBloqueado(false);
        
        // Configurar mock
        when(usuarioRepository.findByEmail(email)).thenReturn(usuario);
        when(usuarioRepository.save(usuario)).thenReturn(usuarioDesbloqueado);
        
        // Ejecutar
        Usuario resultado = usuarioService.desbloquearUsuario(email);
        
        // Verificar
        assertNotNull(resultado);
        assertFalse(resultado.isBloqueado());
        verify(usuarioRepository, times(2)).findByEmail(email);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void testDesbloquearUsuarioInexistente() {
        // Configurar mock
        when(usuarioRepository.findByEmail("noexiste@ejemplo.com")).thenReturn(null);
        
        // Ejecutar y verificar
        Exception exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.desbloquearUsuario("noexiste@ejemplo.com");
        });
        
        assertEquals("Usuario no encontrado con el email proporcionado.", exception.getMessage());
        verify(usuarioRepository).findByEmail("noexiste@ejemplo.com");
    }

    @Test
    void testLogout() {
        // Datos de prueba
        String token = "token123";
        
        // Añadir el token al mapa antes de probarlo
        // Esto es crucial ya que el test espera que el token exista
        UsuarioService.tokens.put(token, new Usuario());
        
        // Ejecutar
        boolean resultado = usuarioService.logout(token).orElse(false);
        
        // Verificar
        assertTrue(resultado);
        assertFalse(usuarioService.getTokens().containsKey(token));
    }

    @Test
    void testLogoutConTokenInexistente() {
        // Ejecutar con token que no existe
        boolean resultado = usuarioService.logout("tokenInexistente").orElse(false);
        
        // Verificar que devuelve false
        assertFalse(resultado);
    }

    @Test
    void testGenerarToken() {
        // Datos de prueba
        Usuario usuario = new Usuario();
        usuario.setEmail("usuario@ejemplo.com");
        
        // Ejecutar
        String token = usuarioService.generarToken(usuario);
        
        // Verificar
        assertNotNull(token);
        assertTrue(token.length() > 10);
        assertTrue(usuarioService.getTokens().containsKey(token));
    }

    @Test
    void testListarUsuariosRegistrados() {
        // Datos de prueba
        List<Usuario> usuariosEsperados = Arrays.asList(
                new Usuario(), new Usuario());
        
        // Configurar mock
        when(usuarioRepository.findAll()).thenReturn(usuariosEsperados);
        
        // Ejecutar
        List<Usuario> resultado = usuarioService.listarUsuariosResgistrados();
        
        // Verificar
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(usuarioRepository).findAll();
    }

    @Test
    void testGetTokens() {
        // Ejecutar
        Set<String> tokens = usuarioService.getTokens().keySet();
        
        // Verificar
        assertNotNull(tokens);
        // El conjunto puede estar vacío o tener elementos dependiendo del estado
    }
}