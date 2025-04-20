package com.deustocoches.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.deustocoches.model.EstadoReserva;
import com.deustocoches.model.Reserva;
import com.deustocoches.service.ReservaService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/reservas")
@Tag(name = "Reserva Controller", description = "API para gestionar reservas")
public class ReservaController {

	@Autowired
    private ReservaService reservaService;
    @Autowired
    private CocheController cocheController;

    @GetMapping("/buscar/{id}")
    public ResponseEntity<Reserva> obtenerReservaPorId(@PathVariable Integer id) {
        return reservaService.obtenerReservaPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/crear")
    public ResponseEntity<Reserva> crearReserva(@RequestBody Reserva reserva) {
        try {
            Reserva nuevaReserva = reservaService.crearReserva(reserva);
            return ResponseEntity.ok(nuevaReserva);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Reserva> actualizarReserva(@PathVariable Integer id, @RequestBody Reserva detallesReserva) {
        try {
            Reserva reservaActualizada = reservaService.actualizarReserva(id, detallesReserva);
            return ResponseEntity.ok(reservaActualizada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarReserva(@PathVariable Integer id) {
        if (reservaService.eliminarReserva(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/pedidos")
    public ResponseEntity<Reserva> hacerPedido(@RequestBody Reserva reserva) {
        if (reserva.getUsuario() == null || reserva.getCoche() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    
        if (reserva.getEstado() == null) {
            reserva.setEstado(EstadoReserva.PENDIENTE);
        }
    
        if (reserva.getFecha() == null || reserva.getFecha().isEmpty()) {
            reserva.setFecha(LocalDate.now().toString());
        }

        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            reserva.getCoche().setDisponible(true);
        }
        else{
            reserva.getCoche().setDisponible(false);
        }

        cocheController.actualizarCoche(reserva.getCoche().getMatricula(), reserva.getCoche());
        Reserva nuevaReserva = reservaService.crearReserva(reserva);
        return new ResponseEntity<>(nuevaReserva, HttpStatus.CREATED);
    }
    
    @GetMapping("/usuario/confirmadas")
    public List<Reserva> obtenerReservasConfirmadasPorUsuario(@RequestParam("email") String email) {
        return reservaService.obtenerReservasCompradasPorUsuario(email);
    }

    @GetMapping("/usuario/pendientes")
    public List<Reserva> obtenerReservasPorConfirmasPorUsuario(@RequestParam("email") String email) {
        return reservaService.obtenerReservasPendientesPorUsuario(email);
    }

    @GetMapping("/compradas")
    public List<Reserva> obtenerReservasCompradas() {
        return reservaService.obtenerCompradas();
    }

    @GetMapping("/pendientes")
    public List<Reserva> obtenerReservasPendientes() {
        return reservaService.obtenerPendientes();
    }

    @GetMapping("/canceladas")
    public List<Reserva> obtenerReservasCanceladas() {
        return reservaService.obtenerCanceladas();
    }
    
}