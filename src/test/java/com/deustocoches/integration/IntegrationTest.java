package com.deustocoches.integration;

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
import org.springframework.test.context.ActiveProfiles;

import com.deustocoches.DeustocochesApplication;
import com.deustocoches.model.Coche;
import com.deustocoches.model.EstadoReserva;
import com.deustocoches.model.Reserva;
import com.deustocoches.model.TipoRol;
import com.deustocoches.model.Usuario;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = DeustocochesApplication.class)
@ActiveProfiles("test")
public class IntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testCrearUsuarioCocheYReserva() {
        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setNombre("Maria");
        usuario.setApellido("Lopez");
        usuario.setFechaNacimiento("01/01/2001");
        usuario.setEmail("maria@email.com");
        usuario.setPassword("password");
        usuario.setTlf("123456789");
        usuario.setRol(TipoRol.CLIENTE);

        ResponseEntity<Usuario> userResponse = restTemplate.postForEntity("/api/usuario/registrar", usuario, Usuario.class);
        assertEquals(HttpStatus.OK, userResponse.getStatusCode());

        Usuario usuarioCreado = userResponse.getBody();
        assertNotNull(usuarioCreado);

        // Crear coche
        String matriculaUnica = "TEST" + System.currentTimeMillis();
        Coche coche = new Coche(matriculaUnica, "Toyota", "Corolla", 2020, "Rojo", 10000.0, true);

        ResponseEntity<Coche> cocheResponse = restTemplate.postForEntity("/api/coche/crear", coche, Coche.class);
        assertEquals(HttpStatus.OK, cocheResponse.getStatusCode());

        Coche cocheCreado = cocheResponse.getBody();
        assertNotNull(cocheCreado);
        String cocheId = cocheCreado.getMatricula();

        // Crear reserva con descuento
        Reserva reserva = new Reserva(usuarioCreado, cocheCreado, LocalDate.now().toString(), 100.0, EstadoReserva.PENDIENTE, 10.0);
        ResponseEntity<Reserva> reservaResponse = restTemplate.postForEntity("/api/reservas/crear", reserva, Reserva.class);
        assertEquals(HttpStatus.OK, reservaResponse.getStatusCode());

        Reserva reservaCreada = reservaResponse.getBody();
        assertNotNull(reservaCreada);
        int reservaId = reservaCreada.getId();

        // Verificar reservas pendientes
        ResponseEntity<Reserva[]> reservasGetResponse = restTemplate.getForEntity("/api/reservas/pendientes", Reserva[].class);
        assertEquals(HttpStatus.OK, reservasGetResponse.getStatusCode());
        Reserva[] reservas = reservasGetResponse.getBody();
        assertNotNull(reservas);

        boolean reservaEncontrada = Arrays.stream(reservas)
                .anyMatch(r -> r.getId() == reservaId);
        assertTrue(reservaEncontrada);

        // Eliminar reserva
        restTemplate.delete("/api/reservas/eliminar/" + reservaId);

        // Eliminar coche
        restTemplate.delete("/api/coche/eliminar?matricula=" + cocheId);

        // Eliminar usuario
        restTemplate.delete("/api/usuario/eliminar?email=" + usuario.getEmail());

        // Verificar que la reserva fue eliminada
        ResponseEntity<Reserva[]> reservasGetResponsePostDelete = restTemplate.getForEntity("/api/reservas/pendientes", Reserva[].class);
        assertEquals(HttpStatus.OK, reservasGetResponsePostDelete.getStatusCode());
        Reserva[] reservasPostDelete = reservasGetResponsePostDelete.getBody();
        assertNotNull(reservasPostDelete);

        boolean reservaEliminada = Arrays.stream(reservasPostDelete)
                .noneMatch(r -> r.getId() == reservaId);
        assertTrue(reservaEliminada);
    }
}
