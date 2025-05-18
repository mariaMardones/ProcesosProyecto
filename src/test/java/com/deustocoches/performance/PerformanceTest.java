package com.deustocoches.performance;

import com.deustocoches.model.Coche;
import com.deustocoches.model.Reserva;
import com.deustocoches.model.Usuario;
import com.deustocoches.model.EstadoReserva;
import com.deustocoches.service.CocheService;
import com.deustocoches.service.ReservaService;
import com.deustocoches.service.UsuarioService;
import com.deustocoches.repository.CocheRepository;
import com.deustocoches.repository.ReservaRepository;
import com.deustocoches.repository.UsuarioRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PerformanceTest {

    @Autowired
    private CocheService cocheService;
    @Autowired
    private ReservaService reservaService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private CocheRepository cocheRepository;
    @Autowired
    private ReservaRepository reservaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    public void cleanDb() {
        reservaRepository.deleteAll();
        cocheRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    public void testGuardarCoche() {
        Coche coche = new Coche("TEST001", "Toyota", "Corolla", 2022, "Blanco", 15000.0, true);
        Coche saved = cocheService.guardarCoche(coche);
        assertNotNull(saved);
        assertEquals("TEST001", saved.getMatricula());
    }

    @Test
    public void testRegistrarUsuario() {
        Usuario usuario = new Usuario("Juan", "Pérez", "1990-05-15", "juanperez@opendeusto.es", "password1", "600000020");
        Usuario saved = usuarioService.registrarUsuario(usuario);
        assertNotNull(saved);
        assertEquals("juanperez@opendeusto.es", saved.getEmail());
    }

    @Test
    public void testCrearReserva() {
        Usuario usuario = usuarioService.registrarUsuario(new Usuario("Ana", "López", "1992-03-10", "ana@opendeusto.es", "pass", "600000021"));
        Coche coche = cocheService.guardarCoche(new Coche("TEST002", "Honda", "Civic", 2021, "Negro", 13000.0, true));
        Reserva reserva = new Reserva(usuario, coche, "2024-05-18", 13000.0, EstadoReserva.PENDIENTE);
        Reserva saved = reservaService.crearReserva(reserva);
        assertNotNull(saved);
        assertEquals(EstadoReserva.PENDIENTE, saved.getEstado());
    }

    @Test
    public void testListarCochesDisponibles() {
        cocheService.guardarCoche(new Coche("TEST003", "Seat", "Ibiza", 2022, "Azul", 11000.0, true));
        cocheService.guardarCoche(new Coche("TEST004", "Renault", "Clio", 2020, "Gris", 9000.0, false));
        List<Coche> disponibles = cocheService.ListarCochesDisponibles();
        assertEquals(1, disponibles.size());
        assertEquals("TEST003", disponibles.get(0).getMatricula());
    }

    @Test
    public void testObtenerReservasPendientes() {
        Usuario usuario = usuarioService.registrarUsuario(new Usuario("Luis", "Martín", "1995-07-20", "luis@opendeusto.es", "pass", "600000022"));
        Coche coche = cocheService.guardarCoche(new Coche("TEST005", "Ford", "Focus", 2020, "Rojo", 12000.0, true));
        Reserva reserva = new Reserva(usuario, coche, "2024-05-18", 12000.0, EstadoReserva.PENDIENTE);
        reservaService.crearReserva(reserva);
        List<Reserva> pendientes = reservaService.obtenerPendientes();
        assertFalse(pendientes.isEmpty());
        assertEquals(EstadoReserva.PENDIENTE, pendientes.get(0).getEstado());
    }

    @Test
    public void testLogInUsuario() {
        Usuario usuario = usuarioService.registrarUsuario(new Usuario("Sara", "Gómez", "1993-11-30", "sara@opendeusto.es", "password2", "600000023"));
        Optional<String> token = usuarioService.logIn("sara@opendeusto.es", "password2");
        assertTrue(token.isPresent());
    }
}