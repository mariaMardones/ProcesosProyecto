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

import com.deustocoches.model.Coche;
import com.deustocoches.repository.CocheRepository;

class CocheServiceTest {

    @Mock
    private CocheRepository cocheRepository;

    @InjectMocks
    private CocheService cocheService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListarCoches() {
        Coche coche1 = new Coche();
        Coche coche2 = new Coche();
        when(cocheRepository.findAll()).thenReturn(Arrays.asList(coche1, coche2));

        List<Coche> coches = cocheService.ListarCoches();

        assertNotNull(coches);
        assertEquals(2, coches.size());
        verify(cocheRepository, times(1)).findAll();
    }

    @Test
    void testListarCochesDisponibles() {
        Coche coche1 = new Coche();
        coche1.setDisponible(true);
        Coche coche2 = new Coche();
        coche2.setDisponible(false);
        when(cocheRepository.findAll()).thenReturn(Arrays.asList(coche1, coche2));

        List<Coche> cochesDisponibles = cocheService.ListarCochesDisponibles();

        assertNotNull(cochesDisponibles);
        assertEquals(1, cochesDisponibles.size());
        assertTrue(cochesDisponibles.get(0).isDisponible());
        verify(cocheRepository, times(1)).findAll();
    }

    @Test
    void testGuardarCoche() {
        Coche coche = new Coche();
        coche.setMatricula("1234ABC");
        when(cocheRepository.findByMatricula("1234ABC")).thenReturn(null);
        when(cocheRepository.save(coche)).thenReturn(coche);

        Coche resultado = cocheService.guardarCoche(coche);

        assertNotNull(resultado);
        assertEquals("1234ABC", resultado.getMatricula());
        verify(cocheRepository, times(1)).findByMatricula("1234ABC");
        verify(cocheRepository, times(1)).save(coche);
    }

    @Test
    void testActualizarCoche() {
        Coche cocheExistente = new Coche();
        cocheExistente.setMatricula("1234ABC");

        Coche detallesCoche = new Coche();
        detallesCoche.setColor("Rojo");

        when(cocheRepository.findById("1234ABC")).thenReturn(Optional.of(cocheExistente));
        when(cocheRepository.save(any(Coche.class))).thenReturn(cocheExistente);

        Coche resultado = cocheService.actualizarCoche("1234ABC", detallesCoche);

        assertNotNull(resultado);
        assertEquals("Rojo", resultado.getColor());
        verify(cocheRepository, times(1)).findById("1234ABC");
        verify(cocheRepository, times(1)).save(any(Coche.class));
    }

    @Test
    void testEliminarCoche() {
        when(cocheRepository.existsById("1234ABC")).thenReturn(true);

        cocheService.eliminarCoche("1234ABC");

        verify(cocheRepository, times(1)).existsById("1234ABC");
        verify(cocheRepository, times(1)).deleteById("1234ABC");
    }
    
    
    @Test
    void testAplicarDescuento_Exito() {
        String matricula = "1234ABC";
        double descuento = 15.0;

        Coche coche = new Coche();
        coche.setMatricula(matricula);
        coche.setDescuento(0.0);

        when(cocheRepository.findById(matricula)).thenReturn(Optional.of(coche));
        when(cocheRepository.save(any(Coche.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Coche resultado = cocheService.aplicarDescuento(matricula, descuento);

        assertNotNull(resultado);
        assertEquals(descuento, resultado.getDescuento());
        verify(cocheRepository).findById(matricula);
        verify(cocheRepository).save(any(Coche.class));
    }

    @Test
    void testAplicarDescuento_CocheNoEncontrado() {
        String matricula = "NOEXISTE";
        double descuento = 10.0;

        when(cocheRepository.findById(matricula)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cocheService.aplicarDescuento(matricula, descuento);
        });

        assertEquals("Coche con matrícula " + matricula + " no encontrado", exception.getMessage());
        verify(cocheRepository).findById(matricula);
        verify(cocheRepository, never()).save(any());
    }

    @Test
    void testEliminarDescuento_Exito() {
        String matricula = "1234ABC";

        Coche coche = new Coche();
        coche.setMatricula(matricula);
        coche.setDescuento(10.0);

        when(cocheRepository.findById(matricula)).thenReturn(Optional.of(coche));
        when(cocheRepository.save(any(Coche.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Coche resultado = cocheService.eliminarDescuento(matricula);

        assertNotNull(resultado);
        assertEquals(0.0, resultado.getDescuento());
        verify(cocheRepository).findById(matricula);
        verify(cocheRepository).save(any(Coche.class));
    }

    @Test
    void testEliminarDescuento_CocheNoEncontrado() {
        String matricula = "NOEXISTE";

        when(cocheRepository.findById(matricula)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cocheService.eliminarDescuento(matricula);
        });

        assertEquals("Coche con matrícula " + matricula + " no encontrado", exception.getMessage());
        verify(cocheRepository).findById(matricula);
        verify(cocheRepository, never()).save(any());
    }
}