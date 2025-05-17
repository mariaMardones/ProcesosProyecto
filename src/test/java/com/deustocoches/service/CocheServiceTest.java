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
    void testGuardarCoche_yaRegistrado_lanzaExcepcion() {
        // Arrange
        CocheRepository cocheRepository = mock(CocheRepository.class);
        CocheService cocheService = new CocheService(cocheRepository);

        Coche coche = new Coche();
        coche.setMatricula("1234ABC");

        // Simula que ya existe un coche con esa matrícula
        when(cocheRepository.findByMatricula("1234ABC")).thenReturn(new Coche());

        // Act & Assert
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> cocheService.guardarCoche(coche)
        );
        assertEquals("El coche ya está registrado.", ex.getMessage());

        verify(cocheRepository, times(1)).findByMatricula("1234ABC");
        verify(cocheRepository, never()).save(any());
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
    void testActualizarCoche_noEncontrado_lanzaExcepcion() {
        // Arrange
        CocheRepository cocheRepository = mock(CocheRepository.class);
        CocheService cocheService = new CocheService(cocheRepository);

        String matricula = "NOEXISTE";
        Coche coche = new Coche();

        // Simula que no existe un coche con esa matrícula
        when(cocheRepository.findById(matricula)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> cocheService.actualizarCoche(matricula, coche)
        );
        assertEquals("Coche not encontrado", ex.getMessage());

        verify(cocheRepository, times(1)).findById(matricula);
        verify(cocheRepository, never()).save(any());
    }

    @Test
    void testEliminarCoche() {
        when(cocheRepository.existsById("1234ABC")).thenReturn(true);

        cocheService.eliminarCoche("1234ABC");

        verify(cocheRepository, times(1)).existsById("1234ABC");
        verify(cocheRepository, times(1)).deleteById("1234ABC");
    }
    
    @Test
    void testEliminarCoche_noEncontrado_lanzaExcepcion() {
        // Arrange
        CocheRepository cocheRepository = mock(CocheRepository.class);
        CocheService cocheService = new CocheService(cocheRepository);

        String matricula = "NOEXISTE";

        // Simula que no existe un coche con esa matrícula
        when(cocheRepository.existsById(matricula)).thenReturn(false);

        // Act & Assert
        RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> cocheService.eliminarCoche(matricula)
        );
        assertEquals("Coche con matricula: " + matricula + " no encontrado", ex.getMessage());

        verify(cocheRepository, times(1)).existsById(matricula);
        verify(cocheRepository, never()).deleteById(any());
    }

    @Test
    void testFiltrarCoches_cubreTodosLosCaminos() {
        Coche coche1 = new Coche();
        coche1.setMatricula("1111AAA");
        coche1.setMarca("Toyota");
        coche1.setModelo("Corolla");
        coche1.setPrecio(15000.0);

        Coche cocheTest = new Coche();
        cocheTest.setMatricula("TEST123");
        cocheTest.setMarca("Toyota");
        cocheTest.setModelo("Yaris");
        cocheTest.setPrecio(18000.0);

        Coche cocheNullMatricula = new Coche();
        cocheNullMatricula.setMatricula(null);
        cocheNullMatricula.setMarca("Renault");
        cocheNullMatricula.setModelo("Clio");
        cocheNullMatricula.setPrecio(9000.0);

        Coche cocheTestNoToyota = new Coche();
        cocheTestNoToyota.setMatricula("TEST999");
        cocheTestNoToyota.setMarca("Ford");
        cocheTestNoToyota.setModelo("Focus");
        cocheTestNoToyota.setPrecio(12000.0);

        List<Coche> coches = Arrays.asList(coche1, cocheTest, cocheNullMatricula, cocheTestNoToyota);
        when(cocheRepository.findAll()).thenReturn(coches);

        List<Coche> resultado1 = cocheService.filtrarCoches("Toyota", "algo", 10000.0, 20000.0);
        assertEquals(2, resultado1.size());
        assertTrue(resultado1.stream().anyMatch(c -> "TEST123".equals(c.getMatricula())));
        assertTrue(resultado1.stream().anyMatch(c -> "TEST999".equals(c.getMatricula())));

        List<Coche> resultado2 = cocheService.filtrarCoches("Ford", null, null, null);
        assertEquals(1, resultado2.size());
        assertEquals("TEST999", resultado2.get(0).getMatricula());

        when(cocheRepository.findAll()).thenReturn(Arrays.asList(coche1, cocheNullMatricula));
        List<Coche> resultado3 = cocheService.filtrarCoches("Toyota", null, null, null);
        assertEquals(1, resultado3.size());
        assertEquals("1111AAA", resultado3.get(0).getMatricula());

        when(cocheRepository.findAll()).thenReturn(coches);
        List<Coche> resultado4 = cocheService.filtrarCoches(null, null, null, null);
        assertEquals(4, resultado4.size());

        List<Coche> resultado5 = cocheService.filtrarCoches("", "", null, null);
        assertEquals(4, resultado5.size());

        List<Coche> resultado6 = cocheService.filtrarCoches(null, null, 10000.0, 16000.0);
        assertTrue(resultado6.stream().anyMatch(c -> "1111AAA".equals(c.getMatricula())));
        assertTrue(resultado6.stream().anyMatch(c -> "TEST999".equals(c.getMatricula())));
        assertEquals(2, resultado6.size());

        List<Coche> resultado7 = cocheService.filtrarCoches(null, "Corolla", null, null);
        assertEquals(1, resultado7.size());
        assertEquals("1111AAA", resultado7.get(0).getMatricula());
    }

    @Test
    void testFiltrarCoches_cochesTestNoVacioMarcaToyotaPrecioMaxNull() {
        Coche cocheTest = new Coche();
        cocheTest.setMatricula("TEST123");
        cocheTest.setMarca("Toyota");
        cocheTest.setModelo("Yaris");
        cocheTest.setPrecio(18000.0);

        List<Coche> coches = Arrays.asList(cocheTest);
        when(cocheRepository.findAll()).thenReturn(coches);

        List<Coche> resultado = cocheService.filtrarCoches("Toyota", null, null, null);
        assertEquals(1, resultado.size());
        assertEquals("TEST123", resultado.get(0).getMatricula());
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

    @Test
    void testListarMarcasCoches() {
        Coche coche1 = new Coche();
        coche1.setMarca("Toyota");
        coche1.setDisponible(true);

        Coche coche2 = new Coche();
        coche2.setMarca("Ford");
        coche2.setDisponible(true);

        Coche coche3 = new Coche();
        coche3.setMarca("Toyota");
        coche3.setDisponible(true);

        Coche coche4 = new Coche();
        coche4.setMarca("Renault");
        coche4.setDisponible(false);

        List<Coche> coches = Arrays.asList(coche1, coche2, coche3, coche4);
        when(cocheRepository.findAll()).thenReturn(coches);

        List<String> marcas = cocheService.ListarMarcasCoches();
        assertEquals(2, marcas.size());
        assertTrue(marcas.contains("Toyota"));
        assertTrue(marcas.contains("Ford"));
    }

}