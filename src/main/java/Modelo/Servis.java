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
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author SELVYN
 */
public class Servis {

    private boolean validarCamposTexto(String... campos) {
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

    private boolean existeRegistro(int idCliente, int idServicio) {
        String sql = "SELECT COUNT(*) FROM Cliente_Servicio WHERE id_cliente=? AND id_servicio=?";

        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            stmt.setInt(2, idServicio);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error al verificar Cliente_Servicio: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    // ==================== Agregar ====================
    public void agregarClienteServicio(int idCliente, int idServicio, Date fecha) {
        if (!validarCamposTexto(String.valueOf(idCliente), String.valueOf(idServicio), fecha.toString())) {
            return;
        }

        if (existeRegistro(idCliente, idServicio)) {
            JOptionPane.showMessageDialog(null,
                    "Este cliente ya tiene asignado este servicio",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "INSERT INTO Cliente_Servicio (id_cliente, id_servicio, fecha) VALUES (?, ?, ?)";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            stmt.setInt(2, idServicio);
            stmt.setDate(3, new java.sql.Date(fecha.getTime()));

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Servicio asignado con éxito");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al asignar servicio: " + e.getMessage());
        }
    }

    // ==================== Editar ====================
    public void editarClienteServicio(int idCliente, int idServicio, Date fechaOriginal, Date nuevaFecha) {
        if (!validarCamposTexto(String.valueOf(idCliente), String.valueOf(idServicio), fechaOriginal.toString())) {
            return;
        }

        // Verificar que existe el registro original
        String sqlCheck = "SELECT COUNT(*) FROM Cliente_Servicio WHERE id_cliente=? AND id_servicio=? AND fecha=?";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmtCheck = conn.prepareStatement(sqlCheck)) {

            stmtCheck.setInt(1, idCliente);
            stmtCheck.setInt(2, idServicio);
            stmtCheck.setDate(3, new java.sql.Date(fechaOriginal.getTime()));

            ResultSet rs = stmtCheck.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                JOptionPane.showMessageDialog(null,
                        "No existe este registro Cliente-Servicio",
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al verificar registro: " + e.getMessage());
            return;
        }

        // Actualizar fecha
        String sqlUpdate = "UPDATE Cliente_Servicio SET fecha=? WHERE id_cliente=? AND id_servicio=? AND fecha=?";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sqlUpdate)) {

            stmt.setDate(1, new java.sql.Date(nuevaFecha.getTime())); // nueva fecha
            stmt.setInt(2, idCliente);
            stmt.setInt(3, idServicio);
            stmt.setDate(4, new java.sql.Date(fechaOriginal.getTime())); // fecha original en el WHERE

            int filas = stmt.executeUpdate();
            JOptionPane.showMessageDialog(null,
                    filas > 0 ? "Registro actualizado" : "No se pudo actualizar");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al editar servicio: " + e.getMessage());
        }
    }

    // ==================== Eliminar ====================
    public void eliminarClienteServicio(int idCliente, int idServicio) {
        if (!existeRegistro(idCliente, idServicio)) {
            JOptionPane.showMessageDialog(null,
                    "El registro no existe",
                    "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "DELETE FROM Cliente_Servicio WHERE id_cliente=? AND id_servicio=?";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            stmt.setInt(2, idServicio);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Registro eliminado");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar servicio: " + e.getMessage());
        }
    }

    // ==================== Listar ====================
    public void listarClienteServicios(JTable tabla) {
        String[] columnas = {"ID Cliente", "ID Servicio", "Fecha"};
        DefaultTableModel modelo = new DefaultTableModel(null, columnas);

        String sql = "SELECT id_cliente, id_servicio, fecha FROM Cliente_Servicio ORDER BY id_cliente ASC";

        try (Connection conn = Conection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("id_cliente"),
                    rs.getInt("id_servicio"),
                    rs.getDate("fecha")
                };
                modelo.addRow(fila);
            }
            tabla.setModel(modelo);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al listar servicios asignados: " + e.getMessage());
        }
    }
}
