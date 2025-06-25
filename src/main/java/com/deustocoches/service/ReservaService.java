package com.deustocoches.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.deustocoches.model.EstadoReserva;
import com.deustocoches.model.Reserva;
import com.deustocoches.model.Usuario;
import com.deustocoches.repository.ReservaRepository;
import com.deustocoches.repository.UsuarioRepository;

@Service
public class ReservaService {
	private final ReservaRepository reservaRepository;
	private final UsuarioRepository usuarioRepository;

    public ReservaService(ReservaRepository reservaRepository, UsuarioRepository usuarioRepository) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
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

    public Reserva hacerPedido(Reserva reserva) {
        return reservaRepository.save(reserva);
    }

    public Reserva actualizarReserva(Integer id, Reserva detallesReserva) {
        return reservaRepository.findById(id)
                .map(reserva -> {
                    EstadoReserva estadoAnterior = reserva.getEstado(); // Guardamos el estado previo

                    reserva.setFecha(detallesReserva.getFecha());
                    reserva.setPrecioTotal(detallesReserva.getPrecioTotal());
                    reserva.setEstado(detallesReserva.getEstado());
                    reserva.setUsuario(detallesReserva.getUsuario());
                    reserva.setCoche(detallesReserva.getCoche());

                    if (estadoAnterior == EstadoReserva.PENDIENTE && detallesReserva.getEstado() == EstadoReserva.COMPRADA) {
                        Usuario usuario = reserva.getUsuario();
                        usuario.setPuntos(usuario.getPuntos() + 10);
                        usuarioRepository.save(usuario);
                    }

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

    public List<Reserva> obtenerTodasReservas() {
        return reservaRepository.findAll();
    }
    
}