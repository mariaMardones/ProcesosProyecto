package es.uDeusto.G15procesos.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    private String idReserva;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "coche_matricula", nullable = false)
    private Coche coche;

    private LocalDate fecha;
    private double precioTotal;

    @Enumerated(EnumType.STRING)
    private EstadoReserva estado;

    // Constructor sin argumentos
    public Reserva() {
    }

    // Constructor con todos los argumentos
    public Reserva(String idReserva, Usuario usuario, Coche coche, LocalDate fecha, double precioTotal, EstadoReserva estado) {
        this.idReserva = idReserva;
        this.usuario = usuario;
        this.coche = coche;
        this.fecha = fecha;
        this.precioTotal = precioTotal;
        this.estado = estado;
    }

    // Getters y setters
    public String getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(String idReserva) {
        this.idReserva = idReserva;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Coche getCoche() {
        return coche;
    }

    public void setCoche(Coche coche) {
        this.coche = coche;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public double getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(double precioTotal) {
        this.precioTotal = precioTotal;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Reserva [idReserva=" + idReserva + ", usuario=" + usuario + ", coche=" + coche + ", fecha=" + fecha + ", precioTotal=" + precioTotal + ", estado=" + estado + "]";
    }
}
