package com.segovia.reservahotel.controllers;

import com.segovia.reservahotel.dao.ClienteDAO;
import com.segovia.reservahotel.models.Cliente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ClientesController {

    @FXML
    private TableView<Cliente> tablaClientes;
    @FXML
    private TableColumn<Cliente, String> colNombre;
    @FXML
    private TableColumn<Cliente, String> colApellido;
    @FXML
    private TableColumn<Cliente, String> colDni;

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

    @FXML
    public void initialize() {
        cbTipo.setItems(FXCollections.observableArrayList("Habitual", "Ocasional"));

        colNombre.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNombre()));
        colApellido.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getApellido()));
        colDni.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDni()));

        cargarTabla();
    }

    private void cargarTabla() {
        listaClientes = FXCollections.observableArrayList(clienteDAO.listar());
        tablaClientes.setItems(listaClientes);
    }

    @FXML
    private void onGuardar() {
        Cliente c = new Cliente();
        c.setNombre(txtNombre.getText());
        c.setApellido(txtApellido.getText());
        c.setDni(txtDni.getText());
        c.setTelefono(txtTelefono.getText());
        c.setEmail(txtEmail.getText());
        c.setTipo(cbTipo.getValue() == null ? "Ocasional" : cbTipo.getValue());

        if (clienteDAO.insertar(c)) {
            cargarTabla();
            limpiar();
        }
    }

    private void limpiar() {
        txtNombre.clear();
        txtApellido.clear();
        txtDni.clear();
        txtTelefono.clear();
        txtEmail.clear();
        cbTipo.setValue(null);
    }
}