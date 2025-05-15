package com.deustocoches.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

import com.deustocoches.model.Coche;
import com.deustocoches.model.EstadoReserva;
import com.deustocoches.model.Reserva;
import com.deustocoches.model.TipoRol;
import com.deustocoches.model.Usuario;
import com.deustocoches.service.ReservaService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("deprecation")
@WebMvcTest(ReservaController.class)
public class ReservaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservaService reservaService;

    @MockBean
    private CocheController cocheController;

    @Autowired
    private ObjectMapper objectMapper;

    private Reserva reserva;
    private Usuario usuario;
    private Coche coche;

    @BeforeEach
    void setUp() {
        // Configurar Usuario
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Juan");
        usuario.setApellido("García");
        usuario.setEmail("juan.garcia@example.com");
        usuario.setPassword("password123");
        usuario.setTlf("666777888");
        usuario.setRol(TipoRol.CLIENTE);

        // Configurar Coche
        coche = new Coche();
        coche.setMatricula("1234ABC");
        coche.setMarca("Toyota");
        coche.setModelo("Corolla");
        coche.setAnio(2020);
        coche.setColor("Azul");
        coche.setPrecio(20000.0);
        coche.setDisponible(true);

        // Configurar Reserva
        reserva = new Reserva();
        reserva.setId(1);
        reserva.setUsuario(usuario);
        reserva.setCoche(coche);
        reserva.setFecha("2023-04-23");
        reserva.setPrecioTotal(500.0);
        reserva.setEstado(EstadoReserva.PENDIENTE);
    }
    
    @Test
    void testCrearReserva() throws Exception {
        when(reservaService.crearReserva(any(Reserva.class)))
                .thenReturn(reserva);

        mockMvc.perform(post("/api/reservas/crear")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reserva)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.precioTotal").value(500.0));

        verify(reservaService, times(1)).crearReserva(any(Reserva.class));
    }

    @Test
    void testActualizarReserva() throws Exception {
        Reserva reservaActualizada = new Reserva();
        reservaActualizada.setId(1);
        reservaActualizada.setUsuario(usuario);
        reservaActualizada.setCoche(coche);
        reservaActualizada.setFecha("2023-04-23");
        reservaActualizada.setPrecioTotal(550.0);
        reservaActualizada.setEstado(EstadoReserva.COMPRADA);

        when(reservaService.actualizarReserva(eq(1), any(Reserva.class)))
                .thenReturn(reservaActualizada);

        mockMvc.perform(put("/api/reservas/actualizar/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservaActualizada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.precioTotal").value(550.0))
                .andExpect(jsonPath("$.estado").exists());

        verify(reservaService, times(1)).actualizarReserva(eq(1), any(Reserva.class));
    }

    @Test
    void testEliminarReserva() throws Exception {
        when(reservaService.obtenerReservaPorId(1))
                .thenReturn(Optional.of(reserva));
        
        when(reservaService.eliminarReserva(1))
                .thenReturn(true);

        mockMvc.perform(delete("/api/reservas/eliminar/1"))
                .andExpect(status().isNoContent());

        verify(reservaService, times(1)).obtenerReservaPorId(1);
        verify(reservaService, times(1)).eliminarReserva(1);
    }

    @Test
    void testHacerPedido() throws Exception {
        when(reservaService.hacerPedido(any(Reserva.class)))
                .thenReturn(reserva);

        mockMvc.perform(post("/api/reservas/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reserva)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.precioTotal").value(500.0));

        verify(reservaService, times(1)).hacerPedido(any(Reserva.class));
    }

    @Test
    void testObtenerReservasPendientes() throws Exception {
        when(reservaService.obtenerPendientes())
                .thenReturn(Arrays.asList(reserva));

        mockMvc.perform(get("/api/reservas/pendientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].estado").exists());

        verify(reservaService, times(1)).obtenerPendientes();
    }

    @Test
    void testObtenerReservasCompradas() throws Exception {
        Reserva reservaComprada = new Reserva();
        reservaComprada.setId(2);
        reservaComprada.setUsuario(usuario);
        reservaComprada.setCoche(coche);
        reservaComprada.setFecha("2023-04-24");
        reservaComprada.setPrecioTotal(600.0);
        reservaComprada.setEstado(EstadoReserva.COMPRADA);

        when(reservaService.obtenerCompradas())
                .thenReturn(Arrays.asList(reservaComprada));

        mockMvc.perform(get("/api/reservas/compradas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].estado").exists());

        verify(reservaService, times(1)).obtenerCompradas();
    }

    @Test
    void testObtenerReservasPendientesPorUsuario() throws Exception {
        when(reservaService.obtenerReservasPendientesPorUsuario("juan.garcia@example.com"))
                .thenReturn(Arrays.asList(reserva));

        mockMvc.perform(get("/api/reservas/usuario/pendientes")
                .param("email", "juan.garcia@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].estado").exists());

        verify(reservaService, times(1)).obtenerReservasPendientesPorUsuario("juan.garcia@example.com");
    }

    @Test
    void testObtenerReservasCompradasPorUsuario() throws Exception {
        Reserva reservaComprada = new Reserva();
        reservaComprada.setId(2);
        reservaComprada.setUsuario(usuario);
        reservaComprada.setCoche(coche);
        reservaComprada.setFecha("2023-04-24");
        reservaComprada.setPrecioTotal(600.0);
        reservaComprada.setEstado(EstadoReserva.COMPRADA);

        when(reservaService.obtenerReservasCompradasPorUsuario("juan.garcia@example.com"))
                .thenReturn(Arrays.asList(reservaComprada));

        mockMvc.perform(get("/api/reservas/usuario/confirmadas")
                .param("email", "juan.garcia@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].estado").exists());

        verify(reservaService, times(1)).obtenerReservasCompradasPorUsuario("juan.garcia@example.com");
    }
}