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

import com.deustocoches.DeustocochesApplication;
import com.deustocoches.model.Coche;
import com.deustocoches.model.EstadoReserva;
import com.deustocoches.model.Reserva;
import com.deustocoches.model.TipoRol;
import com.deustocoches.model.Usuario;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = DeustocochesApplication.class)
public class IntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testCrearUsuarioCocheYReserva() {
        // 1. Crear un usuario con la fecha en formato dd/MM/yyyy
        Usuario usuario = new Usuario();
        usuario.setNombre("Maria");
        usuario.setApellido("Lopez");
        usuario.setFechaNacimiento("01/01/2001"); // Formato dd/MM/yyyy
        usuario.setEmail("maria@email.com");
        usuario.setPassword("password");
        usuario.setTlf("123456789");
        usuario.setRol(TipoRol.CLIENTE);

        // Imprimir el objeto para depuración
        System.out.println("Enviando usuario: " + usuario.getNombre() + ", " + usuario.getEmail());
        
        // Declara la variable fuera del bloque try
        ResponseEntity<Usuario> userResponse = null;
        try {
            userResponse = restTemplate.postForEntity("/api/usuario/registrar", usuario, Usuario.class);
            System.out.println("Respuesta: " + userResponse.getStatusCode() + " - Body: " + userResponse.getBody());
            assertEquals(HttpStatus.OK, userResponse.getStatusCode());
        } catch (Exception e) {
            System.out.println("Error al registrar usuario: " + e.getMessage());
            throw e;
        }

        // Ahora puedes acceder a userResponse aquí
        Usuario usuarioCreado = userResponse.getBody();
        assertNotNull(usuarioCreado);
        //Long usuarioId = usuarioCreado.getId();

        // 2. Crear un coche con matrícula única
        String matriculaUnica = "TEST" + System.currentTimeMillis();
        Coche coche = new Coche(matriculaUnica, "Toyota", "Corolla", 2020, "Rojo", 10000.0, true);

        // Agregar depuración
        try {
            ResponseEntity<Coche> cocheResponse = restTemplate.postForEntity("/api/coche/crear", coche, Coche.class);
            System.out.println("Respuesta coche: " + cocheResponse.getStatusCode() + " - Body: " + cocheResponse.getBody());
            assertEquals(HttpStatus.OK, cocheResponse.getStatusCode());
        } catch (Exception e) {
            System.out.println("Error al crear coche: " + e.getMessage());
            throw e;
        }

        Coche cocheCreado = coche;
        assertNotNull(cocheCreado);
        String cocheId = cocheCreado.getMatricula();

        // 3. Crear una reserva
        Reserva reserva = new Reserva(usuarioCreado, cocheCreado, LocalDate.now().toString(), 100.0, EstadoReserva.PENDIENTE);
        ResponseEntity<Reserva> reservaResponse = restTemplate.postForEntity("/api/reservas/crear", reserva,
                Reserva.class);
        assertEquals(HttpStatus.OK, reservaResponse.getStatusCode());
        Reserva reservaCreada = reservaResponse.getBody();
        assertNotNull(reservaCreada);
        int reservaId = reservaCreada.getId();

        // 4. Obtener todas las reservas y verificar que existe
        ResponseEntity<Reserva[]> reservasGetResponse = restTemplate.getForEntity("/api/reservas/pendientes", Reserva[].class);
        assertEquals(HttpStatus.OK, reservasGetResponse.getStatusCode());
        Reserva[] reservas = reservasGetResponse.getBody();
        assertNotNull(reservas);

        boolean reservaEncontrada = Arrays.stream(reservas)
                .anyMatch(r -> r.getId() == reservaId); 
        assertTrue(reservaEncontrada);

        // 5. Cancelar la reserva
        restTemplate.delete("/api/reservas/eliminar/" + reservaId);

        // 6. Eliminar el coche
        restTemplate.delete("/api/coche/eliminar?matricula=" + cocheId);

        // 7. Eliminar el usuario
        restTemplate.delete("/api/usuario/eliminar?email=" + usuario.getEmail());



        ResponseEntity<Reserva[]> reservasGetResponsePostDelete = restTemplate.getForEntity("/api/reservas/pendientes", Reserva[].class);
        assertEquals(HttpStatus.OK, reservasGetResponsePostDelete.getStatusCode());
        Reserva[] reservasPostDelete = reservasGetResponsePostDelete.getBody();
        assertNotNull(reservasPostDelete);

        boolean reservaEliminada = Arrays.stream(reservasPostDelete)
                .noneMatch(r -> r.getId() == reservaId);
        assertTrue(reservaEliminada);
    }
}
