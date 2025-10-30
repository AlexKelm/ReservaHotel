package com.segovia.reservahotel.models;

import java.time.LocalDate;

public class Reserva {
    private int idReserva;
    private int idUsuario;
    private int idCliente;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado; // Pendiente, Confirmada, Anulada
    private double abono;

    public Reserva() {}

    public Reserva(int idReserva, int idUsuario, int idCliente,
                   LocalDate fechaInicio, LocalDate fechaFin,
                   String estado, double abono) {
        this.idReserva = idReserva;
        this.idUsuario = idUsuario;
        this.idCliente = idCliente;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
        this.abono = abono;
    }

    public int getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getAbono() {
        return abono;
    }

    public void setAbono(double abono) {
        this.abono = abono;
    }
}