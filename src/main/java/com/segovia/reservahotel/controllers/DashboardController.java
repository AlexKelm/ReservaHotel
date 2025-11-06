package com.segovia.reservahotel.controllers;

import com.segovia.reservahotel.dao.HabitacionDAO;
import com.segovia.reservahotel.dao.ReservaDAO;
import com.segovia.reservahotel.models.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    @FXML
    private StackPane mainContent;

    @FXML
    private Label lblUsuarioActivo;

    @FXML
    private Button btnUsuarios;

    private Usuario usuarioLogueado;
    private final HabitacionDAO habitacionDAO = new HabitacionDAO();
    private final ReservaDAO reservaDAO = new ReservaDAO();

    public void initialize() {

        int anuladas = reservaDAO.anularReservasPendientesAntiguas(2);
        if (anuladas > 0) {
            System.out.println("Se anularon automáticamente " + anuladas + " reservas pendientes antiguas.");
        }

        habitacionDAO.actualizarEstadosHabitacion();

        mostrarInicio();
    }

    public void setUsuarioLogueado(Usuario usuario) {
        this.usuarioLogueado = usuario;
        if (usuario != null) {
            lblUsuarioActivo.setText("Usuario: " + usuario.getUsername());
            configurarVisibilidadPorRol(usuario.getRol());
        } else {
            lblUsuarioActivo.setText("Usuario: desconocido");
            if (btnUsuarios != null) {
                btnUsuarios.setVisible(false);
                btnUsuarios.setManaged(false);
            }
        }
    }

    private void configurarVisibilidadPorRol(String rol) {
        boolean esAdmin = "Administrador".equalsIgnoreCase(rol);
        if (btnUsuarios != null) {
            btnUsuarios.setVisible(esAdmin);
            btnUsuarios.setManaged(esAdmin);
        }
    }

    @FXML
    private void mostrarInicio() {
        cambiarVista("/com/segovia/reservahotel/inicio.fxml");
    }

    @FXML
    private void mostrarClientes() {
        cambiarVista("/com/segovia/reservahotel/clientes-view.fxml");
    }

    @FXML
    private void mostrarHabitaciones() {
        cambiarVista("/com/segovia/reservahotel/habitaciones-view.fxml");
    }

    @FXML
    private void mostrarReservas() {
        cambiarVista("/com/segovia/reservahotel/reservas-view.fxml");
    }

    @FXML
    private void mostrarUsuarios() {
        cambiarVista("/com/segovia/reservahotel/usuarios-view.fxml");
    }

    @FXML
    private void cerrarSesion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cerrar sesión");
        alert.setHeaderText("¿Desea cerrar la sesión actual?");
        alert.setContentText("Perderá los cambios no guardados.");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                Stage stageActual = (Stage) mainContent.getScene().getWindow();
                stageActual.close();

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

    private void cambiarVista(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node vista = loader.load();

            // Si el controlador de la nueva vista necesita el usuario, se lo pasamos
            Object controller = loader.getController();
            if (controller instanceof ReservasController) {
                ((ReservasController) controller).setUsuarioLogueado(this.usuarioLogueado);
            }
            // Podrías añadir más `instanceof` para otros controladores si lo necesitas

            mainContent.getChildren().setAll(vista);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
