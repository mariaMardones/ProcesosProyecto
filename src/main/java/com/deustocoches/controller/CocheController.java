package com.deustocoches.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.deustocoches.model.Coche;
import com.deustocoches.service.CocheService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/coche")
@Tag(name = "Coche Controller", description = "API para manejar los coches")
public class CocheController {

    @Autowired
    private CocheService cocheService;

    @GetMapping
    public List<Coche> ListarCoches() {
        return cocheService.ListarCoches();
    }

    @GetMapping("/buscar")
    public ResponseEntity<Coche> getCocheByMatricula(@RequestParam("matricula") String matricula) {
        Optional<Coche> coche = cocheService.getCocheByMatricula(matricula);
        return coche.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/crear")
    public ResponseEntity<Coche> crearCoche(@RequestBody Coche coche) {
        try {
            Coche cochenuevo = cocheService.guardarCoche(coche);
            return ResponseEntity.ok(cochenuevo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/actualizar")
    public ResponseEntity<Coche> actualizarCoche(@RequestParam("matricula") String matricula, @RequestBody Coche coche) {
        try {
            Coche cocheActualizado = cocheService.actualizarCoche(matricula, coche);
            return ResponseEntity.ok(cocheActualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/eliminar")
    public ResponseEntity<Void> eliminarCoche(@RequestParam("matricula") String matricula) {
        Optional<Coche> coche = cocheService.getCocheByMatricula(matricula);
        if (coche.isPresent()) {
            cocheService.eliminarCoche(matricula);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/disponibles")
    public List<Coche> ListarCochesDisponibles() {
        return cocheService.ListarCochesDisponibles();
    }
    
    @GetMapping("/filtrar")
    public ResponseEntity<List<Coche>> filtrarCoches(
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String modelo,
            @RequestParam(required = false) Double precioMin,
            @RequestParam(required = false) Double precioMax) {
        try {
            List<Coche> cochesFiltrados = cocheService.filtrarCoches(marca, modelo, precioMin, precioMax);
            return ResponseEntity.ok(cochesFiltrados);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/aplicarDescuento")
    public ResponseEntity<Coche> aplicarDescuento(@RequestParam("matricula") String matricula, @RequestParam("descuento") double descuento) {
        try {
            Coche cocheActualizado = cocheService.aplicarDescuento(matricula, descuento);
            return ResponseEntity.ok(cocheActualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/eliminarDescuento")
    public ResponseEntity<Coche> eliminarDescuento(@RequestParam("matricula") String matricula) {
        try {
            Coche cocheActualizado = cocheService.eliminarDescuento(matricula);
            return ResponseEntity.ok(cocheActualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/marcas")
    public List<String> ListarMarcasCoches() {
        return cocheService.ListarMarcasCoches();
    }
}
