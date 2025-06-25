package com.deustocoches.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios") // Opcional: especificar nombre de la tabla
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellido;
    private String fechaNacimiento;
    private String email;
    private String password;
    private String tlf;

    @Enumerated(EnumType.STRING)
    private TipoRol rol; // Admin o Cliente
    
    private boolean bloqueado;
    
    // Constructor sin argumentos
    public Usuario() {
    	this.bloqueado = false;
    }
    
    public Usuario( String nombre, String apellido, String fechaNacimiento, String email, String password,
    String tlf) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.email = email;
        this.password = password;
        this.tlf = tlf;
        this.rol = TipoRol.CLIENTE;
        this.bloqueado = false;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
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

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }
    
    
    @Override
    public String toString() {
        return "Usuario [id=" + id + ", nombre=" + nombre + ", apellido=" + apellido + 
        		", fechaNacimiento=" + fechaNacimiento + ", email=" + email + 
        		", password=" + password + ", tlf=" + tlf + ", rol=" + rol +
                ", bloqueado=" + bloqueado + "]";
    }
}
