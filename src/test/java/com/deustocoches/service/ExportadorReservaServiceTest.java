package com.deustocoches.service;

import com.deustocoches.model.*;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ExportadorReservaServiceTest {

    ExportadorReservaService exportador = new ExportadorReservaService();

    private Reserva crearReservaDummy() {
        Usuario usuario = new Usuario();
        usuario.setNombre("María");

        Coche coche = new Coche();
        coche.setMarca("Toyota");
        coche.setModelo("Corolla");

        return new Reserva(usuario, coche, "2024-06-20", 18000.0, EstadoReserva.COMPRADA);
    }

    @Test
    public void testExportarReservasAPdf() {
        Reserva reserva = crearReservaDummy();
        ByteArrayInputStream resultado = exportador.exportarReservasAPdf(List.of(reserva));

        assertNotNull(resultado);
        assertTrue(resultado.available() > 0);
    }

    @Test
    public void testExportarReservasACsv() {
        Reserva reserva = crearReservaDummy();
        ByteArrayInputStream resultado = exportador.exportarReservasACsv(List.of(reserva));

        assertNotNull(resultado);
        assertTrue(resultado.available() > 0);
    }

    @Test
    public void testExportarReservasVacias() {
        ByteArrayInputStream pdf = exportador.exportarReservasAPdf(Collections.emptyList());
        ByteArrayInputStream csv = exportador.exportarReservasACsv(Collections.emptyList());

        assertNotNull(pdf);
        assertNotNull(csv);
    }
}
