package com.example.restapi.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.restapi.model.EstadoReserva;
import com.example.restapi.model.Reserva;

@Repository
    public interface ReservaRepository extends JpaRepository<Reserva, Integer> {
        List<Reserva> findByEstado(EstadoReserva estado);
}