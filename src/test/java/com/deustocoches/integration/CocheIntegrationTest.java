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

import com.deustocoches.DeustocochesApplication;
import com.deustocoches.model.Coche;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = DeustocochesApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CocheIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testCrudOperationsForCoche() {
        // 1. Crear un coche con matrícula única
        String matriculaUnica = "TEST" + System.currentTimeMillis();
        Coche coche = new Coche(matriculaUnica, "Seat", "Ibiza", 2021, "Azul", 15000.0, true);
        
        ResponseEntity<Coche> responseCreate = restTemplate.postForEntity("/api/coche/crear", coche, Coche.class);
        assertEquals(HttpStatus.OK, responseCreate.getStatusCode());
        Coche cocheCreado = responseCreate.getBody();
        assertNotNull(cocheCreado);
        assertEquals(matriculaUnica, cocheCreado.getMatricula());
        
        // 2. Obtener el coche por matrícula - ENDPOINT CORREGIDO
        ResponseEntity<Coche> responseGet = restTemplate.getForEntity("/api/coche/buscar?matricula=" + matriculaUnica, Coche.class);
        assertEquals(HttpStatus.OK, responseGet.getStatusCode());
        Coche cocheRecuperado = responseGet.getBody();
        assertNotNull(cocheRecuperado);
        assertEquals("Seat", cocheRecuperado.getMarca());
        
        // 3. Actualizar el coche - ENDPOINT CORREGIDO
        cocheRecuperado.setColor("Negro");
        cocheRecuperado.setPrecio(16000.0);
        
        restTemplate.put("/api/coche/actualizar?matricula=" + matriculaUnica, cocheRecuperado);
        
        // 4. Verificar la actualización - ENDPOINT CORREGIDO
        ResponseEntity<Coche> responseAfterUpdate = restTemplate.getForEntity("/api/coche/buscar?matricula=" + matriculaUnica, Coche.class);
        assertEquals(HttpStatus.OK, responseAfterUpdate.getStatusCode());
        Coche cocheActualizado = responseAfterUpdate.getBody();
        assertNotNull(cocheActualizado);
        assertEquals("Negro", cocheActualizado.getColor());
        assertEquals(16000.0, cocheActualizado.getPrecio());
        
        // 5. Verificar que aparece en la lista de coches
        ResponseEntity<List<Coche>> responseList = restTemplate.exchange(
                "/api/coche", 
                HttpMethod.GET, 
                null, 
                new ParameterizedTypeReference<List<Coche>>() {}
        );
        assertEquals(HttpStatus.OK, responseList.getStatusCode());
        List<Coche> coches = responseList.getBody();
        assertNotNull(coches);
        assertTrue(coches.stream().anyMatch(c -> c.getMatricula().equals(matriculaUnica)));
        
        // 6. Eliminar el coche
        restTemplate.delete("/api/coche/eliminar?matricula=" + matriculaUnica);
        
        // 7. Verificar que se ha eliminado
        ResponseEntity<Coche> responseAfterDelete = restTemplate.getForEntity("/api/coche/buscar?matricula=" + matriculaUnica, Coche.class);
        assertEquals(HttpStatus.NOT_FOUND, responseAfterDelete.getStatusCode());
    }
}