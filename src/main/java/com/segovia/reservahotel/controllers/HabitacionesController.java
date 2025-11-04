package com.segovia.reservahotel.controllers;

import com.segovia.reservahotel.dao.HabitacionDAO;
import com.segovia.reservahotel.models.Habitacion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class HabitacionesController {

    @FXML
    private TableView<Habitacion> tablaHabitaciones;
    @FXML
    private TableColumn<Habitacion, Number> colId;
    @FXML
    private TableColumn<Habitacion, Number> colCapacidad;
    @FXML
    private TableColumn<Habitacion, String> colCama;
    @FXML
    private TableColumn<Habitacion, String> colCaract;
    @FXML
    private TableColumn<Habitacion, Number> colPrecio;
    @FXML
    private TableColumn<Habitacion, String> colEstado;

    @FXML
    private TextField txtCapacidad;
    @FXML
    private ComboBox<String> cbTipoCama;
    @FXML
    private ComboBox<String> cbCaracteristica;
    @FXML
    private TextField txtPrecio;

    private final HabitacionDAO habitacionDAO = new HabitacionDAO();
    private ObservableList<Habitacion> lista;

    @FXML
    public void initialize() {
        cbTipoCama.setItems(FXCollections.observableArrayList("Single", "Doble", "Queen", "King"));
        cbCaracteristica.setItems(FXCollections.observableArrayList("Estandar", "Lujo"));

        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getIdHabitacion()));
        colCapacidad.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getCapacidad()));
        colCama.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTipoDeCama()));
        colCaract.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCaracteristicas()));
        colPrecio.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getPrecio()));
        colEstado.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEstado()));

        cargarTabla();
    }

    private void cargarTabla() {
        // Actualizamos estados antes de cargar, para tener la info más reciente
        habitacionDAO.actualizarEstadosHabitacion();
        lista = FXCollections.observableArrayList(habitacionDAO.listar());
        tablaHabitaciones.setItems(lista);
    }

    @FXML
    private void onGuardar() {
        Habitacion h = new Habitacion();
        h.setCapacidad(Integer.parseInt(txtCapacidad.getText()));
        h.setTipoDeCama(cbTipoCama.getValue());
        h.setCaracteristicas(cbCaracteristica.getValue());
        h.setPrecio(Double.parseDouble(txtPrecio.getText()));
        h.setEstado("Libre"); // Nueva habitación siempre está libre

        if (habitacionDAO.insertar(h)) {
            cargarTabla();
            limpiar();
        }
    }

    @FXML
    private void onEditar() {
        Habitacion sel = tablaHabitaciones.getSelectionModel().getSelectedItem();
        if (sel == null) return;

        sel.setCapacidad(Integer.parseInt(txtCapacidad.getText()));
        sel.setTipoDeCama(cbTipoCama.getValue());
        sel.setCaracteristicas(cbCaracteristica.getValue());
        sel.setPrecio(Double.parseDouble(txtPrecio.getText()));
        // El estado ya no se edita manualmente

        if (habitacionDAO.actualizar(sel)) {
            cargarTabla();
            limpiar();
        }
    }

    @FXML
    private void onEliminar() {
        Habitacion sel = tablaHabitaciones.getSelectionModel().getSelectedItem();
        if (sel == null) return;

        if (habitacionDAO.eliminar(sel.getIdHabitacion())) {
            cargarTabla();
        }
    }

    @FXML
    private void onSeleccionar() {
        Habitacion sel = tablaHabitaciones.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        txtCapacidad.setText(String.valueOf(sel.getCapacidad()));
        cbTipoCama.setValue(sel.getTipoDeCama());
        cbCaracteristica.setValue(sel.getCaracteristicas());
        txtPrecio.setText(String.valueOf(sel.getPrecio()));
        // El ComboBox de estado ya no existe
    }

    private void limpiar() {
        txtCapacidad.clear();
        cbTipoCama.setValue(null);
        cbCaracteristica.setValue(null);
        txtPrecio.clear();
    }
}