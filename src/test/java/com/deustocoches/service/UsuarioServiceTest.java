package com.deustocoches.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        // Verificar que el método save fue llamado una vez
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }
}