package es.uDeusto.G15procesos.entity;

import java.sql.Date;

public class Usuario {
	int id;
	String nombre;
	String apellido;
	Date fechaNacimiento;
	String email;
	String password;
	String tlf;
	TipoRol rol; //Admin o cliente
	
	
	
	public Usuario() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	public Usuario(int id, String nombre, String apellido, Date fechaNacimiento, String email, String password,
			String tlf, TipoRol rol) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.apellido = apellido;
		this.fechaNacimiento = fechaNacimiento;
		this.email = email;
		this.password = password;
		this.tlf = tlf;
		this.rol = rol;
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	public Date getFechaNacimiento() {
		return fechaNacimiento;
	}
	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getTlf() {
		return tlf;
	}
	public void setTlf(String tlf) {
		this.tlf = tlf;
	}
	public TipoRol getRol() {
		return rol;
	}
	public void setRol(TipoRol rol) {
		this.rol = rol;
	}
	
	
	
	@Override
	public String toString() {
		return "Usuario [id=" + id + ", nombre=" + nombre + ", apellido=" + apellido + ", fechaNacimiento="
				+ fechaNacimiento + ", email=" + email + ", password=" + password + ", tlf=" + tlf + ", rol=" + rol
				+ "]";
	}
	
}
