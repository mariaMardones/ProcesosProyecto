package com.deustocoches.repository;

import com.deustocoches.model.Reserva;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.deustocoches.model.EstadoReserva;

@Repository
    public interface ReservaRepository extends JpaRepository<Reserva, Integer> {
        List<Reserva> findByEstado(EstadoReserva estado);
        List<Reserva> findByUsuarioEmailAndEstado(String email, EstadoReserva estado);
        
        List<Reserva> findByFecha(String fecha);
        List<Reserva> findByFechaBetween(String desde, String hasta);
        
        List<Reserva> findAll();
}