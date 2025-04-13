package com.example.restapi.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.deustocoches.model.Coche;
import com.deustocoches.model.EstadoReserva;
import com.deustocoches.model.Reserva;
import com.deustocoches.model.TipoRol;
import com.deustocoches.model.Usuario;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testCrearUsuarioCocheYReserva() {
        Usuario usuario = new Usuario("Maria", "Lopez", "01/01/2001", "maria@email.com", "password", "hola",
                TipoRol.CLIENTE);
        ResponseEntity<Usuario> userResponse = restTemplate.postForEntity("/api/usuario/registrar", usuario,
                Usuario.class);
        assertEquals(HttpStatus.CREATED, userResponse.getStatusCode());
        Usuario usuarioCreado = userResponse.getBody();
        assertNotNull(usuarioCreado);
        Long usuarioId = usuarioCreado.getId();

        // 2. Crear un coche
        Coche coche = new Coche("6482XMN","Toyota", "Corolla", 2020, "Rojo", 10000.0, true);
        ResponseEntity<Coche> cocheResponse = restTemplate.postForEntity("/api/coche/crear", coche, Coche.class);
        assertEquals(HttpStatus.CREATED, cocheResponse.getStatusCode());
        Coche cocheCreado = cocheResponse.getBody();
        assertNotNull(cocheCreado);
        String cocheId = cocheCreado.getMatricula();

        // 3. Crear una reserva
        Reserva reserva = new Reserva(usuarioCreado, cocheCreado, LocalDate.now().toString(), 100.0, EstadoReserva.PENDIENTE);
        ResponseEntity<Reserva> reservaResponse = restTemplate.postForEntity("/api/reservas/crear", reserva,
                Reserva.class);
        assertEquals(HttpStatus.CREATED, reservaResponse.getStatusCode());
        Reserva reservaCreada = reservaResponse.getBody();
        assertNotNull(reservaCreada);
        int reservaId = reservaCreada.getId();

        // 4. Obtener todas las reservas y verificar que existe
        ResponseEntity<Reserva[]> reservasGetResponse = restTemplate.getForEntity("/api/reservas", Reserva[].class);
        assertEquals(HttpStatus.OK, reservasGetResponse.getStatusCode());
        Reserva[] reservas = reservasGetResponse.getBody();
        assertNotNull(reservas);

        boolean reservaEncontrada = Arrays.stream(reservas)
                .anyMatch(r -> r.getId() == reservaId); 
        assertTrue(reservaEncontrada);

        // 5. Cancelar la reserva
        restTemplate.delete("/api/reservas/" + reservaId);

        // 6. Eliminar el coche
        restTemplate.delete("/api/coche/" + cocheId);

        // 7. Eliminar el usuario
        restTemplate.delete("/api/usuario/" + usuarioId);


        ResponseEntity<Reserva[]> reservasGetResponsePostDelete = restTemplate.getForEntity("/api/reservas", Reserva[].class);
        assertEquals(HttpStatus.OK, reservasGetResponsePostDelete.getStatusCode());
        Reserva[] reservasPostDelete = reservasGetResponsePostDelete.getBody();
        assertNotNull(reservasPostDelete);

        boolean reservaEliminada = Arrays.stream(reservasPostDelete)
                .noneMatch(r -> r.getId() == reservaId);
        assertTrue(reservaEliminada);
    }
}
