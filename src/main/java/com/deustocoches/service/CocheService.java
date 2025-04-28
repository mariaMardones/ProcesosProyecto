package com.deustocoches.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.deustocoches.model.Coche;
import com.deustocoches.repository.CocheRepository;

@Service
public class CocheService {

    private final CocheRepository cocheRepository;
    public CocheService(CocheRepository cocheRepository) {
        this.cocheRepository = cocheRepository;
    }

    public List<Coche> ListarCoches() {
        return cocheRepository.findAll();
    }
    public List<Coche> ListarCochesDisponibles() {
        List<Coche> coches =  cocheRepository.findAll();
        List<Coche> cochesdisponibles = new ArrayList<>();
        for (Coche c: coches){
            if (c.isDisponible()) {
                cochesdisponibles.add(c);
            }
        }
        return cochesdisponibles;
    }

    public Optional<Coche> getCocheByMatricula(String matricula) {
        return cocheRepository.findById(matricula);
    }

    public Coche guardarCoche(Coche coche) {
        if (cocheRepository.findByMatricula(coche.getMatricula()) != null) {
            throw new IllegalArgumentException("El coche ya está registrado.");
        }
        return cocheRepository.save(coche);
    }

    public Coche actualizarCoche(String matricula, Coche Coche) {
        Optional<Coche> c = cocheRepository.findById(matricula);
        if (c.isPresent()) {
            Coche x = c.get();
            x.setAnio(Coche.getAnio());
            x.setColor(Coche.getColor());
            x.setDisponible(Coche.isDisponible());
            x.setMarca(Coche.getMarca());
            x.setMatricula(Coche.getMatricula());
            x.setModelo(Coche.getModelo());
            x.setPrecio(Coche.getPrecio());
            return cocheRepository.save(x);
        } else {
            throw new RuntimeException("Coche not encontrado");
        }
    }

    public void eliminarCoche(String matricula) {
        if (cocheRepository.existsById(matricula)) {
            cocheRepository.deleteById(matricula);
        } else {
            throw new RuntimeException("Coche con matricula: " + matricula + " no encontrado");
        }
    }
}