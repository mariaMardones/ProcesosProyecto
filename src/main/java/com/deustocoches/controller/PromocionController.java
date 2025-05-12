package com.deustocoches.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.deustocoches.model.Promocion;
import com.deustocoches.service.PromocionService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/promociones")
@Tag(name = "Promocion Controller", description = "API para gestionar promociones")
public class PromocionController {

    @Autowired
    private PromocionService promocionService;

    @PostMapping("/crear")
    public ResponseEntity<Promocion> crearPromocion(@RequestBody Promocion promocion) {
        try {
            Promocion nueva = promocionService.crearPromocion(promocion);
            return ResponseEntity.ok(nueva);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public List<Promocion> obtenerTodas() {
        return promocionService.obtenerTodas();
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarPromocion(@PathVariable Integer id) {
        if (promocionService.eliminarPromocion(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
