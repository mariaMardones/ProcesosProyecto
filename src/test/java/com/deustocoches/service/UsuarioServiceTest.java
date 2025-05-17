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
        Usuario usuario = new Usuario();
        usuario.setNombre("Test");
        usuario.setApellido("User");
        usuario.setEmail("test@example.com");
        usuario.setPassword("password123");
        usuario.setTlf("123456789");
        usuario.setRol(TipoRol.CLIENTE);

        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario resultado = usuarioService.registrarUsuario(usuario);

        assertNotNull(resultado);
        assertEquals("Test", resultado.getNombre());
        assertEquals("test@example.com", resultado.getEmail());

        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void testRegistrarUsuario_ExcepcionEmailYaRegistrado() {
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setEmail("test@correo.com");
        usuarioExistente.setTlf("123456789");

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setEmail("test@correo.com");
        nuevoUsuario.setTlf("987654321");

        when(usuarioRepository.findAll()).thenReturn(List.of(usuarioExistente));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.registrarUsuario(nuevoUsuario);
        });
        assertEquals("Ya existe un usuario registrado con ese email.", ex.getMessage());
    }

    @Test
    void testRegistrarUsuario_ExcepcionTelefonoYaRegistrado() {
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setEmail("test@correo.com");
        usuarioExistente.setTlf("123456789");

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setEmail("otro@correo.com");
        nuevoUsuario.setTlf("123456789");

        when(usuarioRepository.findAll()).thenReturn(List.of(usuarioExistente));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.registrarUsuario(nuevoUsuario);
        });
        assertEquals("Ya existe un usuario registrado con ese teléfono.", ex.getMessage());
    }

    @Test
    void testRegistrarUsuario_SinRolPorDefectoCliente() {
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setEmail("nuevo@correo.com");
        nuevoUsuario.setTlf("111222333");
        nuevoUsuario.setRol(null);

        when(usuarioRepository.findAll()).thenReturn(List.of());
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario guardado = usuarioService.registrarUsuario(nuevoUsuario);

        assertEquals(TipoRol.CLIENTE, guardado.getRol());
    }

    @Test
    void testActualizarUsuarioInexistente() {
        String emailInexistente = "noexiste@example.com";
        Usuario usuario = new Usuario();

        when(usuarioRepository.findByEmail(emailInexistente)).thenReturn(null);

        Exception excepcion = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.actualizarUsuario(emailInexistente, usuario);
        });

        assertEquals("No se encontró el usuario con el email proporcionado.", excepcion.getMessage());
        verify(usuarioRepository).findByEmail(emailInexistente);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testEliminarUsuarioInexistente() {
        when(usuarioRepository.findByEmail("noexiste@example.com")).thenReturn(null);

        Exception excepcion = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.eliminarUsuario("noexiste@example.com");
        });

        assertEquals("Usuario no encontrado con el email proporcionado.", excepcion.getMessage());
        verify(usuarioRepository).findByEmail("noexiste@example.com");
        verify(usuarioRepository, never()).delete(any(Usuario.class));
    }

    @Test
    void testLogIn() {
        String email = "usuario@ejemplo.com";
        String password = "contraseña123";
        
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setPassword(password);
        usuario.setNombre("Usuario");
        usuario.setApellido("Prueba");
        
        UsuarioService.tokens.clear();
        
        when(usuarioRepository.findByEmail(email)).thenReturn(usuario);
        
        Optional<String> tokenOpt = usuarioService.logIn(email, password);
        
        assertTrue(tokenOpt.isPresent());
        String token = tokenOpt.get();
        assertNotNull(token);
        assertTrue(token.length() > 10);
        verify(usuarioRepository).findByEmail(email);
    }

    @Test
    void testLogInWithInvalidCredentials() {
        String email = "usuario@ejemplo.com";
        String wrongPassword = "contraseñaIncorrecta";
        
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setPassword("contraseña123");
        
        UsuarioService.tokens.clear();
        
        when(usuarioRepository.findByEmail(email)).thenReturn(usuario);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.logIn(email, wrongPassword);
        });
        
        assertEquals("Contraseña incorrecta.", exception.getMessage());
        verify(usuarioRepository).findByEmail(email);
    }

    @Test
    void testLogInWithNonExistentUser() {
        String email = "noexiste@ejemplo.com";
        
        when(usuarioRepository.findByEmail(email)).thenReturn(null);
        
        Exception exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.logIn(email, "cualquiercontraseña");
        });
        
        assertEquals("Usuario no encontrado con el email proporcionado.", exception.getMessage());
        verify(usuarioRepository).findByEmail(email);
    }

    @Test
    void testLogIn_EmailYaEnTokensDevuelveEmpty() {
        Usuario usuario = new Usuario();
        usuario.setEmail("test@correo.com");
        usuario.setPassword("1234");
        UsuarioService.tokens.put("token123", usuario);

        Optional<String> result = usuarioService.logIn("test@correo.com", "1234");
        assertTrue(result.isEmpty());
    }

    @Test
    void testLogIn_UsuarioBloqueadoLanzaExcepcion() {
        Usuario usuario = new Usuario();
        usuario.setEmail("bloqueado@correo.com");
        usuario.setPassword("pass");
        usuario.setBloqueado(true);

        when(usuarioRepository.findByEmail("bloqueado@correo.com")).thenReturn(usuario);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.logIn("bloqueado@correo.com", "pass");
        });
        assertEquals("Usuario bloqueado.", ex.getMessage());
    }

    @Test
    void testActualizarUsuarioWithInvalidEmail() {
        String emailInexistente = "noexiste@ejemplo.com";
        Usuario nuevosDatos = new Usuario();
        
        when(usuarioRepository.findByEmail(emailInexistente)).thenReturn(null);
        
        Exception exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.actualizarUsuario(emailInexistente, nuevosDatos);
        });
        
        assertEquals("No se encontró el usuario con el email proporcionado.", exception.getMessage());
        verify(usuarioRepository).findByEmail(emailInexistente);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testBloquearUsuario() {
        String email = "usuario@ejemplo.com";
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setBloqueado(false);
        
        Usuario usuarioBloqueado = new Usuario();
        usuarioBloqueado.setEmail(email);
        usuarioBloqueado.setBloqueado(true);
        
        when(usuarioRepository.findByEmail(email)).thenReturn(usuario);
        when(usuarioRepository.save(usuario)).thenReturn(usuarioBloqueado);
        
        Usuario resultado = usuarioService.bloquearUsuario(email);
        
        assertNotNull(resultado);
        assertTrue(resultado.isBloqueado());
        verify(usuarioRepository, times(2)).findByEmail(email);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void testBloquearUsuarioInexistente() {
        when(usuarioRepository.findByEmail("noexiste@ejemplo.com")).thenReturn(null);
        
        Exception exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.bloquearUsuario("noexiste@ejemplo.com");
        });
        
        assertEquals("Usuario no encontrado con el email proporcionado.", exception.getMessage());
        verify(usuarioRepository).findByEmail("noexiste@ejemplo.com");
    }

    @Test
    void testDesbloquearUsuario() {
        String email = "usuario@ejemplo.com";
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setBloqueado(true);
        
        Usuario usuarioDesbloqueado = new Usuario();
        usuarioDesbloqueado.setEmail(email);
        usuarioDesbloqueado.setBloqueado(false);
        
        when(usuarioRepository.findByEmail(email)).thenReturn(usuario);
        when(usuarioRepository.save(usuario)).thenReturn(usuarioDesbloqueado);
        
        Usuario resultado = usuarioService.desbloquearUsuario(email);
        
        assertNotNull(resultado);
        assertFalse(resultado.isBloqueado());
        verify(usuarioRepository, times(2)).findByEmail(email);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void testDesbloquearUsuarioInexistente() {
        when(usuarioRepository.findByEmail("noexiste@ejemplo.com")).thenReturn(null);
        
        Exception exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.desbloquearUsuario("noexiste@ejemplo.com");
        });
        
        assertEquals("Usuario no encontrado con el email proporcionado.", exception.getMessage());
        verify(usuarioRepository).findByEmail("noexiste@ejemplo.com");
    }

    @Test
    void testLogout() {
        String token = "token123";
        

        UsuarioService.tokens.put(token, new Usuario());
        
        boolean resultado = usuarioService.logout(token).orElse(false);
        
        assertTrue(resultado);
        assertFalse(usuarioService.getTokens().containsKey(token));
    }

    @Test
    void testLogoutConTokenInexistente() {
        boolean resultado = usuarioService.logout("tokenInexistente").orElse(false);
        
        assertFalse(resultado);
    }

    @Test
    void testGenerarToken() {
        Usuario usuario = new Usuario();
        usuario.setEmail("usuario@ejemplo.com");
        
        String token = usuarioService.generarToken(usuario);
        
        assertNotNull(token);
        assertTrue(token.length() > 10);
        assertTrue(usuarioService.getTokens().containsKey(token));
    }

    @Test
    void testListarUsuariosRegistrados() {
        List<Usuario> usuariosEsperados = Arrays.asList(
                new Usuario(), new Usuario());
        
        when(usuarioRepository.findAll()).thenReturn(usuariosEsperados);
        
        List<Usuario> resultado = usuarioService.listarUsuariosResgistrados();
        
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(usuarioRepository).findAll();
    }

    @Test
    void testGetTokens() {
        Set<String> tokens = usuarioService.getTokens().keySet();
        
        assertNotNull(tokens);
    }

    @Test
    void testCrearAdmin_CambiaRolAAdmin() {
        Usuario usuario = new Usuario();
        usuario.setEmail("admin@correo.com");
        usuario.setRol(TipoRol.CLIENTE);

        when(usuarioRepository.findByEmail("admin@correo.com")).thenReturn(usuario);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario actualizado = usuarioService.crearAdmin("admin@correo.com");

        assertEquals(TipoRol.ADMIN, actualizado.getRol());
    }

    @Test
    void testCrearAdmin_UsuarioNoEncontrado() {
        when(usuarioRepository.findByEmail("noexiste@correo.com")).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.crearAdmin("noexiste@correo.com");
        });
        assertEquals("Usuario no encontrado con el email proporcionado.", ex.getMessage());
    }

    @Test
    void testEliminarAdmin_CambiaRolACliente() {
        Usuario usuario = new Usuario();
        usuario.setEmail("cliente@correo.com");
        usuario.setRol(TipoRol.ADMIN);

        when(usuarioRepository.findByEmail("cliente@correo.com")).thenReturn(usuario);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario actualizado = usuarioService.eliminarAdmin("cliente@correo.com");

        assertEquals(TipoRol.CLIENTE, actualizado.getRol());
    }

    @Test
    void testEliminarAdmin_UsuarioNoEncontrado() {
        when(usuarioRepository.findByEmail("noexiste@correo.com")).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.eliminarAdmin("noexiste@correo.com");
        });
        assertEquals("Usuario no encontrado con el email proporcionado.", ex.getMessage());
    }
}