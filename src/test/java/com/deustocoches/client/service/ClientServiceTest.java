package com.deustocoches.client.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.deustocoches.model.Coche;
import com.deustocoches.model.EstadoReserva;
import com.deustocoches.model.Reserva;
import com.deustocoches.model.TipoRol;
import com.deustocoches.model.Usuario;

class ClientServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RestTemplateServiceProxy clientService;

    private String apiBaseUrl = "http://localhost:8080";
    private Usuario usuario;
    private Coche coche;
    private Reserva reserva;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Configurar URL base mediante reflexión ya que es un campo privado con @Value
        try {
            java.lang.reflect.Field field = RestTemplateServiceProxy.class.getDeclaredField("apiBaseUrl");
            field.setAccessible(true);
            field.set(clientService, apiBaseUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Configurar datos de prueba
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Juan");
        usuario.setApellido("García");
        usuario.setEmail("juan@example.com");
        usuario.setPassword("password123");
        usuario.setTlf("666777888");
        usuario.setRol(TipoRol.CLIENTE);

        coche = new Coche();
        coche.setMatricula("1234ABC");
        coche.setMarca("Toyota");
        coche.setModelo("Corolla");
        coche.setAnio(2020);
        coche.setColor("Rojo");
        coche.setPrecio(20000.0);
        coche.setDisponible(true);

        reserva = new Reserva();
        reserva.setId(1);
        reserva.setUsuario(usuario);
        reserva.setCoche(coche);
        reserva.setFecha("2023-04-23");
        reserva.setPrecioTotal(500.0);
        reserva.setEstado(EstadoReserva.PENDIENTE);
    }

    @Test
    void testLogin() {
        String token = "abc123token";
        String url = apiBaseUrl + "/api/usuario/login?email=juan@example.com&password=password123";

        when(restTemplate.postForObject(url, null, String.class))
                .thenReturn(token);

        String resultado = clientService.login("juan@example.com", "password123");

        assertNotNull(resultado);
        assertEquals(token, resultado);
        verify(restTemplate, times(1)).postForObject(url, null, String.class);
    }

    @Test
    void testRegistrarUsuario() {
        String url = apiBaseUrl + "/api/usuario/registrar";

        when(restTemplate.postForObject(url, usuario, Usuario.class))
                .thenReturn(usuario);

        Usuario resultado = clientService.registrarUsuario(usuario);

        assertNotNull(resultado);
        assertEquals(usuario, resultado);
        verify(restTemplate, times(1)).postForObject(url, usuario, Usuario.class);
    }

    @Test
    void testListarCoches() {
        String url = apiBaseUrl + "/api/coche";
        List<Coche> coches = Arrays.asList(coche);

        when(restTemplate.getForObject(url, List.class))
                .thenReturn(coches);

        List<Coche> resultado = clientService.ListarCoches();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(restTemplate, times(1)).getForObject(url, List.class);
    }

    @Test
    void testListarCochesDisponibles() {
        String url = apiBaseUrl + "/api/coche/disponibles";
        List<Coche> coches = Arrays.asList(coche);

        when(restTemplate.getForObject(url, List.class))
                .thenReturn(coches);

        List<Coche> resultado = clientService.ListarCochesDisponibles();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(restTemplate, times(1)).getForObject(url, List.class);
    }

    @Test
    void testGetCocheByMatricula() {
        String url = apiBaseUrl + "/api/coche/buscar?matricula=1234ABC";

        when(restTemplate.getForObject(url, Coche.class))
                .thenReturn(coche);

        Coche resultado = clientService.getCocheByMatricula("1234ABC");

        assertNotNull(resultado);
        assertEquals("1234ABC", resultado.getMatricula());
        verify(restTemplate, times(1)).getForObject(url, Coche.class);
    }

    @Test
    void testCrearCoche() {
        String url = apiBaseUrl + "/api/coche/crear";

        when(restTemplate.postForObject(url, coche, Coche.class))
                .thenReturn(coche);

        Coche resultado = clientService.crearCoche(coche);

        assertNotNull(resultado);
        assertEquals(coche, resultado);
        verify(restTemplate, times(1)).postForObject(url, coche, Coche.class);
    }

    @Test
    void testActualizarCoche() {
        String url = apiBaseUrl + "/api/coche/actualizar?matricula=1234ABC";

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(Coche.class)))
                .thenReturn(new ResponseEntity<>(coche, HttpStatus.OK));

        Coche resultado = clientService.actualizarCoche("1234ABC", coche);

        assertNotNull(resultado);
        assertEquals(coche, resultado);
        verify(restTemplate, times(1)).exchange(
                eq(url),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(Coche.class));
    }

    @Test
    void testEliminarCoche() {
        String url = apiBaseUrl + "/api/coche/eliminar?matricula=1234ABC";

        doNothing().when(restTemplate).delete(url);

        clientService.eliminarCoche("1234ABC");

        verify(restTemplate, times(1)).delete(url);
    }

    @Test
    void testCrearReserva() {
        String url = apiBaseUrl + "/api/reservas/pedidos";

        when(restTemplate.postForObject(url, reserva, Reserva.class))
                .thenReturn(reserva);

        Reserva resultado = clientService.crearReserva(reserva);

        assertNotNull(resultado);
        assertEquals(reserva, resultado);
        verify(restTemplate, times(1)).postForObject(url, reserva, Reserva.class);
    }

    @Test
    void testObtenerReservasPendientes() {
        String url = apiBaseUrl + "/api/reservas/pendientes";
        List<Reserva> reservas = Arrays.asList(reserva);

        when(restTemplate.getForObject(url, List.class))
                .thenReturn(reservas);

        List<Reserva> resultado = clientService.obtenerReservasPendientes();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(restTemplate, times(1)).getForObject(url, List.class);
    }

    @Test
    void testObtenerReservasCompradas() {
        String url = apiBaseUrl + "/api/reservas/compradas";
        reserva.setEstado(EstadoReserva.COMPRADA);
        List<Reserva> reservas = Arrays.asList(reserva);

        when(restTemplate.getForObject(url, List.class))
                .thenReturn(reservas);

        List<Reserva> resultado = clientService.obtenerReservasCompradas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(restTemplate, times(1)).getForObject(url, List.class);
    }

    @Test
    void testActualizarUsuario() {
        String url = apiBaseUrl + "/api/usuario/actualizar?email=juan@example.com";

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(Usuario.class)))
                .thenReturn(new ResponseEntity<>(usuario, HttpStatus.OK));

        Usuario resultado = clientService.actualizarUsuario("juan@example.com", usuario);

        assertNotNull(resultado);
        assertEquals(usuario, resultado);
        verify(restTemplate, times(1)).exchange(
                eq(url),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(Usuario.class));
    }

    @Test
    void testGetUsuarioByEmail() {
        String url = apiBaseUrl + "/api/usuario/buscar?email=juan@example.com";

        when(restTemplate.getForObject(url, Usuario.class))
                .thenReturn(usuario);

        Usuario resultado = clientService.getUsuarioByEmail("juan@example.com");

        assertNotNull(resultado);
        assertEquals("juan@example.com", resultado.getEmail());
        verify(restTemplate, times(1)).getForObject(url, Usuario.class);
    }

    @Test
    void testBloquearUsuario() {
        String url = apiBaseUrl + "/api/usuario/bloquear?email=juan@example.com";
        usuario.setBloqueado(true);

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.PUT),
                isNull(),
                eq(Usuario.class)))
                .thenReturn(new ResponseEntity<>(usuario, HttpStatus.OK));

        Usuario resultado = clientService.bloquearUsuario("juan@example.com");

        assertNotNull(resultado);
        assertTrue(resultado.isBloqueado());
        verify(restTemplate, times(1)).exchange(
                eq(url),
                eq(HttpMethod.PUT),
                isNull(),
                eq(Usuario.class));
    }

    @Test
    void testDesbloquearUsuario() {
        String url = apiBaseUrl + "/api/usuario/desbloquear?email=juan@example.com";
        usuario.setBloqueado(false);

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.PUT),
                isNull(),
                eq(Usuario.class)))
                .thenReturn(new ResponseEntity<>(usuario, HttpStatus.OK));

        Usuario resultado = clientService.desbloquearUsuario("juan@example.com");

        assertNotNull(resultado);
        assertFalse(resultado.isBloqueado());
        verify(restTemplate, times(1)).exchange(
                eq(url),
                eq(HttpMethod.PUT),
                isNull(),
                eq(Usuario.class));
    }

    @Test
    void testObtenerReservasConfirmadasPorUsuario() {
        String url = apiBaseUrl + "/api/reservas/usuario/confirmadas?email=juan@example.com";
        reserva.setEstado(EstadoReserva.COMPRADA);
        List<Reserva> reservas = Arrays.asList(reserva);

        when(restTemplate.getForObject(url, List.class))
                .thenReturn(reservas);

        List<Reserva> resultado = clientService.obtenerReservasConfirmadasPorUsuario("juan@example.com");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(restTemplate, times(1)).getForObject(url, List.class);
    }

    @Test
    void testObtenerReservasPendientesPorUsuario() {
        String url = apiBaseUrl + "/api/reservas/usuario/pendientes?email=juan@example.com";
        List<Reserva> reservas = Arrays.asList(reserva);

        when(restTemplate.getForObject(url, List.class))
                .thenReturn(reservas);

        List<Reserva> resultado = clientService.obtenerReservasPendientesPorUsuario("juan@example.com");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(restTemplate, times(1)).getForObject(url, List.class);
    }

    @Test
    void testLogout() {
        String token = "abc123token";
        String url = apiBaseUrl + "/api/usuario/logout?token=" + token;

        when(restTemplate.postForObject(eq(url), isNull(), eq(Void.class)))
                .thenReturn(null);

        clientService.logout(token);

        verify(restTemplate, times(1)).postForObject(eq(url), isNull(), eq(Void.class));
    }

}