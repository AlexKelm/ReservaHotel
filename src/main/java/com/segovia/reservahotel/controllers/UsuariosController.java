package com.segovia.reservahotel.controllers;

import com.segovia.reservahotel.dao.UsuarioDAO;
import com.segovia.reservahotel.models.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class UsuariosController {

    @FXML
    private TableView<Usuario> tablaUsuarios;
    @FXML
    private TableColumn<Usuario, Integer> colId;
    @FXML
    private TableColumn<Usuario, String> colUsername;
    @FXML
    private TableColumn<Usuario, String> colRol;

    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private ComboBox<String> cbRol;
    @FXML
    private Label lblMensaje;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();

    // para saber si estamos editando
    private Usuario usuarioSeleccionado = null;

    @FXML
    public void initialize() {
        // columnas
        colId.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getIdUsuario()).asObject());
        colUsername.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUsername()));
        colRol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRol()));

        // roles
        cbRol.setItems(FXCollections.observableArrayList("Administrador", "Recepcionista"));

        // cargar datos
        cargarUsuarios();

        // listener de selección
        tablaUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                usuarioSeleccionado = newSel;
                txtUsername.setText(newSel.getUsername());
                // contraseña NO la mostramos
                cbRol.setValue(newSel.getRol());
                lblMensaje.setText("");
            }
        });
    }

    private void cargarUsuarios() {
        listaUsuarios.clear();
        listaUsuarios.addAll(usuarioDAO.listarTodos());
        tablaUsuarios.setItems(listaUsuarios);
    }

    @FXML
    private void onNuevo() {
        usuarioSeleccionado = null;
        txtUsername.clear();
        txtPassword.clear();
        cbRol.getSelectionModel().clearSelection();
        lblMensaje.setText("");
        tablaUsuarios.getSelectionModel().clearSelection();
    }

    @FXML
    private void onGuardar() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        String rol = cbRol.getValue();

        if (username == null || username.isBlank()
                || rol == null || rol.isBlank()) {
            lblMensaje.setText("Complete usuario y rol.");
            return;
        }

        // si es edición
        if (usuarioSeleccionado != null) {
            // si dejó la password vacía, no la cambiamos
            boolean ok = usuarioDAO.actualizarUsuario(
                    usuarioSeleccionado.getIdUsuario(),
                    username,
                    (password == null || password.isBlank()) ? null : password,
                    rol
            );
            if (ok) {
                lblMensaje.setText("Usuario actualizado.");
                cargarUsuarios();
                onNuevo();
            } else {
                lblMensaje.setText("Error al actualizar.");
            }
        } else {
            // inserción
            if (password == null || password.isBlank()) {
                lblMensaje.setText("Ingrese contraseña para nuevo usuario.");
                return;
            }
            boolean ok = usuarioDAO.insertarUsuario(username, password, rol);
            if (ok) {
                lblMensaje.setText("Usuario guardado.");
                cargarUsuarios();
                onNuevo();
            } else {
                lblMensaje.setText("Error al guardar.");
            }
        }
    }

    @FXML
    private void onEliminar() {
        Usuario u = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (u == null) {
            lblMensaje.setText("Seleccione un usuario.");
            return;
        }
        boolean ok = usuarioDAO.eliminarUsuario(u.getIdUsuario());
        if (ok) {
            lblMensaje.setText("Usuario eliminado.");
            cargarUsuarios();
            onNuevo();
        } else {
            lblMensaje.setText("No se pudo eliminar.");
        }
    }
}