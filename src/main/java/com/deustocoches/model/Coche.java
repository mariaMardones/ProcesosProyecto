package com.deustocoches.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "coches") // Opcional: especificar nombre de la tabla
public class Coche {

    @Id
    private String matricula; // Identificador único

    private String marca;
    private String modelo;
    private int anio;
    private String color;
    private double precio;
    private boolean disponible;

    private double descuento;
    private double precioFinal;

    // Constructor sin argumentos
    public Coche() {
    }

    // Constructor con todos los argumentos
    public Coche(String matricula, String marca, String modelo, int anio, String color, double precio, boolean disponible) {
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.color = color;
        this.precio = precio;
        this.disponible = disponible;
        this.descuento = 0.0; // Inicializar descuento a 0
        this.precioFinal = precio; // Inicializar precio final al precio original
    }

    // Getters y setters
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

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
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

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
        calcularPrecioFinal();
    }

    public double getPrecioFinal() {
        return precioFinal;
    }

    private void calcularPrecioFinal() {
        if (descuento > 0) {
            precioFinal = precio - (precio * (descuento / 100));
        } else {
            precioFinal = precio;
        }
    }

    @Override
    public String toString() {
        return "Coche{" +
                "matricula='" + matricula + '\'' +
                ", marca='" + marca + '\'' +
                ", modelo='" + modelo + '\'' +
                ", anio=" + anio +
                ", color='" + color + '\'' +
                ", precio=" + precio +
                ", disponible=" + disponible +
                ", descuento=" + descuento +
                ", precioFinal=" + precioFinal +
                '}';
    }
}
