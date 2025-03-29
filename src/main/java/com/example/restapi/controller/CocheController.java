import java.util.List;
import java.util.Optional;

import com.example.restapi.model.Coche;

import main.java.com.example.restapi.service.CocheService;

@RestController
@RequestMapping("/api/coche")
@Tag(name = "Coche Controller", description = "API para manejar los coches")
public class CocheController {

    @Autowired
    private CocheService cocheService;

    @GetMapping
    public List<Coche> getAllCoches() {
        return cocheService.getAllCoches();
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
}
