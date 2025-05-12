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
import com.deustocoches.model.EstadoReserva;
import com.deustocoches.model.Reserva;
import com.deustocoches.model.TipoRol;
import com.deustocoches.model.Usuario;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = DeustocochesApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class ReservaIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testFlujoCompletaDeReserva() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Ana");
        usuario.setApellido("Martínez");
        usuario.setFechaNacimiento("15/05/1985");
        usuario.setEmail("ana.martinez@example.com");
        usuario.setPassword("password123");
        usuario.setTlf("611222333");
        usuario.setRol(TipoRol.CLIENTE);
        
        ResponseEntity<Usuario> userResponse = restTemplate.postForEntity("/api/usuario/registrar", usuario, Usuario.class);
        assertEquals(HttpStatus.OK, userResponse.getStatusCode());
        Usuario usuarioCreado = userResponse.getBody();
        assertNotNull(usuarioCreado);
        
        String matriculaUnica = "TEST" + System.currentTimeMillis();
        Coche coche = new Coche(matriculaUnica, "Ford", "Focus", 2019, "Blanco", 12000.0, true);
        
        ResponseEntity<Coche> cocheResponse = restTemplate.postForEntity("/api/coche/crear", coche, Coche.class);
        assertEquals(HttpStatus.OK, cocheResponse.getStatusCode());
        Coche cocheCreado = cocheResponse.getBody();
        assertNotNull(cocheCreado);
        
        Reserva reserva = new Reserva(usuarioCreado, cocheCreado, LocalDate.now().toString(), 200.0, EstadoReserva.PENDIENTE);
        
        ResponseEntity<Reserva> reservaResponse = restTemplate.postForEntity("/api/reservas/crear", reserva, Reserva.class);
        assertEquals(HttpStatus.OK, reservaResponse.getStatusCode());
        Reserva reservaCreada = reservaResponse.getBody();
        assertNotNull(reservaCreada);
        int reservaId = reservaCreada.getId();
        
        ResponseEntity<List<Reserva>> pendientesResponse = restTemplate.exchange(
                "/api/reservas/pendientes", 
                HttpMethod.GET, 
                null, 
                new ParameterizedTypeReference<List<Reserva>>() {}
        );
        assertEquals(HttpStatus.OK, pendientesResponse.getStatusCode());
        List<Reserva> reservasPendientes = pendientesResponse.getBody();
        assertNotNull(reservasPendientes);
        assertTrue(reservasPendientes.stream().anyMatch(r -> r.getId() == reservaId));
        
        Reserva reservaActualizar = reservaCreada;
        reservaActualizar.setEstado(EstadoReserva.COMPRADA);
        
        restTemplate.put("/api/reservas/actualizar/" + reservaId, reservaActualizar);
        
        ResponseEntity<List<Reserva>> compradasResponse = restTemplate.exchange(
                "/api/reservas/compradas", 
                HttpMethod.GET, 
                null, 
                new ParameterizedTypeReference<List<Reserva>>() {}
        );
        assertEquals(HttpStatus.OK, compradasResponse.getStatusCode());
        List<Reserva> reservasCompradas = compradasResponse.getBody();
        assertNotNull(reservasCompradas);
        assertTrue(reservasCompradas.stream().anyMatch(r -> r.getId() == reservaId));
        
        ResponseEntity<List<Reserva>> pendientesDespuesResponse = restTemplate.exchange(
                "/api/reservas/pendientes", 
                HttpMethod.GET, 
                null, 
                new ParameterizedTypeReference<List<Reserva>>() {}
        );
        List<Reserva> reservasPendientesDespues = pendientesDespuesResponse.getBody();
        assertNotNull(reservasPendientesDespues);
        assertFalse(reservasPendientesDespues.stream().anyMatch(r -> r.getId() == reservaId));
        
        restTemplate.delete("/api/reservas/eliminar/" + reservaId);
        restTemplate.delete("/api/coche/eliminar?matricula=" + matriculaUnica);
        restTemplate.delete("/api/usuario/eliminar?email=" + usuario.getEmail());
    }
}