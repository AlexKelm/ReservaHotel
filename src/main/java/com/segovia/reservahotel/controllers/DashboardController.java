package com.segovia.reservahotel.controllers;

import com.segovia.reservahotel.models.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    @FXML
    private StackPane mainContent;

    @FXML
    private Label lblUsuarioActivo;

    // lo seteamos por defecto, pero lo vamos a pisar desde el login
    public void initialize() {
        lblUsuarioActivo.setText("Usuario: admin");
    }

    // ====== MÉTODO QUE TE FALTABA ======
    public void setUsuarioLogueado(Usuario usuario) {
        if (usuario != null) {
            lblUsuarioActivo.setText("Usuario: " + usuario.getUsername());
        } else {
            lblUsuarioActivo.setText("Usuario: desconocido");
        }
    }
    // ===================================

    @FXML
    private void mostrarInicio() {
        cambiarVista("inicio.fxml");
    }

    @FXML
    private void mostrarClientes() {
        cambiarVista("clientes-view.fxml"); // nombre real de tu recurso
    }

    @FXML
    private void mostrarHabitaciones() {
        cambiarVista("habitaciones-view.fxml");
    }

    @FXML
    private void mostrarReservas() {
        cambiarVista("reservas-view.fxml");
    }

    @FXML
    private void mostrarUsuarios() {
        // cuando agreguemos usuarios-view.fxml, lo llamamos acá
        cambiarVista("usuarios-view.fxml");
    }

    @FXML
    private void cerrarSesion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cerrar sesión");
        alert.setHeaderText("¿Desea cerrar la sesión actual?");
        alert.setContentText("Perderá los cambios no guardados.");

        // versión segura: si no elige nada, toma CANCEL
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                // 1) cerrar la ventana actual
                Stage stageActual = (Stage) mainContent.getScene().getWindow();
                stageActual.close();

                // 2) volver a mostrar el login
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/segovia/reservahotel/login-view.fxml"));
                Scene scene = new Scene(loader.load());

                Stage nuevoStage = new Stage();
                nuevoStage.setTitle("Inicio de sesión - Sistema de Reservas");
                nuevoStage.setScene(scene);
                nuevoStage.show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void cambiarVista(String nombreVista) {
        try {
            // OJO: en tu zip los fxml están en /com/segovia/reservahotel/ directamente
            Node vista = FXMLLoader.load(getClass().getResource("/com/segovia/reservahotel/" + nombreVista));
            mainContent.getChildren().setAll(vista);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}