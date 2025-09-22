/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Controlador.Conection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author SELVYN
 */
public class Pago {

    // ==================== Verificar si el pago ya existe ====================
    private boolean existePago(Connection conn, int idCliente, int idTipoPago, Date fechaInicio, Date fechaFin, String concepto) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Pago WHERE id_cliente = ? AND id_tipo_pago = ? AND fecha_inicio = ? AND fecha_fin = ? AND concepto = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            stmt.setInt(2, idTipoPago);
            stmt.setDate(3, new java.sql.Date(fechaInicio.getTime()));
            stmt.setDate(4, new java.sql.Date(fechaFin.getTime()));
            stmt.setString(5, concepto);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // true si ya existe
                }
            }
        }
        return false;
    }

    // ==================== Agregar Pago ====================
    private int obtenerIdTipoPago(Connection conn, String nombreTipo) throws SQLException {
        String sql = "SELECT id_tipo_pago FROM TipoPago WHERE nombre = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombreTipo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_tipo_pago");
                }
            }
        }
        throw new SQLException("Tipo de pago no encontrado: " + nombreTipo);
    }

    public void agregarPago(int idCliente, String tipoPago,
            Date fechaInicio, Date fechaFin,
            double monto, String concepto, String estado) {

        String sql = "INSERT INTO Pago (id_cliente, id_tipo_pago, fecha_inicio, fecha_fin, monto, concepto, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conection.getConnection()) {
            int idTipoPago = obtenerIdTipoPago(conn, tipoPago);

            if (existePago(conn, idCliente, idTipoPago, fechaInicio, fechaFin, concepto)) {
                JOptionPane.showMessageDialog(null,
                        "El cliente ya tiene un pago registrado con estos datos.",
                        "Pago duplicado",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, idCliente);
                stmt.setInt(2, idTipoPago);
                stmt.setDate(3, new java.sql.Date(fechaInicio.getTime()));
                stmt.setDate(4, new java.sql.Date(fechaFin.getTime()));
                stmt.setDouble(5, monto);
                stmt.setString(6, concepto);
                stmt.setString(7, estado);

                stmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Pago registrado con éxito");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al registrar pago: " + e.getMessage());
        }
    }

    // ==================== Editar Pago ====================
    public void editarPago(int idPago, int idCliente, String tipoPago,
            Date fechaInicio, Date fechaFin,
            double monto, String concepto, String estado) {

        String sql = "UPDATE Pago SET id_cliente=?, id_tipo_pago=?, fecha_inicio=?, fecha_fin=?, "
                + "monto=?, concepto=?, estado=? WHERE id_pago=?";

        try (Connection conn = Conection.getConnection()) {
            int idTipoPago = obtenerIdTipoPago(conn, tipoPago);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, idCliente);
                stmt.setInt(2, idTipoPago);
                stmt.setDate(3, new java.sql.Date(fechaInicio.getTime()));
                stmt.setDate(4, new java.sql.Date(fechaFin.getTime()));
                stmt.setDouble(5, monto);
                stmt.setString(6, concepto);
                stmt.setString(7, estado);
                stmt.setInt(8, idPago);

                int filas = stmt.executeUpdate();
                JOptionPane.showMessageDialog(null,
                        filas > 0 ? "Pago actualizado" : "No se pudo actualizar");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al editar pago: " + e.getMessage());
        }
    }

    // ==================== Eliminar Pago ====================
    public void eliminarPago(int idPago) {
        String sql = "DELETE FROM Pago WHERE id_pago=?";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPago);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Pago eliminado");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar pago: " + e.getMessage());
        }
    }

    public void listarPagos(JTable tabla) {
        String[] columnas = {"ID Pago", "Cliente", "Tipo de Pago", "Fecha Inicio", "Fecha Fin", "Monto", "Concepto", "Estado", "Hora y Fecha"};
        DefaultTableModel modelo = new DefaultTableModel(null, columnas);

        String sql = "SELECT p.id_pago, c.nombre AS cliente, t.nombre AS tipo_pago, "
                + "p.fecha_inicio, p.fecha_fin, p.monto, p.concepto, p.estado, p.created_at "
                + "FROM Pago p "
                + "JOIN Cliente c ON p.id_cliente = c.id_cliente "
                + "JOIN TipoPago t ON p.id_tipo_pago = t.id_tipo_pago "
                + "ORDER BY p.id_pago ASC";

        try (Connection conn = Conection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("id_pago"),
                    rs.getString("cliente"),
                    rs.getString("tipo_pago"),
                    rs.getDate("fecha_inicio"),
                    rs.getDate("fecha_fin"),
                    rs.getDouble("monto"),
                    rs.getString("concepto"),
                    rs.getString("estado"), // <- agregada la columna estado
                    rs.getTimestamp("created_at")
                };
                modelo.addRow(fila);
            }
            tabla.setModel(modelo);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al listar pagos: " + e.getMessage());
        }
    }

    public void buscarHistorialPorCliente(int idCliente, JTable tabla) {
        String[] columnas = {"ID Pago", "Cliente", "Tipo de Pago", "Fecha Inicio", "Fecha Fin", "Monto", "Concepto", "Estado", "Hora y Fecha"};
        DefaultTableModel modelo = new DefaultTableModel(null, columnas);

        try (Connection conn = Conection.getConnection()) {

            String sql = "SELECT p.id_pago, c.nombre AS cliente, t.nombre AS tipo_pago, "
                    + "p.fecha_inicio, p.fecha_fin, p.monto, p.concepto, p.estado, p.created_at "
                    + "FROM Pago p "
                    + "JOIN Cliente c ON p.id_cliente = c.id_cliente "
                    + "JOIN TipoPago t ON p.id_tipo_pago = t.id_tipo_pago "
                    + "WHERE c.id_cliente = ? "
                    + "ORDER BY p.id_pago ASC";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, idCliente);

                try (ResultSet rs = stmt.executeQuery()) {
                    boolean tieneHistorial = false;
                    while (rs.next()) {
                        Object[] fila = {
                            rs.getInt("id_pago"),
                            rs.getString("cliente"),
                            rs.getString("tipo_pago"),
                            rs.getDate("fecha_inicio"),
                            rs.getDate("fecha_fin"),
                            rs.getDouble("monto"),
                            rs.getString("concepto"),
                            rs.getString("estado"),
                            rs.getTimestamp("created_at")
                        };
                        modelo.addRow(fila);
                        tieneHistorial = true;
                    }

                    if (!tieneHistorial) {
                        JOptionPane.showMessageDialog(null,
                                "El cliente con ID " + idCliente + " no tiene historial de pagos.",
                                "Historial vacío",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }

            tabla.setModel(modelo);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al buscar historial: " + e.getMessage());
        }
    }

    public void listarMembresias(JTable tabla) {
        String[] columnas = {"ID Cliente", "Nombre", "Membresía", "Fecha Inicio", "Fecha Fin", "Estado"};
        DefaultTableModel modelo = new DefaultTableModel(null, columnas);

        String sql = "SELECT cm.id_cliente, c.nombre || ' ' || c.apellido AS cliente, "
                + "m.tipo AS membresia, cm.fecha_inicio, cm.fecha_fin, cm.estado "
                + "FROM Cliente_Membresia cm "
                + "JOIN Cliente c ON cm.id_cliente = c.id_cliente "
                + "JOIN Membresia m ON cm.id_membresia = m.id_membresia "
                + "ORDER BY cm.id_cliente ASC";

        try (Connection conn = Conection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("id_cliente"),
                    rs.getString("cliente"),
                    rs.getString("membresia"),
                    rs.getDate("fecha_inicio"),
                    rs.getDate("fecha_fin"),
                    rs.getString("estado") // <- aquí se ve el estado
                };
                modelo.addRow(fila);
            }

            tabla.setModel(modelo);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al listar membresías: " + e.getMessage());
        }
    }

    public void listarMem(JTable tabla) {
        // Columnas que quieres mostrar
        String[] columnas = {"ID", "Tipo", "Precio"};
        DefaultTableModel modelo = new DefaultTableModel(null, columnas);

        // Consulta SQL
        String sql = "SELECT id_membresia, tipo, precio FROM Membresia ORDER BY id_membresia ASC";

        try (Connection conn = Conection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("id_membresia"),
                    rs.getString("tipo"),
                    rs.getDouble("precio")
                };
                modelo.addRow(fila);
            }

            tabla.setModel(modelo);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al listar membresías: " + e.getMessage());
        }
    }

}
