package com.segovia.reservahotel.dao;

import com.segovia.reservahotel.models.Habitacion;
import com.segovia.reservahotel.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HabitacionDAO {

    public int contar() {
        String sql = "SELECT COUNT(*) FROM habitacion";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

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
        String sql = "UPDATE habitacion SET capacidad=?, tipoDeCama=?, caracteristicas=?, precio=? WHERE idHabitacion=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, h.getCapacidad());
            ps.setString(2, h.getTipoDeCama());
            ps.setString(3, h.getCaracteristicas());
            ps.setDouble(4, h.getPrecio());
            ps.setInt(5, h.getIdHabitacion());
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

    public List<Habitacion> listarHabitacionesDisponibles(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Habitacion> lista = new ArrayList<>();
        String sql = "SELECT * FROM habitacion h WHERE h.idHabitacion NOT IN (" +
                     "  SELECT rh.idHabitacion FROM reserva_habitacion rh " +
                     "  JOIN reserva r ON rh.idReserva = r.idReserva " +
                     "  WHERE r.estado != 'Anulada' AND (" +
                     "      r.fechaInicio <= ? AND r.fechaFin >= ?" +
                     "  )" +
                     ")";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(fechaFin));
            ps.setDate(2, Date.valueOf(fechaInicio));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Habitacion(
                        rs.getInt("idHabitacion"),
                        rs.getInt("capacidad"),
                        rs.getString("tipoDeCama"),
                        rs.getString("caracteristicas"),
                        rs.getDouble("precio"),
                        rs.getString("estado")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void actualizarEstadosHabitacion() {
        String sqlSetLibres = "UPDATE habitacion SET estado = 'Libre'";
        String sqlSetOcupadas = "UPDATE habitacion h " +
                "JOIN reserva_habitacion rh ON h.idHabitacion = rh.idHabitacion " +
                "JOIN reserva r ON rh.idReserva = r.idReserva " +
                "SET h.estado = 'Ocupada' " +
                "WHERE CURDATE() BETWEEN r.fechaInicio AND r.fechaFin AND r.estado != 'Anulada'";

        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement()) {

            if (con == null) return;

            stmt.executeUpdate(sqlSetLibres);
            stmt.executeUpdate(sqlSetOcupadas);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
