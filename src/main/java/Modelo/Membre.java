/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Controlador.Conection;
import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author SELVYN
 */
public class Membre {

    private boolean clienteExiste(int idCliente) {
        String sql = "SELECT 1 FROM Cliente WHERE id_cliente = ?";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // si encuentra un registro, retorna true
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al verificar cliente: " + e.getMessage());
            return false;
        }
    }

    public boolean validarCamposTexto(String... campos) {
        for (String campo : campos) {
            if (campo == null || campo.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Todos los campos deben estar completos",
                        "Advertencia",
                        JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        return true;
    }

    public void agregarMembresia(int idCliente, int idMembresia, Date fechaInicio) {
        if (!clienteExiste(idCliente)) {
            JOptionPane.showMessageDialog(null, "El cliente con ID " + idCliente + " no existe.", "Cliente no encontrado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtener duración desde la tabla Membresia
        int duracionMeses = 0;
        String sqlDuracion = "SELECT duracion_meses FROM Membresia WHERE id_membresia = ?";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sqlDuracion)) {

            stmt.setInt(1, idMembresia);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    duracionMeses = rs.getInt("duracion_meses");
                } else {
                    JOptionPane.showMessageDialog(null, "Tipo de membresía no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Calcular fecha fin
            Calendar cal = Calendar.getInstance();
            cal.setTime(fechaInicio);
            cal.add(Calendar.MONTH, duracionMeses);
            Date fechaFin = cal.getTime();

            // Insertar membresía
            String sqlInsert = "INSERT INTO Cliente_Membresia (id_cliente, id_membresia, fecha_inicio, fecha_fin) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert)) {
                stmtInsert.setInt(1, idCliente);
                stmtInsert.setInt(2, idMembresia);
                stmtInsert.setDate(3, new java.sql.Date(fechaInicio.getTime()));
                stmtInsert.setDate(4, new java.sql.Date(fechaFin.getTime()));
                stmtInsert.executeUpdate();
                JOptionPane.showMessageDialog(null, "Membresía registrada con éxito");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al registrar membresía: " + e.getMessage());
        }
    }

    public void editarMembresia(int idCliente, int idMembresia, Date fechaInicio) {
        if (!clienteExiste(idCliente)) {
            JOptionPane.showMessageDialog(null, "El cliente con ID " + idCliente + " no existe.", "Cliente no encontrado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtener duración desde la tabla Membresia
        int duracionMeses = 0;
        String sqlDuracion = "SELECT duracion_meses FROM Membresia WHERE id_membresia = ?";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sqlDuracion)) {

            stmt.setInt(1, idMembresia);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    duracionMeses = rs.getInt("duracion_meses");
                } else {
                    JOptionPane.showMessageDialog(null, "Tipo de membresía no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Calcular fecha fin
            Calendar cal = Calendar.getInstance();
            cal.setTime(fechaInicio);
            cal.add(Calendar.MONTH, duracionMeses);
            Date fechaFin = cal.getTime();

            // Actualizar membresía
            String sqlUpdate = "UPDATE Cliente_Membresia SET id_membresia = ?, fecha_inicio = ?, fecha_fin = ? WHERE id_cliente = ?";
            try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)) {
                stmtUpdate.setInt(1, idMembresia);
                stmtUpdate.setDate(2, new java.sql.Date(fechaInicio.getTime()));
                stmtUpdate.setDate(3, new java.sql.Date(fechaFin.getTime()));
                stmtUpdate.setInt(4, idCliente);

                int filas = stmtUpdate.executeUpdate();
                if (filas > 0) {
                    JOptionPane.showMessageDialog(null, "Membresía actualizada con éxito");
                } else {
                    JOptionPane.showMessageDialog(null, "No se encontró la membresía para actualizar");
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar membresía: " + e.getMessage());
        }
    }

    public void eliminarMembresia(int idCliente) {
        if (!clienteExiste(idCliente)) {
            JOptionPane.showMessageDialog(null, "El cliente con ID " + idCliente + " no existe.", "Cliente no encontrado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validarCamposTexto(String.valueOf(idCliente))) {
            return;
        }

        String sql = "DELETE FROM Cliente_Membresia WHERE id_cliente = ?";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                JOptionPane.showMessageDialog(null, "Membresía eliminada con éxito");
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró la membresía para eliminar");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar membresía: " + e.getMessage());
        }
    }

    public void listarMembresias(JTable tabla) {
        String[] columnas = {"ID Cliente", "Membresía", "Fecha Inicio", "Fecha Fin"};
        DefaultTableModel modelo = new DefaultTableModel(null, columnas);

        String sql = "SELECT cm.id_cliente, m.tipo, cm.fecha_inicio, cm.fecha_fin "
                + "FROM Cliente_Membresia cm "
                + "JOIN Membresia m ON cm.id_membresia = m.id_membresia "
                + "ORDER BY cm.id_cliente ASC";

        try (Connection conn = Conection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("id_cliente"),
                    rs.getString("tipo"),
                    rs.getDate("fecha_inicio"),
                    rs.getDate("fecha_fin")
                };
                modelo.addRow(fila);
            }

            tabla.setModel(modelo);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al listar membresías: " + e.getMessage());
        }
    }
}
