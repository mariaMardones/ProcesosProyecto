package com.example.restapi.service;

import com.example.restapi.model.Coche;
import com.example.restapi.repository.CocheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CocheService {

    private final CocheRepository cocheRepository;

    @Autowired
    public CocheService(CocheRepository cocheRepository) {
        this.cocheRepository = cocheRepository;
    }

    public List<Coche> getAllCoches() {
        return cocheRepository.findAll();
    }

    public Optional<Coche> getCocheByMatricula(String matricula) {
        return cocheRepository.findById(matricula);
    }

    public Coche GuardarCoche(Coche coche) {
        return cocheRepository.save(coche);
    }

    public Coche ActualizarCoche(String matricula, Coche Coche) {
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

    public void EliminarCoche(String matricula) {
        if (cocheRepository.existsById(matricula)) {
            cocheRepository.deleteById(matricula);
        } else {
            throw new RuntimeException("Coche con matricula: " + matricula + " no encontrado");
        }
    }
}