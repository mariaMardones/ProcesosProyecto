package com.deustocoches.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.deustocoches.model.Coche;
import com.deustocoches.model.EstadoReserva;
import com.deustocoches.model.Reserva;
import com.deustocoches.model.Usuario;
import com.deustocoches.repository.ReservaRepository;

class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @InjectMocks
    private ReservaService reservaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testObtenerReservasConfirmadas() {
        Reserva reserva1 = new Reserva();
        reserva1.setEstado(EstadoReserva.COMPRADA);

        Reserva reserva2 = new Reserva();
        reserva2.setEstado(EstadoReserva.COMPRADA);

        when(reservaRepository.findByEstado(EstadoReserva.COMPRADA))
            .thenReturn(Arrays.asList(reserva1, reserva2));

        List<Reserva> reservas = reservaService.obtenerReservasConfirmadas();

        assertNotNull(reservas);
        assertEquals(2, reservas.size());
        verify(reservaRepository, times(1)).findByEstado(EstadoReserva.COMPRADA);
    }

    @Test
    void testObtenerReservaPorId() {
        Reserva reserva = new Reserva();
        reserva.setId(1);

        when(reservaRepository.findById(1)).thenReturn(Optional.of(reserva));

        Optional<Reserva> resultado = reservaService.obtenerReservaPorId(1);

        assertTrue(resultado.isPresent());
        assertEquals(1, resultado.get().getId());
        verify(reservaRepository, times(1)).findById(1);
    }

    @Test
    void testCrearReserva() {
        Reserva reserva = new Reserva();
        reserva.setId(1);

        when(reservaRepository.save(reserva)).thenReturn(reserva);

        Reserva resultado = reservaService.crearReserva(reserva);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        verify(reservaRepository, times(1)).save(reserva);
    }

    @Test
    void testActualizarReserva_exito() {
        Integer id = 1;
        Reserva reservaExistente = new Reserva();
        reservaExistente.setId(id);

        Reserva detalles = new Reserva();
        detalles.setFecha("2024-05-17");
        detalles.setPrecioTotal(100.0);
        detalles.setEstado(EstadoReserva.COMPRADA);
        detalles.setUsuario(new Usuario());
        detalles.setCoche(new Coche());

        when(reservaRepository.findById(id)).thenReturn(Optional.of(reservaExistente));
        when(reservaRepository.save(reservaExistente)).thenReturn(reservaExistente);

        Reserva resultado = reservaService.actualizarReserva(id, detalles);

        assertEquals(detalles.getFecha(), resultado.getFecha());
        assertEquals(detalles.getPrecioTotal(), resultado.getPrecioTotal());
        assertEquals(detalles.getEstado(), resultado.getEstado());
        assertEquals(detalles.getUsuario(), resultado.getUsuario());
        assertEquals(detalles.getCoche(), resultado.getCoche());
        verify(reservaRepository).findById(id);
        verify(reservaRepository).save(reservaExistente);
    }

    @Test
    void testActualizarReserva() {
        Reserva reservaExistente = new Reserva();
        reservaExistente.setId(1);

        Reserva detallesReserva = new Reserva();
        detallesReserva.setFecha("2025-04-22");
        detallesReserva.setPrecioTotal(100.0);
        detallesReserva.setEstado(EstadoReserva.COMPRADA);

        when(reservaRepository.findById(1)).thenReturn(Optional.of(reservaExistente));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaExistente);

        Reserva resultado = reservaService.actualizarReserva(1, detallesReserva);

        assertNotNull(resultado);
        assertEquals(EstadoReserva.COMPRADA, resultado.getEstado());
        verify(reservaRepository, times(1)).findById(1);
        verify(reservaRepository, times(1)).save(any(Reserva.class));
    }

    @Test
    void testActualizarReserva_noEncontrada_lanzaExcepcion() {
        Integer id = 2;
        Reserva detalles = new Reserva();

        when(reservaRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            reservaService.actualizarReserva(id, detalles);
        });

        assertEquals("Reserva no encontrada", ex.getMessage());
        verify(reservaRepository).findById(id);
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void testEliminarReserva_true() {
        Integer id = 1;
        when(reservaRepository.existsById(id)).thenReturn(true);

        boolean resultado = reservaService.eliminarReserva(id);

        assertTrue(resultado);
        verify(reservaRepository).existsById(id);
        verify(reservaRepository).deleteById(id);
    }

    @Test
    void testEliminarReserva_false() {
        Integer id = 2;
        when(reservaRepository.existsById(id)).thenReturn(false);

        boolean resultado = reservaService.eliminarReserva(id);

        assertFalse(resultado);
        verify(reservaRepository).existsById(id);
        verify(reservaRepository, never()).deleteById(any());
    }

    @Test
    void testHacerPedido() {
        Reserva reserva = new Reserva();
        when(reservaRepository.save(reserva)).thenReturn(reserva);

        Reserva resultado = reservaService.hacerPedido(reserva);

        assertNotNull(resultado);
        assertEquals(reserva, resultado);
        verify(reservaRepository).save(reserva);
    }

    @Test
    void testObtenerReservasPorFecha() {
        String fecha = "2024-05-17";
        Reserva reserva1 = new Reserva();
        reserva1.setFecha(fecha);
        Reserva reserva2 = new Reserva();
        reserva2.setFecha(fecha);

        List<Reserva> reservas = Arrays.asList(reserva1, reserva2);
        when(reservaRepository.findByFecha(fecha)).thenReturn(reservas);

        List<Reserva> resultado = reservaService.obtenerReservasPorFecha(fecha);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(fecha, resultado.get(0).getFecha());
        assertEquals(fecha, resultado.get(1).getFecha());
        verify(reservaRepository).findByFecha(fecha);
    }

    @Test
    void testObtenerReservasPorRangoFechas() {
        String desde = "2024-05-01";
        String hasta = "2024-05-31";
        Reserva reserva1 = new Reserva();
        reserva1.setFecha("2024-05-10");
        Reserva reserva2 = new Reserva();
        reserva2.setFecha("2024-05-20");

        List<Reserva> reservas = Arrays.asList(reserva1, reserva2);
        when(reservaRepository.findByFechaBetween(desde, hasta)).thenReturn(reservas);

        List<Reserva> resultado = reservaService.obtenerReservasPorRangoFechas(desde, hasta);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("2024-05-10", resultado.get(0).getFecha());
        assertEquals("2024-05-20", resultado.get(1).getFecha());
        verify(reservaRepository).findByFechaBetween(desde, hasta);
    }

    @Test
    void testObtenerReservasCompradasPorUsuario() {
        String email = "usuario@correo.com";
        Reserva reserva1 = new Reserva();
        reserva1.setEstado(EstadoReserva.COMPRADA);
        Reserva reserva2 = new Reserva();
        reserva2.setEstado(EstadoReserva.COMPRADA);

        List<Reserva> reservas = Arrays.asList(reserva1, reserva2);
        when(reservaRepository.findByUsuarioEmailAndEstado(email, EstadoReserva.COMPRADA)).thenReturn(reservas);

        List<Reserva> resultado = reservaService.obtenerReservasCompradasPorUsuario(email);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(EstadoReserva.COMPRADA, resultado.get(0).getEstado());
        assertEquals(EstadoReserva.COMPRADA, resultado.get(1).getEstado());
        verify(reservaRepository).findByUsuarioEmailAndEstado(email, EstadoReserva.COMPRADA);
    }

    @Test
    void testObtenerReservasPendientesPorUsuario() {
        String email = "usuario@correo.com";
        Reserva reserva1 = new Reserva();
        reserva1.setEstado(EstadoReserva.PENDIENTE);
        Reserva reserva2 = new Reserva();
        reserva2.setEstado(EstadoReserva.PENDIENTE);

        List<Reserva> reservas = Arrays.asList(reserva1, reserva2);
        when(reservaRepository.findByUsuarioEmailAndEstado(email, EstadoReserva.PENDIENTE)).thenReturn(reservas);

        List<Reserva> resultado = reservaService.obtenerReservasPendientesPorUsuario(email);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(EstadoReserva.PENDIENTE, resultado.get(0).getEstado());
        assertEquals(EstadoReserva.PENDIENTE, resultado.get(1).getEstado());
        verify(reservaRepository).findByUsuarioEmailAndEstado(email, EstadoReserva.PENDIENTE);
    }

    @Test
    void testObtenerCompradas() {
        Reserva reserva1 = new Reserva();
        reserva1.setEstado(EstadoReserva.COMPRADA);
        Reserva reserva2 = new Reserva();
        reserva2.setEstado(EstadoReserva.COMPRADA);

        List<Reserva> reservas = Arrays.asList(reserva1, reserva2);
        when(reservaRepository.findByEstado(EstadoReserva.COMPRADA)).thenReturn(reservas);

        List<Reserva> resultado = reservaService.obtenerCompradas();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(EstadoReserva.COMPRADA, resultado.get(0).getEstado());
        assertEquals(EstadoReserva.COMPRADA, resultado.get(1).getEstado());
        verify(reservaRepository).findByEstado(EstadoReserva.COMPRADA);
    }

    @Test
    void testObtenerTodasReservas() {
        Reserva reserva1 = new Reserva();
        Reserva reserva2 = new Reserva();

        List<Reserva> reservas = Arrays.asList(reserva1, reserva2);
        when(reservaRepository.findAll()).thenReturn(reservas);

        List<Reserva> resultado = reservaService.obtenerTodasReservas();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(reservaRepository).findAll();
    }

    @Test
    void testObtenerReservasPendientes() {
        Reserva reserva1 = new Reserva();
        reserva1.setEstado(EstadoReserva.PENDIENTE);
        Reserva reserva2 = new Reserva();
        reserva2.setEstado(EstadoReserva.PENDIENTE);

        List<Reserva> reservas = Arrays.asList(reserva1, reserva2);
        when(reservaRepository.findByEstado(EstadoReserva.PENDIENTE)).thenReturn(reservas);

        List<Reserva> resultado = reservaService.obtenerPendientes();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(EstadoReserva.PENDIENTE, resultado.get(0).getEstado());
        assertEquals(EstadoReserva.PENDIENTE, resultado.get(1).getEstado());
        verify(reservaRepository).findByEstado(EstadoReserva.PENDIENTE);
    }

}