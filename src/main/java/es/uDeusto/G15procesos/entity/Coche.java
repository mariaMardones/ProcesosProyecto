package es.uDeusto.G15procesos.entity;

public class Coche {
	String matricula; // Id
	String marca;
	String modelo;
	int año;
	String color;
	double precio;
	boolean disponible;
	
	
	public Coche() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Coche(String matricula, String marca, String modelo, int año, String color, double precio,
			boolean disponible) {
		super();
		this.matricula = matricula;
		this.marca = marca;
		this.modelo = modelo;
		this.año = año;
		this.color = color;
		this.precio = precio;
		this.disponible = disponible;
	}
	
	
	public String getMatricula() {
		return matricula;
	}
	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}
	public String getMarca() {
		return marca;
	}
	public void setMarca(String marca) {
		this.marca = marca;
	}
	public String getModelo() {
		return modelo;
	}
	public void setModelo(String modelo) {
		this.modelo = modelo;
	}
	public int getAño() {
		return año;
	}
	public void setAño(int año) {
		this.año = año;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public double getPrecio() {
		return precio;
	}
	public void setPrecio(double precio) {
		this.precio = precio;
	}
	public boolean isDisponible() {
		return disponible;
	}
	public void setDisponible(boolean disponible) {
		this.disponible = disponible;
	}
	
	
	@Override
	public String toString() {
		return "Coche [matricula=" + matricula + ", marca=" + marca + ", modelo=" + modelo + ", año=" + año + ", color="
				+ color + ", precio=" + precio + ", disponible=" + disponible + "]";
	}
	
	
}
