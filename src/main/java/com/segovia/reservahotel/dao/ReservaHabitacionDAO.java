package com.segovia.reservahotel.dao;

import com.segovia.reservahotel.models.ReservaHabitacion;
import com.segovia.reservahotel.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ReservaHabitacionDAO {

    public boolean insertar(ReservaHabitacion rh) {
        String sql = "INSERT INTO reserva_habitacion (idReserva, idHabitacion) VALUES (?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, rh.getIdReserva());
            ps.setInt(2, rh.getIdHabitacion());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}