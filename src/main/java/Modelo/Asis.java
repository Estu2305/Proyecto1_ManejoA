/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Controlador.Conection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author SELVYN
 */
public class Asis {

    // ==================== Métodos de ayuda ====================
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

    public boolean existeAsistencia(int idAsistencia) {
        String sql = "SELECT COUNT(*) FROM Asistencia WHERE id_asistencia=?";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idAsistencia);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error al verificar ID de asistencia: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    // ==================== Agregar ====================
    public void agregarAsistencia(int idCliente, int idSucursal, Timestamp fechaHora) {
        if (!validarCamposTexto(String.valueOf(idCliente), String.valueOf(idSucursal), fechaHora.toString())) {
            return;
        }

        String sql = "INSERT INTO Asistencia (id_cliente, id_sucursal, fecha_hora) VALUES (?, ?, ?)";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            stmt.setInt(2, idSucursal);
            stmt.setTimestamp(3, fechaHora);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Asistencia registrada con éxito");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al registrar asistencia: " + e.getMessage());
        }
    }

    // ==================== Editar ====================
    public void editarAsistencia(int idAsistencia, int idCliente, int idSucursal, Timestamp fechaHora) {
        if (!validarCamposTexto(String.valueOf(idAsistencia), String.valueOf(idCliente),
                String.valueOf(idSucursal), fechaHora.toString())) {
            return;
        }
        if (!existeAsistencia(idAsistencia)) {
            JOptionPane.showMessageDialog(null,
                    "El ID de asistencia no existe.",
                    "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "UPDATE Asistencia SET id_cliente=?, id_sucursal=?, fecha_hora=? WHERE id_asistencia=?";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            stmt.setInt(2, idSucursal);
            stmt.setTimestamp(3, fechaHora);
            stmt.setInt(4, idAsistencia);

            int filas = stmt.executeUpdate();
            JOptionPane.showMessageDialog(null,
                    filas > 0 ? "Asistencia actualizada con éxito"
                            : "No se encontró asistencia con ese ID");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar asistencia: " + e.getMessage());
        }
    }

    // ==================== Eliminar ====================
    public void eliminarAsistencia(int idAsistencia) {
        if (!existeAsistencia(idAsistencia)) {
            JOptionPane.showMessageDialog(null,
                    "El ID de asistencia no existe. Ingresa un ID válido.",
                    "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "DELETE FROM Asistencia WHERE id_asistencia=?";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idAsistencia);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Asistencia eliminada con éxito");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar asistencia: " + e.getMessage());
        }
    }

    // ==================== Listar ====================
    public void listarAsistencias(JTable tabla) {
        String[] columnas = {"ID", "Cliente", "Sucursal", "Fecha y Hora"};
        DefaultTableModel modelo = new DefaultTableModel(null, columnas);

        String sql = "SELECT id_asistencia, id_cliente, id_sucursal, fecha_hora FROM Asistencia ORDER BY id_asistencia ASC";

        try (Connection conn = Conection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("id_asistencia"),
                    rs.getInt("id_cliente"),
                    rs.getInt("id_sucursal"),
                    rs.getTimestamp("fecha_hora")
                };
                modelo.addRow(fila);
            }
            tabla.setModel(modelo);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al listar asistencias: " + e.getMessage());
        }
    }

    public void listarClientes(JTable tabla) {
        String[] columnas = {"ID", "Nombre"}; // solo las columnas que queremos mostrar
        DefaultTableModel modelo = new DefaultTableModel(null, columnas);

        String sql = "SELECT id_cliente, nombre FROM Cliente ORDER BY id_cliente ASC";

        try (Connection conn = Conection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("id_cliente"),
                    rs.getString("nombre")
                };
                modelo.addRow(fila);
            }

            tabla.setModel(modelo);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al listar clientes: " + e.getMessage());
        }
    }

}
