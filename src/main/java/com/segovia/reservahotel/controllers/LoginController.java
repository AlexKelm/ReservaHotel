package com.segovia.reservahotel.controllers;

import com.segovia.reservahotel.dao.UsuarioDAO;
import com.segovia.reservahotel.models.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblMensaje;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    private void onLogin(ActionEvent event) {
        String user = txtUsuario.getText();
        String pass = txtPassword.getText();

        Usuario u = usuarioDAO.login(user, pass);
        if (u != null) {
            abrirDashboard(u);
            // cerrar ventana de login
            Stage stageActual = (Stage) txtUsuario.getScene().getWindow();
            stageActual.close();
        } else {
            lblMensaje.setText("Usuario o contraseña incorrectos");
        }
    }

    private void abrirDashboard(Usuario u) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/segovia/reservahotel/dashboard.fxml"));
            Scene scene = new Scene(loader.load());

            // obtener controller para pasarle el usuario
            DashboardController controller = loader.getController();
            controller.setUsuarioLogueado(u);

            Stage stage = new Stage();
            stage.setTitle("Panel principal - Sistema de Reservas");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onCancelar(ActionEvent event) {
        Stage stage = (Stage) txtUsuario.getScene().getWindow();
        stage.close();
    }
}