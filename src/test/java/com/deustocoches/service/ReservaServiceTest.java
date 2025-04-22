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
        // Preparar datos de prueba
        Reserva reserva1 = new Reserva();
        reserva1.setEstado(EstadoReserva.COMPRADA);

        Reserva reserva2 = new Reserva();
        reserva2.setEstado(EstadoReserva.COMPRADA);

        when(reservaRepository.findByEstado(EstadoReserva.COMPRADA))
            .thenReturn(Arrays.asList(reserva1, reserva2));

        // Ejecutar el método
        List<Reserva> reservas = reservaService.obtenerReservasConfirmadas();

        // Verificar resultados
        assertNotNull(reservas);
        assertEquals(2, reservas.size());
        verify(reservaRepository, times(1)).findByEstado(EstadoReserva.COMPRADA);
    }

    @Test
    void testObtenerReservaPorId() {
        // Preparar datos de prueba
        Reserva reserva = new Reserva();
        reserva.setId(1);

        when(reservaRepository.findById(1)).thenReturn(Optional.of(reserva));

        // Ejecutar el método
        Optional<Reserva> resultado = reservaService.obtenerReservaPorId(1);

        // Verificar resultados
        assertTrue(resultado.isPresent());
        assertEquals(1, resultado.get().getId());
        verify(reservaRepository, times(1)).findById(1);
    }

    @Test
    void testCrearReserva() {
        // Preparar datos de prueba
        Reserva reserva = new Reserva();
        reserva.setId(1);

        when(reservaRepository.save(reserva)).thenReturn(reserva);

        // Ejecutar el método
        Reserva resultado = reservaService.crearReserva(reserva);

        // Verificar resultados
        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        verify(reservaRepository, times(1)).save(reserva);
    }

    @Test
    void testActualizarReserva() {
        // Preparar datos de prueba
        Reserva reservaExistente = new Reserva();
        reservaExistente.setId(1);

        Reserva detallesReserva = new Reserva();
        detallesReserva.setFecha("2025-04-22");
        detallesReserva.setPrecioTotal(100.0);
        detallesReserva.setEstado(EstadoReserva.COMPRADA);

        when(reservaRepository.findById(1)).thenReturn(Optional.of(reservaExistente));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaExistente);

        // Ejecutar el método
        Reserva resultado = reservaService.actualizarReserva(1, detallesReserva);

        // Verificar resultados
        assertNotNull(resultado);
        assertEquals(EstadoReserva.COMPRADA, resultado.getEstado());
        verify(reservaRepository, times(1)).findById(1);
        verify(reservaRepository, times(1)).save(any(Reserva.class));
    }

    @Test
    void testEliminarReserva() {
        // Preparar datos de prueba
        when(reservaRepository.existsById(1)).thenReturn(true);

        // Ejecutar el método
        boolean resultado = reservaService.eliminarReserva(1);

        // Verificar resultados
        assertTrue(resultado);
        verify(reservaRepository, times(1)).existsById(1);
        verify(reservaRepository, times(1)).deleteById(1);
    }
}