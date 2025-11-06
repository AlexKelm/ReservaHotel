package com.segovia.reservahotel.controllers;

import com.segovia.reservahotel.dao.ClienteDAO;
import com.segovia.reservahotel.models.Cliente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientesController {

    @FXML
    private TableView<Cliente> tablaClientes;
    @FXML
    private TableColumn<Cliente, Integer> colId;
    @FXML
    private TableColumn<Cliente, String> colNombre;
    @FXML
    private TableColumn<Cliente, String> colApellido;
    @FXML
    private TableColumn<Cliente, String> colDni;
    @FXML
    private TableColumn<Cliente, String> colTelefono;
    @FXML
    private TableColumn<Cliente, String> colEmail;
    @FXML
    private TableColumn<Cliente, String> colTipo;

    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtApellido;
    @FXML
    private TextField txtDni;
    @FXML
    private TextField txtTelefono;
    @FXML
    private TextField txtEmail;
    @FXML
    private ComboBox<String> cbTipo;

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private ObservableList<Cliente> listaClientes;
    private Cliente clienteSeleccionado = null;

    @FXML
    public void initialize() {
        cbTipo.setItems(FXCollections.observableArrayList("Habitual", "Ocasional"));

        colId.setCellValueFactory(new PropertyValueFactory<>("idCliente"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        cargarTabla();

        //listener para ver que cliente selecciono el usuario
        tablaClientes.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        clienteSeleccionado = newSelection;
                        cargarDatosFormulario(clienteSeleccionado);
                    }
                });
    }

    private String validarCampos() {
        StringBuilder errores = new StringBuilder();

        if (txtNombre.getText().trim().isEmpty() || !txtNombre.getText().matches("[a-zA-Z\\s]{3,50}")) {
            errores.append("El nombre es inválido (solo letras y espacios, 3-50 caracteres).\n");
        }
        if (txtApellido.getText().trim().isEmpty() || !txtApellido.getText().matches("[a-zA-Z\\s]{3,50}")) {
            errores.append("El apellido es inválido (solo letras y espacios, 3-50 caracteres).\n");
        }
        if (txtDni.getText().trim().isEmpty() || !txtDni.getText().matches("\\d{8}")) {
            errores.append("El DNI debe contener exactamente 8 dígitos numéricos.\n");
        }
        if (txtTelefono.getText().trim().isEmpty() || !txtTelefono.getText().matches("\\d{7,15}")) {
            errores.append("El teléfono debe contener entre 7 y 15 dígitos numéricos.\n");
        }
        if (!txtEmail.getText().trim().isEmpty() && !txtEmail.getText().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            errores.append("El formato del email es inválido.\n");
        }
        if (cbTipo.getValue() == null) {
            errores.append("Debe seleccionar un tipo de cliente.\n");
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
        if (clienteSeleccionado == null) {
            Cliente nuevo = new Cliente();
            actualizarDatosClienteDesdeFormulario(nuevo);
            exito = clienteDAO.insertar(nuevo);
        } else {
            actualizarDatosClienteDesdeFormulario(clienteSeleccionado);
            exito = clienteDAO.actualizar(clienteSeleccionado);
        }

        if (exito) {
            cargarTabla();
            onLimpiar();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Guardado", "No se pudo guardar el cliente. Verifique que el DNI no esté repetido.");
        }
    }

    @FXML
    private void onEliminar() {
        if (clienteSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Ningún Cliente Seleccionado", "Por favor, seleccione un cliente de la tabla para eliminar.");
            return;
        }

        Optional<ButtonType> result = mostrarAlertaConfirmacion("Eliminar Cliente",
                "¿Está seguro de que desea eliminar a " + clienteSeleccionado.getNombreCompleto() + "?\n\nEsta acción no se puede deshacer.");

        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (clienteDAO.eliminar(clienteSeleccionado.getIdCliente())) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Cliente Eliminado", "El cliente ha sido eliminado con éxito.");
                cargarTabla();
                onLimpiar();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error al Eliminar", "No se pudo eliminar el cliente. Verifique que no tenga reservas asociadas.");
            }
        }
    }

    @FXML
    private void onLimpiar() {
        clienteSeleccionado = null;
        txtNombre.clear();
        txtApellido.clear();
        txtDni.clear();
        txtTelefono.clear();
        txtEmail.clear();
        cbTipo.setValue(null);
        tablaClientes.getSelectionModel().clearSelection();
    }
    
    private void cargarTabla() {
        listaClientes = FXCollections.observableArrayList(clienteDAO.listar());
        tablaClientes.setItems(listaClientes);
    }

    private void cargarDatosFormulario(Cliente cliente) {
        txtNombre.setText(cliente.getNombre());
        txtApellido.setText(cliente.getApellido());
        txtDni.setText(cliente.getDni());
        txtTelefono.setText(cliente.getTelefono());
        txtEmail.setText(cliente.getEmail());
        cbTipo.setValue(cliente.getTipo());
    }

    private void actualizarDatosClienteDesdeFormulario(Cliente cliente) {
        cliente.setNombre(txtNombre.getText().trim());
        cliente.setApellido(txtApellido.getText().trim());
        cliente.setDni(txtDni.getText().trim());
        cliente.setTelefono(txtTelefono.getText().trim());
        cliente.setEmail(txtEmail.getText().trim());
        cliente.setTipo(cbTipo.getValue());
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
