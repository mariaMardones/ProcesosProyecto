package com.deustocoches.controller;

import com.deustocoches.model.Promocion;
import com.deustocoches.service.PromocionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/promociones")
public class PromocionController {

    @Autowired
    private PromocionService promocionService;

    @PostMapping
    public Promocion crear(@RequestBody Promocion promocion) {
        return promocionService.crearPromocion(promocion);
    }

    @GetMapping
    public List<Promocion> listar() {
        return promocionService.obtenerTodas();
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        promocionService.eliminarPromocion(id);
    }
}
