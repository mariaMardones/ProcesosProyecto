package com.deustocoches.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.deustocoches.model.EstadoReserva;
import com.deustocoches.model.Reserva;
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
    void testEliminarReserva() {
        when(reservaRepository.existsById(1)).thenReturn(true);

        boolean resultado = reservaService.eliminarReserva(1);

        assertTrue(resultado);
        verify(reservaRepository, times(1)).existsById(1);
        verify(reservaRepository, times(1)).deleteById(1);
    }
}