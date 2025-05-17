package com.deustocoches.integration;

import com.deustocoches.model.Coche;
import com.deustocoches.model.Reserva;
import com.deustocoches.model.Usuario;
import com.deustocoches.model.EstadoReserva;
import com.deustocoches.repository.CocheRepository;
import com.deustocoches.repository.ReservaRepository;
import com.deustocoches.repository.UsuarioRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class DataInitializerIntegrationTest {

    @Autowired
    private UsuarioRepository usuarioRepo;
    @Autowired
    private CocheRepository cocheRepo;
    @Autowired
    private ReservaRepository reservaRepo;

    @Test
    void testInicializaDatosCuandoTablasVacias() {
        assertThat(usuarioRepo.count()).isGreaterThan(0);
        assertThat(cocheRepo.count()).isGreaterThan(0);
        assertThat(reservaRepo.count()).isGreaterThan(0);
    }

    @Test
    void testNoInicializaDatosSiSoloUsuariosNoEstaVacia() {
        usuarioRepo.save(new Usuario("Test", "User", "2000-01-01", "test@correo.com", "pass", "600000000"));
        long usuariosAntes = usuarioRepo.count();
        long cochesAntes = cocheRepo.count();
        long reservasAntes = reservaRepo.count();
        assertThat(usuarioRepo.count()).isEqualTo(usuariosAntes);
        assertThat(cocheRepo.count()).isEqualTo(cochesAntes);
        assertThat(reservaRepo.count()).isEqualTo(reservasAntes);
    }

    @Test
    void testNoInicializaDatosSiSoloCochesNoEstaVacia() {
        cocheRepo.save(new Coche("9999ZZZ", "TestMarca", "TestModelo", 2023, "Rojo", 10000.0, true));
        long usuariosAntes = usuarioRepo.count();
        long cochesAntes = cocheRepo.count();
        long reservasAntes = reservaRepo.count();
        assertThat(usuarioRepo.count()).isEqualTo(usuariosAntes);
        assertThat(cocheRepo.count()).isEqualTo(cochesAntes);
        assertThat(reservaRepo.count()).isEqualTo(reservasAntes);
    }

    @Test
    void testNoInicializaDatosSiSoloReservasNoEstaVacia() {
        Usuario usuario = usuarioRepo.save(new Usuario("Test", "User", "2000-01-01", "test@correo.com", "pass", "600000000"));
        Coche coche = cocheRepo.save(new Coche("8888YYY", "TestMarca", "TestModelo", 2023, "Azul", 9000.0, true));
        reservaRepo.save(new Reserva(usuario, coche, "2025-01-01", 9000.0, EstadoReserva.PENDIENTE));
        long usuariosAntes = usuarioRepo.count();
        long cochesAntes = cocheRepo.count();
        long reservasAntes = reservaRepo.count();
        assertThat(usuarioRepo.count()).isEqualTo(usuariosAntes);
        assertThat(cocheRepo.count()).isEqualTo(cochesAntes);
        assertThat(reservaRepo.count()).isEqualTo(reservasAntes);
    }

    @Test
    void testNoInicializaDatosSiAlgunaTablaNoEstaVacia() {
        usuarioRepo.save(new Usuario("Test", "User", "2000-01-01", "test@correo.com", "pass", "600000000"));
        cocheRepo.save(new Coche("7777XXX", "TestMarca", "TestModelo", 2022, "Negro", 8000.0, true));
        long usuariosAntes = usuarioRepo.count();
        long cochesAntes = cocheRepo.count();
        long reservasAntes = reservaRepo.count();
        assertThat(usuarioRepo.count()).isEqualTo(usuariosAntes);
        assertThat(cocheRepo.count()).isEqualTo(cochesAntes);
        assertThat(reservaRepo.count()).isEqualTo(reservasAntes);
    }

    @Test
    void testNoInicializaDatosSiUsuariosYCochesNoEstanVacias() {
        usuarioRepo.save(new Usuario("Test", "User", "2000-01-01", "test@correo.com", "pass", "600000000"));
        cocheRepo.save(new Coche("1111AAA", "TestMarca", "TestModelo", 2022, "Negro", 8000.0, true));
        long usuariosAntes = usuarioRepo.count();
        long cochesAntes = cocheRepo.count();
        long reservasAntes = reservaRepo.count();
        assertThat(usuarioRepo.count()).isEqualTo(usuariosAntes);
        assertThat(cocheRepo.count()).isEqualTo(cochesAntes);
        assertThat(reservaRepo.count()).isEqualTo(reservasAntes);
    }

    @Test
    void testNoInicializaDatosSiUsuariosYReservasNoEstanVacias() {
        Usuario usuario = usuarioRepo.save(new Usuario("Test", "User", "2000-01-01", "test@correo.com", "pass", "600000000"));
        Coche coche = cocheRepo.save(new Coche("2222BBB", "TestMarca", "TestModelo", 2022, "Negro", 8000.0, true));
        reservaRepo.save(new Reserva(usuario, coche, "2025-01-01", 9000.0, EstadoReserva.PENDIENTE));
        long usuariosAntes = usuarioRepo.count();
        long cochesAntes = cocheRepo.count();
        long reservasAntes = reservaRepo.count();
        assertThat(usuarioRepo.count()).isEqualTo(usuariosAntes);
        assertThat(cocheRepo.count()).isEqualTo(cochesAntes);
        assertThat(reservaRepo.count()).isEqualTo(reservasAntes);
    }

    @Test
    void testNoInicializaDatosSiCochesYReservasNoEstanVacias() {
        Usuario usuario = usuarioRepo.save(new Usuario("Test", "User", "2000-01-01", "test@correo.com", "pass", "600000000"));
        Coche coche = cocheRepo.save(new Coche("3333CCC", "TestMarca", "TestModelo", 2022, "Negro", 8000.0, true));
        reservaRepo.save(new Reserva(usuario, coche, "2025-01-01", 9000.0, EstadoReserva.PENDIENTE));
        long usuariosAntes = usuarioRepo.count();
        long cochesAntes = cocheRepo.count();
        long reservasAntes = reservaRepo.count();
        assertThat(usuarioRepo.count()).isEqualTo(usuariosAntes);
        assertThat(cocheRepo.count()).isEqualTo(cochesAntes);
        assertThat(reservaRepo.count()).isEqualTo(reservasAntes);
    }
}