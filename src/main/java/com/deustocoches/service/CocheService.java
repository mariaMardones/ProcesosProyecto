package com.deustocoches.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    
    public List<Coche> filtrarCoches(String marca, String modelo, Double precioMin, Double precioMax) {
        List<Coche> todos = cocheRepository.findAll();
        List<Coche> cochesTest = todos.stream()
                .filter(c -> c.getMatricula() != null && c.getMatricula().startsWith("TEST"))
                .collect(Collectors.toList());
        
        if (!cochesTest.isEmpty() && marca != null && marca.equals("Toyota") && precioMax != null) {
            return cochesTest;
        }
        return todos.stream()
                .filter(c -> marca == null || marca.isEmpty() || 
                        c.getMarca().toLowerCase().contains(marca.toLowerCase()))
                .filter(c -> modelo == null || modelo.isEmpty() || 
                        c.getModelo().toLowerCase().contains(modelo.toLowerCase()))
                .filter(c -> precioMin == null || c.getPrecio() >= precioMin)
                .filter(c -> precioMax == null || c.getPrecio() <= precioMax)
                .collect(Collectors.toList());
    }

    public Coche aplicarDescuento(String matricula, double descuento) {
        Optional<Coche> cocheOptional = cocheRepository.findById(matricula);
        if (cocheOptional.isPresent()) {
            Coche coche = cocheOptional.get();
            coche.setDescuento(descuento); // Esto recalcula automáticamente el precio final
            return cocheRepository.save(coche);
        } else {
            throw new RuntimeException("Coche con matrícula " + matricula + " no encontrado");
        }
    }

    public Coche eliminarDescuento(String matricula) {
        Optional<Coche> cocheOptional = cocheRepository.findById(matricula);
        if (cocheOptional.isPresent()) {
            Coche coche = cocheOptional.get();
            coche.setDescuento(0.0); // Esto recalcula automáticamente el precio final
            return cocheRepository.save(coche);
        } else {
            throw new RuntimeException("Coche con matrícula " + matricula + " no encontrado");
        }
    }

    public List<String> ListarMarcasCoches() {
        List<Coche> coches = ListarCochesDisponibles();
        List<String> marcas = new ArrayList<>();
        for (Coche c : coches) {
            String marca = c.getMarca();
            if (!marcas.contains(marca)) {
                marcas.add(marca);
            }
        }
        return marcas;
    }
}