package com.segovia.reservahotel.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Reserva {
    private int idReserva;
    private int idUsuario;
    private int idCliente;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado;
    private double abono;
    private LocalDateTime fechaCreacion;
    private double precioTotal; // Nuevo campo

    // Getters y Setters

    public int getIdReserva() { return idReserva; }
    public void setIdReserva(int idReserva) { this.idReserva = idReserva; }
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public double getAbono() { return abono; }
    public void setAbono(double abono) { this.abono = abono; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public double getPrecioTotal() { return precioTotal; }
    public void setPrecioTotal(double precioTotal) { this.precioTotal = precioTotal; }
}
