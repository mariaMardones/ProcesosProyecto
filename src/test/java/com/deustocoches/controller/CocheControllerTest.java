package com.deustocoches.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.deustocoches.model.Coche;
import com.deustocoches.service.CocheService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("deprecation") // Suprimir advertencia de MockBean
@WebMvcTest(CocheController.class)
public class CocheControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CocheService cocheService;

    @Autowired
    private ObjectMapper objectMapper;

    private Coche coche;

    @BeforeEach
    void setUp() {
        coche = new Coche();
        coche.setMatricula("1234ABC");
        coche.setMarca("Toyota");
        coche.setModelo("Corolla");
        coche.setAnio(2020);
        coche.setColor("Azul");
        coche.setPrecio(20000.0);
        coche.setDisponible(true);
    }

    @Test
    void testListarCoches() throws Exception {
        Coche coche2 = new Coche();
        coche2.setMatricula("5678DEF");
        coche2.setMarca("Honda");
        coche2.setModelo("Civic");

        when(cocheService.ListarCoches())
                .thenReturn(Arrays.asList(coche, coche2));

        mockMvc.perform(get("/api/coche"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].matricula").value("1234ABC"))
                .andExpect(jsonPath("$[0].marca").value("Toyota"))
                .andExpect(jsonPath("$[1].matricula").value("5678DEF"))
                .andExpect(jsonPath("$[1].marca").value("Honda"));

        verify(cocheService, times(1)).ListarCoches();
    }

    @Test
    void testObtenerCochePorMatricula() throws Exception {
        when(cocheService.getCocheByMatricula("1234ABC"))
                .thenReturn(Optional.of(coche));

        mockMvc.perform(get("/api/coche/buscar")
                .param("matricula", "1234ABC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matricula").value("1234ABC"))
                .andExpect(jsonPath("$.marca").value("Toyota"))
                .andExpect(jsonPath("$.modelo").value("Corolla"));

        verify(cocheService, times(1)).getCocheByMatricula("1234ABC");
    }

    @Test
    void testObtenerCocheNoExistente() throws Exception {
        when(cocheService.getCocheByMatricula("NOEXISTE"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/coche/buscar")
                .param("matricula", "NOEXISTE"))
                .andExpect(status().isNotFound());

        verify(cocheService, times(1)).getCocheByMatricula("NOEXISTE");
    }

    @Test
    void testCrearCoche() throws Exception {
        when(cocheService.guardarCoche(any(Coche.class)))
                .thenReturn(coche);

        mockMvc.perform(post("/api/coche/crear")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(coche)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matricula").value("1234ABC"))
                .andExpect(jsonPath("$.marca").value("Toyota"));

        verify(cocheService, times(1)).guardarCoche(any(Coche.class));
    }

    @Test
    void testCrearCocheConError() throws Exception {
        when(cocheService.guardarCoche(any(Coche.class)))
                .thenThrow(new RuntimeException("Matrícula ya existe"));

        mockMvc.perform(post("/api/coche/crear")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(coche)))
                .andExpect(status().isBadRequest());

        verify(cocheService, times(1)).guardarCoche(any(Coche.class));
    }

    @Test
    void testActualizarCoche() throws Exception {
        Coche cocheActualizado = new Coche();
        cocheActualizado.setMatricula("1234ABC");
        cocheActualizado.setMarca("Toyota");
        cocheActualizado.setModelo("Corolla");
        cocheActualizado.setColor("Rojo");
        cocheActualizado.setPrecio(22000.0);

        when(cocheService.actualizarCoche(eq("1234ABC"), any(Coche.class)))
                .thenReturn(cocheActualizado);

        mockMvc.perform(put("/api/coche/actualizar")
                .param("matricula", "1234ABC")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cocheActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matricula").value("1234ABC"))
                .andExpect(jsonPath("$.color").value("Rojo"))
                .andExpect(jsonPath("$.precio").value(22000.0));

        verify(cocheService, times(1)).actualizarCoche(eq("1234ABC"), any(Coche.class));
    }

    @Test
    void testActualizarCocheConError() throws Exception {
        when(cocheService.actualizarCoche(eq("1234ABC"), any(Coche.class)))
                .thenThrow(new RuntimeException("Error al actualizar"));

        mockMvc.perform(put("/api/coche/actualizar")
                .param("matricula", "1234ABC")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(coche)))
                .andExpect(status().isBadRequest());

        verify(cocheService, times(1)).actualizarCoche(eq("1234ABC"), any(Coche.class));
    }

    @Test
    void testEliminarCoche() throws Exception {
        when(cocheService.getCocheByMatricula("1234ABC"))
                .thenReturn(Optional.of(coche));

        mockMvc.perform(delete("/api/coche/eliminar")
                .param("matricula", "1234ABC"))
                .andExpect(status().isNoContent());

        verify(cocheService, times(1)).getCocheByMatricula("1234ABC");
        verify(cocheService, times(1)).eliminarCoche("1234ABC");
    }

    @Test
    void testEliminarCocheNoExistente() throws Exception {
        when(cocheService.getCocheByMatricula("NOEXISTE"))
                .thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/coche/eliminar")
                .param("matricula", "NOEXISTE"))
                .andExpect(status().isNotFound());

        verify(cocheService, times(1)).getCocheByMatricula("NOEXISTE");
        verify(cocheService, times(0)).eliminarCoche("NOEXISTE");
    }

    @Test
    void testListarCochesDisponibles() throws Exception {
        when(cocheService.ListarCochesDisponibles())
                .thenReturn(Arrays.asList(coche));

        mockMvc.perform(get("/api/coche/disponibles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].matricula").value("1234ABC"))
                .andExpect(jsonPath("$[0].disponible").value(true));

        verify(cocheService, times(1)).ListarCochesDisponibles();
    }
    
    @Test
    void testAplicarDescuento() throws Exception {
        String matricula = "1234ABC";
        double descuento = 10.0;

        Coche cocheConDescuento = new Coche();
        cocheConDescuento.setMatricula(matricula);
        cocheConDescuento.setDescuento(descuento);

        when(cocheService.aplicarDescuento(matricula, descuento)).thenReturn(cocheConDescuento);

        mockMvc.perform(put("/api/coche/aplicarDescuento")
                .param("matricula", matricula)
                .param("descuento", String.valueOf(descuento))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matricula").value(matricula))
                .andExpect(jsonPath("$.descuento").value(descuento));
    }

    @Test
    void testEliminarDescuento() throws Exception {
        String matricula = "1234ABC";

        Coche cocheSinDescuento = new Coche();
        cocheSinDescuento.setMatricula(matricula);
        cocheSinDescuento.setDescuento(0.0);

        when(cocheService.eliminarDescuento(matricula)).thenReturn(cocheSinDescuento);

        mockMvc.perform(put("/api/coche/eliminarDescuento")
                .param("matricula", matricula)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matricula").value(matricula))
                .andExpect(jsonPath("$.descuento").value(0.0));
    }

    @Test
    void testAplicarDescuentoCocheNoExiste() throws Exception {
        String matricula = "NOEXISTE";
        double descuento = 5.0;

        when(cocheService.aplicarDescuento(matricula, descuento)).thenThrow(new RuntimeException("Coche no encontrado"));

        mockMvc.perform(put("/api/coche/aplicarDescuento")
                .param("matricula", matricula)
                .param("descuento", String.valueOf(descuento))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    @Test
    void testFiltrarCoches_Exception() throws Exception {
        when(cocheService.filtrarCoches(any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Error inesperado"));

        mockMvc.perform(get("/api/coche/filtrar")
                .param("marca", "Toyota")
                .param("modelo", "Corolla")
                .param("precioMin", "10000")
                .param("precioMax", "30000"))
                .andExpect(status().isBadRequest());

        verify(cocheService, times(1)).filtrarCoches("Toyota", "Corolla", 10000.0, 30000.0);
    }

    @Test
    void testListarMarcasCoches() throws Exception {
        when(cocheService.ListarMarcasCoches()).thenReturn(Arrays.asList("Toyota", "Honda"));

        mockMvc.perform(get("/api/coche/marcas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Toyota"))
                .andExpect(jsonPath("$[1]").value("Honda"));

        verify(cocheService, times(1)).ListarMarcasCoches();
    }

    @Test
    void testEliminarDescuento_Exception() throws Exception {
        String matricula = "NOEXISTE";
        when(cocheService.eliminarDescuento(matricula)).thenThrow(new RuntimeException("Error al eliminar descuento"));

        mockMvc.perform(put("/api/coche/eliminarDescuento")
                .param("matricula", matricula)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(cocheService, times(1)).eliminarDescuento(matricula);
    }
}