package com.example.restapi.repository;
import com.example.restapi.model.Coche;
import com.example.restapi.model.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
    public interface CocheRepository extends JpaRepository<Coche, String> {
        Coche findByMatricula(String matricula) ;
}