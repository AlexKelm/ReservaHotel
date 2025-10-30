package com.segovia.reservahotel.controllers;

import com.segovia.reservahotel.dao.*;
import com.segovia.reservahotel.models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

public class ReservasController {

    @FXML
    private ComboBox<Cliente> cbCliente;
    @FXML
    private DatePicker dpInicio;
    @FXML
    private DatePicker dpFin;
    @FXML
    private TextField txtAbono;
    @FXML
    private ComboBox<Habitacion> cbHabitacion;
    @FXML
    private ListView<Habitacion> listHabitacionesSeleccionadas;

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final HabitacionDAO habitacionDAO = new HabitacionDAO();
    private final ReservaDAO reservaDAO = new ReservaDAO();
    private final ReservaHabitacionDAO reservaHabitacionDAO = new ReservaHabitacionDAO();

    // suponemos usuario logueado id=1
    private int idUsuarioLogueado = 1;

    @FXML
    public void initialize() {
        cbCliente.setItems(FXCollections.observableArrayList(clienteDAO.listar()));
        cbHabitacion.setItems(FXCollections.observableArrayList(habitacionDAO.listarLibres()));
    }

    @FXML
    private void onAgregarHabitacion() {
        Habitacion h = cbHabitacion.getValue();
        if (h != null && !listHabitacionesSeleccionadas.getItems().contains(h)) {
            listHabitacionesSeleccionadas.getItems().add(h);
        }
    }

    @FXML
    private void onGuardarReserva() {
        Cliente c = cbCliente.getValue();
        LocalDate fi = dpInicio.getValue();
        LocalDate ff = dpFin.getValue();
        double abono = txtAbono.getText().isEmpty() ? 0 : Double.parseDouble(txtAbono.getText());

        if (c == null || fi == null || ff == null) {
            // mostrar alerta
            return;
        }

        // regla de negocio: si abono > 0 => confirmada
        String estado = abono > 0 ? "Confirmada" : "Pendiente";

        Reserva r = new Reserva();
        r.setIdUsuario(idUsuarioLogueado);
        r.setIdCliente(c.getIdCliente());
        r.setFechaInicio(fi);
        r.setFechaFin(ff);
        r.setAbono(abono);
        r.setEstado(estado);

        int idReservaGenerada = reservaDAO.insertar(r);
        if (idReservaGenerada > 0) {
            for (Habitacion h : listHabitacionesSeleccionadas.getItems()) {
                reservaHabitacionDAO.insertar(new ReservaHabitacion(idReservaGenerada, h.getIdHabitacion()));
                // opcional: cambiar estado de la habitación a "Reservada"
                habitacionDAO.cambiarEstado(h.getIdHabitacion(), "Reservada");
            }
            limpiar();
        }
    }

    private void limpiar() {
        cbCliente.setValue(null);
        dpInicio.setValue(null);
        dpFin.setValue(null);
        txtAbono.clear();
        listHabitacionesSeleccionadas.getItems().clear();
    }
}