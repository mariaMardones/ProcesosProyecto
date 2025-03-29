package com.example.restapi.repository;
import com.example.restapi.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
    public interface ReservaRepository extends JpaRepository<Reserva, String> {
}