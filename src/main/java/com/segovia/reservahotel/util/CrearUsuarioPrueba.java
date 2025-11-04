package com.segovia.reservahotel.util;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CrearUsuarioPrueba {
    public static void main(String[] args) {
        String username = "admin";
        String password = "admin123";
        String rol = "Administrador"; // Rol: Administrador o Recepcionista
        String password_encriptada = BCrypt.hashpw(password, BCrypt.gensalt());
        String consulta = "INSERT INTO usuarios(username, password, rol) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(consulta);
            stmt.setString(1, username);
            stmt.setString(2, password_encriptada);
            stmt.setString(3, rol);
            int filas = stmt.executeUpdate();
            System.out.println("Usuario de prueba creado: " + (filas > 0));
        } catch (SQLException ex) {
            System.out.println("Error al crear el usuario de prueba:");
            ex.printStackTrace();
        }
    }
}
