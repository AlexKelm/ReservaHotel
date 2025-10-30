package com.segovia.reservahotel.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/sistema_reservas_hotel";
    private static final String USER = "root"; // cambialo si usás otro usuario
    private static final String PASSWORD = ""; // poné tu pass de MySQL

    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                System.out.println("Error cargando el driver de MySQL: " + e.getMessage());
            }
        }
        return connection;
    }
}