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
                Usuario cliente3 = new Usuario("Lucía", "Martínez", "1992-07-22", "lucia.martinez@gmail.com", "password4", "600000005");
                Usuario clienteBloqueado = new Usuario("Carlos", "Ruiz", "1995-03-10", "carlosruiz@gmail.com", "password3", "600000004");
                
                clienteBloqueado.setBloqueado(true); 
                admin.setRol(TipoRol.ADMIN);

                usuarioRepo.saveAll(List.of(admin, cliente1, cliente2, cliente3, clienteBloqueado));
                logger.info("Usuarios guardados!");

                // Inicialización de coches (20 coches, 15 disponibles, marcas/modelos repetidos)
                Coche coche1 = new Coche("1234ABC", "Toyota", "Corolla", 2020, "Blanco", 4000.0, true);
                Coche coche2 = new Coche("5678DEF", "Ford", "Focus", 2019, "Negro", 2999.99, true);
                Coche coche3 = new Coche("9012GHI", "Volkswagen", "Golf", 2021, "Rojo", 10000.0, false);
                Coche coche4 = new Coche("3456JKL", "Seat", "Ibiza", 2018, "Azul", 3000.5, true);
                Coche coche5 = new Coche("7890MNO", "Toyota", "Corolla", 2022, "Gris", 12000.0, true);
                Coche coche6 = new Coche("2468PQR", "Ford", "Focus", 2020, "Blanco", 8500.0, true);
                Coche coche7 = new Coche("1357STU", "Volkswagen", "Golf", 2017, "Negro", 7000.0, true);
                Coche coche8 = new Coche("1122VWX", "Seat", "Ibiza", 2019, "Rojo", 5000.0, true);
                Coche coche9 = new Coche("3344YZA", "Renault", "Clio", 2016, "Azul", 4500.0, false);
                Coche coche10 = new Coche("5566BCD", "Peugeot", "208", 2021, "Blanco", 9500.0, true);
                Coche coche11 = new Coche("7788EFG", "Toyota", "Corolla", 2018, "Negro", 6000.0, true);
                Coche coche12 = new Coche("9900HIJ", "Ford", "Focus", 2015, "Gris", 3500.0, false);
                Coche coche13 = new Coche("2233KLM", "Volkswagen", "Golf", 2022, "Azul", 15000.0, true);
                Coche coche14 = new Coche("4455NOP", "Seat", "Ibiza", 2020, "Blanco", 8000.0, true);
                Coche coche15 = new Coche("6677QRS", "Renault", "Clio", 2017, "Rojo", 4800.0, true);
                Coche coche16 = new Coche("8899TUV", "Peugeot", "208", 2019, "Negro", 7200.0, true);
                Coche coche17 = new Coche("1011WXY", "Toyota", "Corolla", 2021, "Azul", 11000.0, true);
                Coche coche18 = new Coche("1213ZAB", "Ford", "Focus", 2016, "Blanco", 4000.0, false);
                Coche coche19 = new Coche("1415CDE", "Volkswagen", "Golf", 2018, "Gris", 8000.0, true);
                Coche coche20 = new Coche("1617FGH", "Seat", "Ibiza", 2022, "Negro", 13000.0, true);
                coche1.setDescuento(10);
                coche5.setDescuento(5);
                coche10.setDescuento(15);
                coche13.setDescuento(20);
                coche17.setDescuento(8);
                cocheRepo.saveAll(List.of(
                    coche1, coche2, coche3, coche4, coche5, coche6, coche7, coche8, coche9, coche10,
                    coche11, coche12, coche13, coche14, coche15, coche16, coche17, coche18, coche19, coche20
                ));
                logger.info("Coches guardados!");

                // Inicialización de reservas (al menos una por cliente, algunas más)
                Reserva reserva1 = new Reserva(cliente1, coche1, "2025-05-10", 4000.0, EstadoReserva.COMPRADA);
                Reserva reserva2 = new Reserva(cliente2, coche2, "2025-05-12", 2999.99, EstadoReserva.PENDIENTE);
                Reserva reserva3 = new Reserva(cliente1, coche3, "2025-05-15", 10000.0, EstadoReserva.CANCELADA);
                Reserva reserva4 = new Reserva(cliente3, coche5, "2025-06-01", 12000.0, EstadoReserva.PENDIENTE);
                Reserva reserva5 = new Reserva(cliente2, coche14, "2025-06-15", 8000.0, EstadoReserva.PENDIENTE);
                Reserva reserva6 = new Reserva(cliente3, coche11, "2025-06-20", 6000.0, EstadoReserva.COMPRADA);

                reservaRepo.saveAll(List.of(reserva1, reserva2, reserva3, reserva4, reserva5, reserva6));
                logger.info("Reservas guardadas!");
            }
        };
    }
}