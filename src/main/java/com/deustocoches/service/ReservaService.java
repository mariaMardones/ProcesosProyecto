package com.deustocoches.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.deustocoches.model.EstadoReserva;
import com.deustocoches.model.Reserva;
import com.deustocoches.repository.ReservaRepository;

@Service
public class ReservaService {
	private final ReservaRepository reservaRepository;

    @Autowired
    public ReservaService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    public List<Reserva> obtenerReservas() {
        return reservaRepository.findAll();
    }

    public List<Reserva> obtenerReservasConfirmadas() {
        return reservaRepository.findByEstado(EstadoReserva.COMPRADA);
    }

    public Optional<Reserva> obtenerReservaPorId(Integer id) {
        return reservaRepository.findById(id);
    }

    public Reserva crearReserva(Reserva reserva) {
        return reservaRepository.save(reserva);
    }

    public Reserva actualizarReserva(Integer id, Reserva detallesReserva) {
        return reservaRepository.findById(id)
                .map(reserva -> {
                    reserva.setFecha(detallesReserva.getFecha());
                    reserva.setPrecioTotal(detallesReserva.getPrecioTotal());
                    reserva.setEstado(detallesReserva.getEstado());
                    reserva.setUsuario(detallesReserva.getUsuario());
                    reserva.setCoche(detallesReserva.getCoche());
                    return reservaRepository.save(reserva);
                })
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
    }

    public boolean eliminarReserva(Integer id) {
        if (reservaRepository.existsById(id)) {
            reservaRepository.deleteById(id);
            return true;
        }
        return false;
    }

}