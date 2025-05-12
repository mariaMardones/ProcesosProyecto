package com.deustocoches;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import com.deustocoches.model.Coche;
import com.deustocoches.model.EstadoReserva;
import com.deustocoches.model.Reserva;
import com.deustocoches.model.TipoRol;
import com.deustocoches.model.Usuario;
import com.deustocoches.repository.CocheRepository;
import com.deustocoches.repository.ReservaRepository;
import com.deustocoches.repository.UsuarioRepository;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    @Transactional
    CommandLineRunner initData(
            UsuarioRepository usuarioRepo,
            CocheRepository cocheRepo,
            ReservaRepository reservaRepo) {
        return args -> {
            // Elimina todo solo si las tablas están vacías
            if (usuarioRepo.count() == 0 && cocheRepo.count() == 0 && reservaRepo.count() == 0) {
                reservaRepo.deleteAll();
                usuarioRepo.deleteAll();
                cocheRepo.deleteAll();

                // Inicialización de usuarios
                Usuario admin = new Usuario("Admin", "Principal", "1980-01-01", "admin@deusto.com", "adminpass", "600000001");
                Usuario cliente1 = new Usuario("Juan", "Pérez", "1990-05-15", "juanperez@gmail.com", "password1", "600000002");
                Usuario cliente2 = new Usuario("Ana", "López", "1985-10-20", "analopez@gmail.com", "password2", "600000003");
                Usuario clienteBloqueado = new Usuario("Carlos", "Ruiz", "1995-03-10", "carlosruiz@gmail.com", "password3", "600000004");
                
                clienteBloqueado.setBloqueado(true); 
                admin.setRol(TipoRol.ADMIN);

                usuarioRepo.saveAll(List.of(admin, cliente1, cliente2, clienteBloqueado));
                logger.info("Usuarios guardados!");

                // Inicialización de coches
                Coche coche1 = new Coche("1234ABC", "Toyota", "Corolla", 2020, "Blanco", 45.0, true);
                Coche coche2 = new Coche("5678DEF", "Ford", "Focus", 2019, "Negro", 40.0, true);
                Coche coche3 = new Coche("9012GHI", "Volkswagen", "Golf", 2021, "Rojo", 50.0, false);
                Coche coche4 = new Coche("3456JKL", "Seat", "Ibiza", 2018, "Azul", 38.0, true);

                cocheRepo.saveAll(List.of(coche1, coche2, coche3, coche4));
                logger.info("Coches guardados!");

                // Inicialización de reservas
                Reserva reserva1 = new Reserva(cliente1, coche1, "2025-05-10", 9000.0, EstadoReserva.COMPRADA);
                Reserva reserva2 = new Reserva(cliente2, coche2, "2025-05-12", 8000.0, EstadoReserva.PENDIENTE);
                Reserva reserva3 = new Reserva(cliente1, coche3, "2025-05-15", 10000.0, EstadoReserva.CANCELADA);

                reservaRepo.saveAll(List.of(reserva1, reserva2, reserva3));
                logger.info("Reservas guardadas!");
            }
        };
    }
}