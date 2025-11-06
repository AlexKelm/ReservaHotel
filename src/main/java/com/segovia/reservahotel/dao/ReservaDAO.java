package com.segovia.reservahotel.dao;

import com.segovia.reservahotel.models.Reserva;
import com.segovia.reservahotel.models.ReservaInfo;
import com.segovia.reservahotel.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO {

    public int contarActivas() {
        String sql = "SELECT COUNT(*) FROM reserva WHERE estado = 'Confirmada' OR estado = 'Pendiente'";
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

    public int insertar(Reserva r) {
        String sql = "INSERT INTO reserva (idUsuario, idCliente, fechaInicio, fechaFin, estado, abono, precioTotal) VALUES (?,?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, r.getIdUsuario());
            ps.setInt(2, r.getIdCliente());
            ps.setDate(3, Date.valueOf(r.getFechaInicio()));
            ps.setDate(4, Date.valueOf(r.getFechaFin()));
            ps.setString(5, r.getEstado());
            ps.setDouble(6, r.getAbono());
            ps.setDouble(7, r.getPrecioTotal());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean borrar(int idReserva) {
        String sqlDeleteHabitaciones = "DELETE FROM reserva_habitacion WHERE idReserva = ?";
        String sqlDeleteReserva = "DELETE FROM reserva WHERE idReserva = ?";
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            if (con == null) return false;

            con.setAutoCommit(false);

            try (PreparedStatement psHab = con.prepareStatement(sqlDeleteHabitaciones)) {
                psHab.setInt(1, idReserva);
                psHab.executeUpdate();
            }

            try (PreparedStatement psRes = con.prepareStatement(sqlDeleteReserva)) {
                psRes.setInt(1, idReserva);
                int filasAfectadas = psRes.executeUpdate();
                if (filasAfectadas > 0) {
                    con.commit();
                    return true;
                }
            }

            con.rollback();
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<ReservaInfo> listarReservasInfo() {
        List<ReservaInfo> lista = new ArrayList<>();
        String sql = "SELECT r.idReserva, CONCAT(c.nombre, ' ', c.apellido) AS cliente, " +
                     "r.fechaInicio, r.fechaFin, r.estado, r.abono, r.precioTotal " +
                     "FROM reserva r " +
                     "JOIN clientes c ON r.idCliente = c.idCliente " +
                     "ORDER BY r.fechaInicio DESC";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(new ReservaInfo(
                        rs.getInt("idReserva"),
                        rs.getString("cliente"),
                        rs.getDate("fechaInicio").toLocalDate(),
                        rs.getDate("fechaFin").toLocalDate(),
                        rs.getString("estado"),
                        rs.getDouble("abono"),
                        rs.getDouble("precioTotal")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean confirmarPago(int idReserva) {
        String sql = "UPDATE reserva SET estado = 'Confirmada' WHERE idReserva = ? AND estado = 'Pendiente'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean anularReserva(int idReserva) {
        String sql = "UPDATE reserva SET estado = 'Anulada' WHERE idReserva = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int anularReservasPendientesAntiguas(int diasAntiguedad) {
        String sql = "UPDATE reserva SET estado = 'Anulada' " +
                     "WHERE estado = 'Pendiente' AND fechaCreacion < NOW() - INTERVAL ? DAY";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, diasAntiguedad);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
