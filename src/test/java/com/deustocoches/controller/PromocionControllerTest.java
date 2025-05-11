package com.deustocoches.controller;

import com.deustocoches.model.Promocion;
import com.deustocoches.service.PromocionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PromocionController.class)
public class PromocionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PromocionService promocionService;

    @Autowired
    private ObjectMapper objectMapper;

    private Promocion promocion;

    @BeforeEach
    void setUp() {
        promocion = new Promocion();
        promocion.setId(1L);
        promocion.setDescripcion("Promoción de prueba");
        promocion.setDescuento(10.0);
    }

    @Test
    void testCrearPromocion() throws Exception {
        when(promocionService.crearPromocion(any(Promocion.class))).thenReturn(promocion);

        mockMvc.perform(post("/promociones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(promocion)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.descripcion").value("Promoción de prueba"))
                .andExpect(jsonPath("$.descuento").value(10.0));

        verify(promocionService, times(1)).crearPromocion(any(Promocion.class));
    }

    @Test
    void testListarPromociones() throws Exception {
        List<Promocion> promociones = Arrays.asList(promocion);
        when(promocionService.obtenerTodas()).thenReturn(promociones);

        mockMvc.perform(get("/promociones")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].descripcion").value("Promoción de prueba"))
                .andExpect(jsonPath("$[0].descuento").value(10.0));

        verify(promocionService, times(1)).obtenerTodas();
    }

    @Test
    void testEliminarPromocion() throws Exception {
        doNothing().when(promocionService).eliminarPromocion(1L);

        mockMvc.perform(delete("/promociones/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(promocionService, times(1)).eliminarPromocion(1L);
    }
}