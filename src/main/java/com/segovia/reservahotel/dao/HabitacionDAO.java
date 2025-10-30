package com.segovia.reservahotel.dao;

import com.segovia.reservahotel.models.Habitacion;
import com.segovia.reservahotel.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HabitacionDAO {

    public boolean insertar(Habitacion h) {
        String sql = "INSERT INTO habitacion (capacidad, tipoDeCama, caracteristicas, precio, estado) VALUES (?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, h.getCapacidad());
            ps.setString(2, h.getTipoDeCama());
            ps.setString(3, h.getCaracteristicas());
            ps.setDouble(4, h.getPrecio());
            ps.setString(5, h.getEstado());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Habitacion> listar() {
        List<Habitacion> lista = new ArrayList<>();
        String sql = "SELECT * FROM habitacion";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Habitacion h = new Habitacion(
                        rs.getInt("idHabitacion"),
                        rs.getInt("capacidad"),
                        rs.getString("tipoDeCama"),
                        rs.getString("caracteristicas"),
                        rs.getDouble("precio"),
                        rs.getString("estado")
                );
                lista.add(h);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean actualizar(Habitacion h) {
        String sql = "UPDATE habitacion SET capacidad=?, tipoDeCama=?, caracteristicas=?, precio=?, estado=? WHERE idHabitacion=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, h.getCapacidad());
            ps.setString(2, h.getTipoDeCama());
            ps.setString(3, h.getCaracteristicas());
            ps.setDouble(4, h.getPrecio());
            ps.setString(5, h.getEstado());
            ps.setInt(6, h.getIdHabitacion());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean cambiarEstado(int idHabitacion, String nuevoEstado) {
        String sql = "UPDATE habitacion SET estado=? WHERE idHabitacion=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado);
            ps.setInt(2, idHabitacion);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean eliminar(int idHabitacion) {
        String sql = "DELETE FROM habitacion WHERE idHabitacion=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idHabitacion);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Disponibilidad por fechas (simplificada)
    public List<Habitacion> listarLibres() {
        List<Habitacion> lista = new ArrayList<>();
        String sql = "SELECT * FROM habitacion WHERE estado='Libre'";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Habitacion h = new Habitacion(
                        rs.getInt("idHabitacion"),
                        rs.getInt("capacidad"),
                        rs.getString("tipoDeCama"),
                        rs.getString("caracteristicas"),
                        rs.getDouble("precio"),
                        rs.getString("estado")
                );
                lista.add(h);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}