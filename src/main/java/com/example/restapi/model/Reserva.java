package com.example.restapi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "coche_matricula", nullable = false)
    private Coche coche;

    private String fecha;
    private double precioTotal;

    @Enumerated(EnumType.STRING)
    private EstadoReserva estado;

    // Constructor sin argumentos
    public Reserva() {
    }

    // Constructor con todos los argumentos
    public Reserva(Usuario usuario, Coche coche, String fecha, double precioTotal, EstadoReserva estado) {
        this.usuario = usuario;
        this.coche = coche;
        this.fecha = fecha;
        this.precioTotal = precioTotal;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
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
        return "Reserva [idReserva=" + id + ", usuario=" + usuario + ", coche=" + coche + ", fecha=" + fecha + ", precioTotal=" + precioTotal + ", estado=" + estado + "]";
    }
}
