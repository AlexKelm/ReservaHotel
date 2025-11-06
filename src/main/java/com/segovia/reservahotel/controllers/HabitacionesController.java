package com.segovia.reservahotel.controllers;

import com.segovia.reservahotel.dao.HabitacionDAO;
import com.segovia.reservahotel.models.Habitacion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Optional;

public class HabitacionesController {

    @FXML
    private TableView<Habitacion> tablaHabitaciones;
    @FXML
    private TableColumn<Habitacion, Integer> colId;
    @FXML
    private TableColumn<Habitacion, Integer> colCapacidad;
    @FXML
    private TableColumn<Habitacion, String> colCama;
    @FXML
    private TableColumn<Habitacion, String> colCaract;
    @FXML
    private TableColumn<Habitacion, Double> colPrecio;
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
    private Habitacion habitacionSeleccionada = null;

    @FXML
    public void initialize() {
        cbTipoCama.setItems(FXCollections.observableArrayList("Single", "Doble", "Queen", "King"));
        cbCaracteristica.setItems(FXCollections.observableArrayList("Estandar", "Lujo"));

        colId.setCellValueFactory(new PropertyValueFactory<>("idHabitacion"));
        colCapacidad.setCellValueFactory(new PropertyValueFactory<>("capacidad"));
        colCama.setCellValueFactory(new PropertyValueFactory<>("tipoDeCama"));
        colCaract.setCellValueFactory(new PropertyValueFactory<>("caracteristicas"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        cargarTabla();

        tablaHabitaciones.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        habitacionSeleccionada = newSelection;
                        cargarDatosFormulario(habitacionSeleccionada);
                    }
                });
    }

    private String validarCampos() {
        StringBuilder errores = new StringBuilder();
        try {
            int capacidad = Integer.parseInt(txtCapacidad.getText());
            if (capacidad <= 0) {
                errores.append("La capacidad debe ser un número positivo.\n");
            }
        } catch (NumberFormatException e) {
            errores.append("La capacidad debe ser un número válido.\n");
        }

        try {
            double precio = Double.parseDouble(txtPrecio.getText());
            if (precio <= 0) {
                errores.append("El precio debe ser un número positivo.\n");
            }
        } catch (NumberFormatException e) {
            errores.append("El precio debe ser un número válido.\n");
        }

        if (cbTipoCama.getValue() == null) {
            errores.append("Debe seleccionar un tipo de cama.\n");
        }
        if (cbCaracteristica.getValue() == null) {
            errores.append("Debe seleccionar una característica.\n");
        }

        return errores.toString();
    }

    @FXML
    private void onGuardar() {
        String errores = validarCampos();
        if (!errores.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Datos Inválidos", errores);
            return;
        }

        boolean exito;
        if (habitacionSeleccionada == null) {
            Habitacion nueva = new Habitacion();
            actualizarDatosHabitacionDesdeFormulario(nueva);
            nueva.setEstado("Libre"); // Nueva habitación siempre está libre
            exito = habitacionDAO.insertar(nueva);
        } else {
            actualizarDatosHabitacionDesdeFormulario(habitacionSeleccionada);
            exito = habitacionDAO.actualizar(habitacionSeleccionada);
        }

        if (exito) {
            cargarTabla();
            onLimpiar();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Guardado", "No se pudo guardar la habitación.");
        }
    }

    @FXML
    private void onEliminar() {
        if (habitacionSeleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Ninguna Habitación Seleccionada", "Por favor, seleccione una habitación de la tabla para eliminar.");
            return;
        }

        Optional<ButtonType> result = mostrarAlertaConfirmacion("Eliminar Habitación",
                "¿Está seguro de que desea eliminar la habitación #" + habitacionSeleccionada.getIdHabitacion() + "?\n\nEsta acción no se puede deshacer.");

        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (habitacionDAO.eliminar(habitacionSeleccionada.getIdHabitacion())) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Habitación Eliminada", "La habitación ha sido eliminada con éxito.");
                cargarTabla();
                onLimpiar();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error al Eliminar", "No se pudo eliminar la habitación. Verifique que no esté asociada a una reserva.");
            }
        }
    }

    @FXML
    private void onLimpiar() {
        habitacionSeleccionada = null;
        txtCapacidad.clear();
        txtPrecio.clear();
        cbTipoCama.setValue(null);
        cbCaracteristica.setValue(null);
        tablaHabitaciones.getSelectionModel().clearSelection();
    }
    
    private void cargarTabla() {
        habitacionDAO.actualizarEstadosHabitacion();
        lista = FXCollections.observableArrayList(habitacionDAO.listar());
        tablaHabitaciones.setItems(lista);
    }

    private void cargarDatosFormulario(Habitacion habitacion) {
        txtCapacidad.setText(String.valueOf(habitacion.getCapacidad()));
        txtPrecio.setText(String.format("%.2f", habitacion.getPrecio()));
        cbTipoCama.setValue(habitacion.getTipoDeCama());
        cbCaracteristica.setValue(habitacion.getCaracteristicas());
    }

    private void actualizarDatosHabitacionDesdeFormulario(Habitacion habitacion) {
        habitacion.setCapacidad(Integer.parseInt(txtCapacidad.getText()));
        habitacion.setPrecio(Double.parseDouble(txtPrecio.getText()));
        habitacion.setTipoDeCama(cbTipoCama.getValue());
        habitacion.setCaracteristicas(cbCaracteristica.getValue());
    }

    private void mostrarAlerta(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText("Por favor, corrija los siguientes errores:");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Optional<ButtonType> mostrarAlertaConfirmacion(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }
}
