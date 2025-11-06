package com.segovia.reservahotel.controllers;

import com.segovia.reservahotel.dao.ClienteDAO;
import com.segovia.reservahotel.dao.HabitacionDAO;
import com.segovia.reservahotel.dao.ReservaDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class InicioController {

    @FXML
    private Label lblClientes;

    @FXML
    private Label lblHabitaciones;

    @FXML
    private Label lblReservas;

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final HabitacionDAO habitacionDAO = new HabitacionDAO();
    private final ReservaDAO reservaDAO = new ReservaDAO();

    @FXML
    public void initialize() {
        int totalClientes = clienteDAO.contar();
        int totalHabitaciones = habitacionDAO.contar();
        int totalReservas = reservaDAO.contarActivas();

        lblClientes.setText(String.valueOf(totalClientes));
        lblHabitaciones.setText(String.valueOf(totalHabitaciones));
        lblReservas.setText(String.valueOf(totalReservas));
    }
}
