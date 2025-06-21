package com.deustocoches.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ReservaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
//Al menos un usuario registrado con ese correo (test@correo.com) y alguna reserva confirmada.
    @Test
    public void testExportarReservasPdf_RespuestaOk() throws Exception {
        mockMvc.perform(get("/api/reservas/exportar")
                .param("email", "test@correo.com")
                .param("formato", "pdf"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=historial.pdf"))
                .andExpect(content().contentType("application/pdf"));
    }

    @Test
    public void testExportarReservasCsv_RespuestaOk() throws Exception {
        mockMvc.perform(get("/api/reservas/exportar")
                .param("email", "test@correo.com")
                .param("formato", "csv"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=historial.csv"))
                .andExpect(content().contentType("text/csv"));
    }

    @Test
    public void testExportarReservasFormatoInvalido() throws Exception {
        mockMvc.perform(get("/api/reservas/exportar")
                .param("email", "test@correo.com")
                .param("formato", "xml"))
                .andExpect(status().isBadRequest());
    }
}
