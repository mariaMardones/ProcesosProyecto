package com.deustocoches.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.deustocoches.model.EstadoReserva;
import com.deustocoches.model.Reserva;
import com.deustocoches.repository.ReservaRepository;

@Service
public class ReservaService {
	private final ReservaRepository reservaRepository;

    public ReservaService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
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
    
    public List<Reserva> obtenerReservasPorFecha(String fecha) {
        return reservaRepository.findByFecha(fecha);
    }

    public List<Reserva> obtenerReservasPorRangoFechas(String desde, String hasta) {
        return reservaRepository.findByFechaBetween(desde, hasta);
    }



    public List<Reserva> obtenerReservasCompradasPorUsuario(String emailUsuario) {
        return reservaRepository.findByUsuarioEmailAndEstado(emailUsuario, EstadoReserva.COMPRADA);
    }

    public List<Reserva> obtenerReservasPendientesPorUsuario(String emailUsuario) {
        return reservaRepository.findByUsuarioEmailAndEstado(emailUsuario, EstadoReserva.PENDIENTE);
    }

    public List<Reserva> obtenerCompradas() {
        return reservaRepository.findByEstado(EstadoReserva.COMPRADA);
    }

    public List<Reserva> obtenerPendientes() {
        return reservaRepository.findByEstado(EstadoReserva.PENDIENTE);
    }
    
    public List<Reserva> findAll() {
        return reservaRepository.findAll();
    }

    public Reserva aplicarDescuento(Integer id, double descuento) {
        return reservaRepository.findById(id)
                .map(reserva -> {
                    reserva.setDescuento(descuento);
                    reserva.setPrecioTotal(reserva.getPrecioTotal() - (reserva.getPrecioTotal() * (descuento / 100)));
                    return reservaRepository.save(reserva);
                })
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
    }

    public Reserva eliminarDescuento(Integer id) {
        return reservaRepository.findById(id)
                .map(reserva -> {
                    reserva.setPrecioTotal(reserva.getPrecioTotal() / (1 - (reserva.getDescuento() / 100)));
                    reserva.setDescuento(0.0);
                    return reservaRepository.save(reserva);
                })
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
    }
}