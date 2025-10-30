package com.segovia.reservahotel.models;

import java.time.LocalDate;

public class Reporte {
    private int idReporte;
    private String tipo;
    private LocalDate fechaGeneracion;

    public Reporte() {}

    public Reporte(int idReporte, String tipo, LocalDate fechaGeneracion) {
        this.idReporte = idReporte;
        this.tipo = tipo;
        this.fechaGeneracion = fechaGeneracion;
    }

    // getters y setters...
}