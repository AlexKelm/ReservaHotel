package com.segovia.reservahotel.models;

public class Habitacion {
    private int idHabitacion;
    private int capacidad;
    private String tipoDeCama;       // Single, Doble, Queen, King
    private String caracteristicas;  // Estandar, Lujo
    private double precio;
    private String estado;           // Libre, Reservada, Ocupada

    public Habitacion() {}

    public Habitacion(int idHabitacion, int capacidad, String tipoDeCama,
                      String caracteristicas, double precio, String estado) {
        this.idHabitacion = idHabitacion;
        this.capacidad = capacidad;
        this.tipoDeCama = tipoDeCama;
        this.caracteristicas = caracteristicas;
        this.precio = precio;
        this.estado = estado;
    }

    public int getIdHabitacion() {
        return idHabitacion;
    }

    public void setIdHabitacion(int idHabitacion) {
        this.idHabitacion = idHabitacion;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public String getTipoDeCama() {
        return tipoDeCama;
    }

    public void setTipoDeCama(String tipoDeCama) {
        this.tipoDeCama = tipoDeCama;
    }

    public String getCaracteristicas() {
        return caracteristicas;
    }

    public void setCaracteristicas(String caracteristicas) {
        this.caracteristicas = caracteristicas;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Hab. " + idHabitacion + " - " + caracteristicas + " - " + estado;
    }
}