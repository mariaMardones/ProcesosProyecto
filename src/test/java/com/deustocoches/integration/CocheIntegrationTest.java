package com.deustocoches.integration;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.deustocoches.DeustocochesApplication;
import com.deustocoches.model.Coche;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = DeustocochesApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class CocheIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testCrudOperationsForCoche() {
        String matriculaUnica = "TEST" + System.currentTimeMillis();
        Coche coche = new Coche(matriculaUnica, "Seat", "Ibiza", 2021, "Azul", 15000.0, true);

        ResponseEntity<Coche> responseCreate = restTemplate.postForEntity("/api/coche/crear", coche, Coche.class);
        assertEquals(HttpStatus.OK, responseCreate.getStatusCode());
        Coche cocheCreado = responseCreate.getBody();
        assertNotNull(cocheCreado);
        assertEquals(matriculaUnica, cocheCreado.getMatricula());

        ResponseEntity<Coche> responseGet = restTemplate.getForEntity("/api/coche/buscar?matricula=" + matriculaUnica,
                Coche.class);
        assertEquals(HttpStatus.OK, responseGet.getStatusCode());
        Coche cocheRecuperado = responseGet.getBody();
        assertNotNull(cocheRecuperado);
        assertEquals("Seat", cocheRecuperado.getMarca());

        cocheRecuperado.setColor("Negro");
        cocheRecuperado.setPrecio(16000.0);

        restTemplate.put("/api/coche/actualizar?matricula=" + matriculaUnica, cocheRecuperado);

        ResponseEntity<Coche> responseAfterUpdate = restTemplate
                .getForEntity("/api/coche/buscar?matricula=" + matriculaUnica, Coche.class);
        assertEquals(HttpStatus.OK, responseAfterUpdate.getStatusCode());
        Coche cocheActualizado = responseAfterUpdate.getBody();
        assertNotNull(cocheActualizado);
        assertEquals("Negro", cocheActualizado.getColor());
        assertEquals(16000.0, cocheActualizado.getPrecio());

        ResponseEntity<List<Coche>> responseList = restTemplate.exchange(
                "/api/coche",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Coche>>() {
                });
        assertEquals(HttpStatus.OK, responseList.getStatusCode());
        List<Coche> coches = responseList.getBody();
        assertNotNull(coches);
        assertTrue(coches.stream().anyMatch(c -> c.getMatricula().equals(matriculaUnica)));

        restTemplate.delete("/api/coche/eliminar?matricula=" + matriculaUnica);

        ResponseEntity<Coche> responseAfterDelete = restTemplate
                .getForEntity("/api/coche/buscar?matricula=" + matriculaUnica, Coche.class);
        assertEquals(HttpStatus.NOT_FOUND, responseAfterDelete.getStatusCode());
    }

    @Test
    void testFiltrarCoches() {
        Coche coche1 = new Coche("TEST1", "Toyota", "Corolla", 2021, "Azul", 15000.0, true);
        Coche coche2 = new Coche("TEST2", "Honda", "Civic", 2022, "Rojo", 20000.0, true);

        restTemplate.postForEntity("/api/coche/crear", coche1, Coche.class);
        restTemplate.postForEntity("/api/coche/crear", coche2, Coche.class);

        ResponseEntity<List<Coche>> response = restTemplate.exchange(
                "/api/coche/filtrar?marca=Toyota&precioMax=16000",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Coche>>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Coche> cochesFiltrados = response.getBody();
        assertNotNull(cochesFiltrados);
        assertEquals(1, cochesFiltrados.size());
        assertEquals("Toyota", cochesFiltrados.get(0).getMarca());

        restTemplate.delete("/api/coche/eliminar?matricula=TEST1");
        restTemplate.delete("/api/coche/eliminar?matricula=TEST2");
    }
}