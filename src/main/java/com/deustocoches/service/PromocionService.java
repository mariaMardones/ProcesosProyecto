package com.deustocoches.service;

import com.deustocoches.repository.PromocionRepository;
import com.deustocoches.model.Promocion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public void eliminarPromocion(Long id) {
        promocionRepository.deleteById(id);
    }
}

