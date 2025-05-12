package com.deustocoches.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.deustocoches.model.Promocion;
import com.deustocoches.repository.PromocionRepository;

@Service
public class PromocionService {

    @Autowired
    private PromocionRepository promocionRepository;

    public Promocion crearPromocion(Promocion promocion) {
        return promocionRepository.save(promocion);
    }

    public List<Promocion> obtenerTodas() {
        return promocionRepository.findAll();
    }

    public boolean eliminarPromocion(Integer id) {
        Optional<Promocion> promocion = promocionRepository.findById(id);
        if (promocion.isPresent()) {
            promocionRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
