package com.segovia.reservahotel.models;

public class ReservaHabitacion {
    private int idReserva;
    private int idHabitacion;

    public ReservaHabitacion() {}

    public ReservaHabitacion(int idReserva, int idHabitacion) {
        this.idReserva = idReserva;
        this.idHabitacion = idHabitacion;
    }

    public int getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
    }

    public int getIdHabitacion() {
        return idHabitacion;
    }

    public void setIdHabitacion(int idHabitacion) {
        this.idHabitacion = idHabitacion;
    }
}