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
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author SELVYN
 */
public class Ser {

    // ==================== Métodos de ayuda ====================
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

    private boolean existeServicio(int idServicio) {
        String sql = "SELECT COUNT(*) FROM Servicio WHERE id_servicio=?";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idServicio);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error al verificar ID de servicio: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    // ==================== Agregar ====================
    public void agregarServicio(int idServicio, String nombre, String descripcion, double costo) {
        if (!validarCamposTexto(nombre, descripcion)) {
            return;
        }
        if (existeServicio(idServicio)) {
            JOptionPane.showMessageDialog(null,
                    "El ID de servicio ya existe. Elige otro ID.",
                    "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "INSERT INTO Servicio (id_servicio, nombre, descripcion, costo) VALUES (?, ?, ?, ?)";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idServicio);
            stmt.setString(2, nombre);
            stmt.setString(3, descripcion);
            stmt.setDouble(4, costo);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Servicio agregado con éxito");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al agregar servicio: " + e.getMessage());
        }
    }

    // ==================== Editar ====================
    public void editarServicio(int idServicio, String nombre, String descripcion, double costo) {
        if (!validarCamposTexto(nombre, descripcion)) {
            return;
        }
        if (!existeServicio(idServicio)) {
            JOptionPane.showMessageDialog(null,
                    "El ID de servicio no existe.",
                    "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "UPDATE Servicio SET nombre=?, descripcion=?, costo=? WHERE id_servicio=?";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            stmt.setString(2, descripcion);
            stmt.setDouble(3, costo);
            stmt.setInt(4, idServicio);

            int filas = stmt.executeUpdate();
            JOptionPane.showMessageDialog(null,
                    filas > 0 ? "Servicio actualizado con éxito"
                            : "No se encontró el servicio con el ID especificado");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar servicio: " + e.getMessage());
        }
    }

    // ==================== Eliminar ====================
    public void eliminarServicio(int idServicio) {
        if (!existeServicio(idServicio)) {
            JOptionPane.showMessageDialog(null,
                    "El ID de servicio no existe. Ingresa un ID válido.",
                    "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "DELETE FROM Servicio WHERE id_servicio=?";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idServicio);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Servicio eliminado con éxito");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar servicio: " + e.getMessage());
        }
    }

    // ==================== Listar ====================
    public void listarServicios(JTable tabla) {
        String[] columnas = {"ID", "Nombre", "Descripción", "Costo"};
        DefaultTableModel modelo = new DefaultTableModel(null, columnas);

        String sql = "SELECT id_servicio, nombre, descripcion, costo FROM Servicio ORDER BY id_servicio ASC";

        try (Connection conn = Conection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("id_servicio"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getDouble("costo")
                };
                modelo.addRow(fila);
            }
            tabla.setModel(modelo);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al listar servicios: " + e.getMessage());
        }
    }

    // ==================== Limpiar campos ====================
    public void limpiarCampos(JTextField txtIdServicio, JTextField txtNombre,
            JTextArea txtDescripcion, JTextField txtCosto) {

        txtIdServicio.setText("");
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtCosto.setText("");
        txtIdServicio.requestFocus();
    }

}
