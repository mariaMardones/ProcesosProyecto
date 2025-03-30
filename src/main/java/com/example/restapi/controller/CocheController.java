package main.java.com.example.restapi.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.restapi.model.Coche;
import main.java.com.example.restapi.service.CocheService;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coche")
@Tag(name = "Coche Controller", description = "API para manejar los coches")
public class CocheController {

    @Autowired
    private com.example.restapi.service.CocheService cocheService;

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
}
