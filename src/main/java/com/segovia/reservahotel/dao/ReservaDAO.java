package com.segovia.reservahotel.dao;

import com.segovia.reservahotel.models.Reserva;
import com.segovia.reservahotel.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO {

    public int insertar(Reserva r) {
        String sql = "INSERT INTO reserva (idUsuario, idCliente, fechaInicio, fechaFin, estado, abono) VALUES (?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, r.getIdUsuario());
            ps.setInt(2, r.getIdCliente());
            ps.setDate(3, Date.valueOf(r.getFechaInicio()));
            ps.setDate(4, Date.valueOf(r.getFechaFin()));
            ps.setString(5, r.getEstado());
            ps.setDouble(6, r.getAbono());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1); // id generado
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // error
    }

    public boolean actualizarEstado(int idReserva, String nuevoEstado) {
        String sql = "UPDATE reserva SET estado=? WHERE idReserva=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado);
            ps.setInt(2, idReserva);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Ingresar un abono y confirmar
    public boolean abonarReserva(int idReserva, double monto) {
        String sql = "UPDATE reserva SET abono = abono + ?, estado='Confirmada' WHERE idReserva=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, monto);
            ps.setInt(2, idReserva);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Anular por nombre o apellido (JOIN)
    public boolean anularPorNombreApellido(String texto) {
        String sql = "UPDATE reserva r " +
                "JOIN clientes c ON r.idCliente = c.idCliente " +
                "SET r.estado='Anulada' " +
                "WHERE (c.nombre LIKE ? OR c.apellido LIKE ?) AND r.estado='Pendiente'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + texto + "%");
            ps.setString(2, "%" + texto + "%");
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Reserva> listar() {
        List<Reserva> lista = new ArrayList<>();
        String sql = "SELECT * FROM reserva";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Reserva r = new Reserva(
                        rs.getInt("idReserva"),
                        rs.getInt("idUsuario"),
                        rs.getInt("idCliente"),
                        rs.getDate("fechaInicio").toLocalDate(),
                        rs.getDate("fechaFin").toLocalDate(),
                        rs.getString("estado"),
                        rs.getDouble("abono")
                );
                lista.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}