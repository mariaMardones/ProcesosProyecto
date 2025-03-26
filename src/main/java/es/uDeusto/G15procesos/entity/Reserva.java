package es.uDeusto.G15procesos.entity;

import java.time.LocalDate;

public class Reserva {
	String idReserva;
	Usuario usuario;
	Coche coche;
	LocalDate fechaInicio;
	LocalDate fechaFin;
	double precioTotal;
	TipoEstado estado;
	
	
	public Reserva() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	public Reserva(String idReserva, Usuario usuario, Coche coche, LocalDate fechaInicio, LocalDate fechaFin,
			double precioTotal, TipoEstado estado) {
		super();
		this.idReserva = idReserva;
		this.usuario = usuario;
		this.coche = coche;
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
		this.precioTotal = precioTotal;
		this.estado = estado;
	}
	
	
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
	public LocalDate getFechaInicio() {
		return fechaInicio;
	}
	public void setFechaInicio(LocalDate fechaInicio) {
		this.fechaInicio = fechaInicio;
	}
	public LocalDate getFechaFin() {
		return fechaFin;
	}
	public void setFechaFin(LocalDate fechaFin) {
		this.fechaFin = fechaFin;
	}
	public double getPrecioTotal() {
		return precioTotal;
	}
	public void setPrecioTotal(double precioTotal) {
		this.precioTotal = precioTotal;
	}
	public TipoEstado getEstado() {
		return estado;
	}
	public void setEstado(TipoEstado estado) {
		this.estado = estado;
	}
	
	
	@Override
	public String toString() {
		return "Reserva [idReserva=" + idReserva + ", usuario=" + usuario + ", coche=" + coche + ", fechaInicio="
				+ fechaInicio + ", fechaFin=" + fechaFin + ", precioTotal=" + precioTotal + ", estado=" + estado + "]";
	}

	
}
