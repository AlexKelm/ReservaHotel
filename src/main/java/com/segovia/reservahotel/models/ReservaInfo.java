package com.segovia.reservahotel.models;

import java.time.LocalDate;

public class ReservaInfo {
    private int idReserva;
    private String clienteNombreCompleto;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado;
    private double abono;
    private double precioTotal; // Nuevo campo

    public ReservaInfo(int idReserva, String clienteNombreCompleto, LocalDate fechaInicio, LocalDate fechaFin, String estado, double abono, double precioTotal) {
        this.idReserva = idReserva;
        this.clienteNombreCompleto = clienteNombreCompleto;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
        this.abono = abono;
        this.precioTotal = precioTotal;
    }

    // Getters
    public int getIdReserva() { return idReserva; }
    public String getClienteNombreCompleto() { return clienteNombreCompleto; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public String getEstado() { return estado; }
    public double getAbono() { return abono; }
    public double getPrecioTotal() { return precioTotal; }
}
