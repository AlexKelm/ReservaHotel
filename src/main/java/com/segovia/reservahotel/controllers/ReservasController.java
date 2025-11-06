package com.segovia.reservahotel.controllers;

import com.segovia.reservahotel.dao.*;
import com.segovia.reservahotel.models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class ReservasController {

    @FXML
    private TableView<ReservaInfo> tablaReservas;
    @FXML
    private TableColumn<ReservaInfo, Integer> colIdReserva;
    @FXML
    private TableColumn<ReservaInfo, String> colCliente;
    @FXML
    private TableColumn<ReservaInfo, LocalDate> colFechaInicio;
    @FXML
    private TableColumn<ReservaInfo, LocalDate> colFechaFin;
    @FXML
    private TableColumn<ReservaInfo, String> colEstado;
    @FXML
    private TableColumn<ReservaInfo, Double> colAbono;
    @FXML
    private TableColumn<ReservaInfo, Double> colPrecioTotal;

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

    private ObservableList<ReservaInfo> listaReservas;
    private Usuario usuarioLogueado;

    public void setUsuarioLogueado(Usuario usuario) {
        this.usuarioLogueado = usuario;
    }

    @FXML
    public void initialize() {
        colIdReserva.setCellValueFactory(new PropertyValueFactory<>("idReserva"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("clienteNombreCompleto"));
        colFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        colFechaFin.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colAbono.setCellValueFactory(new PropertyValueFactory<>("abono"));
        colPrecioTotal.setCellValueFactory(new PropertyValueFactory<>("precioTotal"));

        cargarTablaReservas();
        cbCliente.setItems(FXCollections.observableArrayList(clienteDAO.listar()));

        dpInicio.valueProperty().addListener((obs, oldVal, newVal) -> actualizarHabitacionesDisponibles());
        dpFin.valueProperty().addListener((obs, oldVal, newVal) -> actualizarHabitacionesDisponibles());
    }

    private String validarCampos() {
        StringBuilder errores = new StringBuilder();

        if (cbCliente.getValue() == null) {
            errores.append("Debe seleccionar un cliente.\n");
        }
        if (dpInicio.getValue() == null) {
            errores.append("Debe seleccionar una fecha de inicio.\n");
        }
        if (dpFin.getValue() == null) {
            errores.append("Debe seleccionar una fecha de fin.\n");
        }
        if (dpInicio.getValue() != null && dpFin.getValue() != null && dpFin.getValue().isBefore(dpInicio.getValue())) {
            errores.append("La fecha de fin no puede ser anterior a la fecha de inicio.\n");
        }
        if (listHabitacionesSeleccionadas.getItems().isEmpty()) {
            errores.append("Debe añadir al menos una habitación a la reserva.\n");
        }

        try {
            double abono = txtAbono.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtAbono.getText());
            if (abono < 0) {
                errores.append("El abono no puede ser un número negativo.\n");
            }
        } catch (NumberFormatException e) {
            errores.append("El abono debe ser un número válido.\n");
        }

        return errores.toString();
    }

    @FXML
    private void onGuardarReserva() {
        if (usuarioLogueado == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error Crítico", "No se ha podido identificar al usuario.");
            return;
        }

        String errores = validarCampos();
        if (!errores.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Datos Inválidos", errores);
            return;
        }

        Cliente c = cbCliente.getValue();
        LocalDate fi = dpInicio.getValue();
        LocalDate ff = dpFin.getValue();
        double abono = txtAbono.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtAbono.getText());

        long noches = ChronoUnit.DAYS.between(fi, ff);
        if (noches <= 0) {
            mostrarAlerta(Alert.AlertType.WARNING, "Fechas Inválidas", "La reserva debe ser de al menos una noche.");
            return;
        }

        double precioTotal = 0;
        for (Habitacion h : listHabitacionesSeleccionadas.getItems()) {
            double precioBase = h.getPrecio();
            double recargo = "Lujo".equalsIgnoreCase(h.getCaracteristicas()) ? 1.25 : 1.0;
            precioTotal += (precioBase * noches * recargo);
        }

        String estado = (abono >= precioTotal) ? "Confirmada" : "Pendiente";

        Reserva r = new Reserva();
        r.setIdUsuario(usuarioLogueado.getIdUsuario());
        r.setIdCliente(c.getIdCliente());
        r.setFechaInicio(fi);
        r.setFechaFin(ff);
        r.setAbono(abono);
        r.setPrecioTotal(precioTotal);
        r.setEstado(estado);

        int idReservaGenerada = reservaDAO.insertar(r);
        if (idReservaGenerada > 0) {
            for (Habitacion h : listHabitacionesSeleccionadas.getItems()) {
                reservaHabitacionDAO.insertar(new ReservaHabitacion(idReservaGenerada, h.getIdHabitacion()));
            }
            mostrarAlerta(Alert.AlertType.INFORMATION, "Reserva Guardada", "La reserva ha sido guardada con éxito. Precio Total: " + String.format("$%.2f", precioTotal));
            cargarTablaReservas();
            onLimpiarFormulario();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo guardar la reserva.");
        }
    }
    
    private void actualizarHabitacionesDisponibles() {
        LocalDate inicio = dpInicio.getValue();
        LocalDate fin = dpFin.getValue();

        if (inicio != null && fin != null && !fin.isBefore(inicio)) {
            listHabitacionesSeleccionadas.getItems().clear();
            cbHabitacion.setItems(FXCollections.observableArrayList(habitacionDAO.listarHabitacionesDisponibles(inicio, fin)));
        } else {
            cbHabitacion.getItems().clear();
        }
    }

    private void cargarTablaReservas() {
        habitacionDAO.actualizarEstadosHabitacion();
        listaReservas = FXCollections.observableArrayList(reservaDAO.listarReservasInfo());
        tablaReservas.setItems(listaReservas);
    }

    @FXML
    private void onAgregarHabitacion() {
        Habitacion h = cbHabitacion.getValue();
        if (h != null && !listHabitacionesSeleccionadas.getItems().contains(h)) {
            listHabitacionesSeleccionadas.getItems().add(h);
        }
    }

    @FXML
    private void onBorrarReserva() {
        ReservaInfo selectedReserva = tablaReservas.getSelectionModel().getSelectedItem();
        if (selectedReserva == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Ninguna Reserva Seleccionada", "Por favor, seleccione una reserva de la tabla para borrar.");
            return;
        }

        Optional<ButtonType> result = mostrarAlertaConfirmacion("Borrar Reserva",
                "¿Está seguro de que desea borrar permanentemente la reserva #" + selectedReserva.getIdReserva() + "?\n\nEsta acción no se puede deshacer.");

        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (reservaDAO.borrar(selectedReserva.getIdReserva())) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Reserva Borrada", "La reserva ha sido eliminada con éxito.");
                cargarTablaReservas();
                habitacionDAO.actualizarEstadosHabitacion();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo borrar la reserva.");
            }
        }
    }

    @FXML
    private void onConfirmarPago() {
        ReservaInfo selectedReserva = tablaReservas.getSelectionModel().getSelectedItem();
        if (selectedReserva == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Ninguna Reserva Seleccionada", "Por favor, seleccione una reserva de la tabla.");
            return;
        }

        if ("Confirmada".equalsIgnoreCase(selectedReserva.getEstado())) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Reserva Ya Confirmada", "Esta reserva ya está confirmada.");
            return;
        }

        Optional<ButtonType> result = mostrarAlertaConfirmacion("Confirmar Pago", "¿Está seguro de que desea confirmar el pago de esta reserva?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (reservaDAO.confirmarPago(selectedReserva.getIdReserva())) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Pago Confirmado", "El pago de la reserva ha sido confirmado.");
                cargarTablaReservas();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo confirmar el pago de la reserva.");
            }
        }
    }

    @FXML
    private void onAnularReserva() {
        ReservaInfo selectedReserva = tablaReservas.getSelectionModel().getSelectedItem();
        if (selectedReserva == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Ninguna Reserva Seleccionada", "Por favor, seleccione una reserva de la tabla.");
            return;
        }

        if ("Anulada".equalsIgnoreCase(selectedReserva.getEstado())) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Reserva Ya Anulada", "Esta reserva ya está anulada.");
            return;
        }

        Optional<ButtonType> result = mostrarAlertaConfirmacion("Anular Reserva", "¿Está seguro de que desea anular esta reserva?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (reservaDAO.anularReserva(selectedReserva.getIdReserva())) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Reserva Anulada", "La reserva ha sido anulada.");
                cargarTablaReservas();
                habitacionDAO.actualizarEstadosHabitacion();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo anular la reserva.");
            }
        }
    }

    @FXML
    private void onLimpiarFormulario() {
        cbCliente.setValue(null);
        dpInicio.setValue(null);
        dpFin.setValue(null);
        txtAbono.clear();
        listHabitacionesSeleccionadas.getItems().clear();
        tablaReservas.getSelectionModel().clearSelection();
        cbHabitacion.getItems().clear();
    }

    private void mostrarAlerta(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        // Condicionalmente establecer el headerText
        if (type == Alert.AlertType.ERROR || type == Alert.AlertType.WARNING) {
            alert.setHeaderText("Por favor, corrija los siguientes errores:");
        } else {
            alert.setHeaderText(null); // No mostrar encabezado para información o éxito
        }
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
