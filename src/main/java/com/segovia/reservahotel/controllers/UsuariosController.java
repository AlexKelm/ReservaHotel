package com.segovia.reservahotel.controllers;

import com.segovia.reservahotel.dao.UsuarioDAO;
import com.segovia.reservahotel.models.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Optional;

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

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();

    private Usuario usuarioSeleccionado = null;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));

        cbRol.setItems(FXCollections.observableArrayList("Administrador", "Recepcionista"));

        cargarUsuarios();

        tablaUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                usuarioSeleccionado = newSel;
                txtUsername.setText(newSel.getUsername());
                txtPassword.clear(); // No mostramos la contraseña
                cbRol.setValue(newSel.getRol());
            }
        });
    }

    private String validarCampos() {
        StringBuilder errores = new StringBuilder();

        if (txtUsername.getText().trim().isEmpty() || !txtUsername.getText().matches("^[a-zA-Z0-9]{3,20}$")) {
            errores.append("El nombre de usuario es inválido (solo letras y números, 3-20 caracteres).\n");
        }

        // Validar contraseña solo si es un nuevo usuario o si se está intentando cambiar
        if (usuarioSeleccionado == null || !txtPassword.getText().isEmpty()) {
            if (txtPassword.getText().length() < 6) {
                errores.append("La contraseña debe tener al menos 6 caracteres.\n");
            }
        }

        if (cbRol.getValue() == null) {
            errores.append("Debe seleccionar un rol para el usuario.\n");
        }

        return errores.toString();
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
        tablaUsuarios.getSelectionModel().clearSelection();
    }

    @FXML
    private void onGuardar() {
        String errores = validarCampos();
        if (!errores.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Datos Inválidos", errores);
            return;
        }

        boolean exito;
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText(); // Puede estar vacío si no se cambia en edición
        String rol = cbRol.getValue();

        if (usuarioSeleccionado != null) {
            // Edición
            exito = usuarioDAO.actualizarUsuario(
                    usuarioSeleccionado.getIdUsuario(),
                    username,
                    password.isEmpty() ? null : password, // Si la contraseña está vacía, no la actualizamos
                    rol
            );
        } else {
            // Inserción
            exito = usuarioDAO.insertarUsuario(username, password, rol);
        }

        if (exito) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Operación Exitosa", "Usuario guardado con éxito.");
            cargarUsuarios();
            onNuevo();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Guardado", "No se pudo guardar el usuario. Verifique que el nombre de usuario no esté repetido.");
        }
    }

    @FXML
    private void onEliminar() {
        Usuario u = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (u == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Ningún Usuario Seleccionado", "Por favor, seleccione un usuario de la tabla para eliminar.");
            return;
        }

        Optional<ButtonType> result = mostrarAlertaConfirmacion("Eliminar Usuario",
                "¿Está seguro de que desea eliminar a " + u.getUsername() + "?\n\nEsta acción no se puede deshacer.");

        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (usuarioDAO.eliminarUsuario(u.getIdUsuario())) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Usuario Eliminado", "El usuario ha sido eliminado con éxito.");
                cargarUsuarios();
                onNuevo();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error al Eliminar", "No se pudo eliminar el usuario. Verifique que no tenga reservas asociadas.");
            }
        }
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
