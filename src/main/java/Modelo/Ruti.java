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
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author SELVYN
 */
public class Ruti {

    private boolean validarCamposRutina(String cliente, String entrenador, String descripcion) {
        if (cliente == null || cliente.trim().isEmpty()
                || entrenador == null || entrenador.trim().isEmpty()
                || descripcion == null || descripcion.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Todos los campos deben estar completos",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    // Verifica si ya existe la rutina
    private boolean rutinaExiste(int idCliente, int idEntrenador, String descripcion) {
        String sql = "SELECT COUNT(*) FROM Rutina WHERE id_cliente=? AND id_entrenador=? AND descripcion=?";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            stmt.setInt(2, idEntrenador);
            stmt.setString(3, descripcion);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al verificar rutina: " + e.getMessage());
        }
        return false;
    }

    public boolean rutinaPerteneceEntrenador(int idRutina, int idEntrenador) {
        String sql = "SELECT COUNT(*) FROM Rutina WHERE id_rutina = ? AND id_entrenador = ?";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idRutina);
            stmt.setInt(2, idEntrenador);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true; // La rutina pertenece al entrenador
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al verificar rutina: " + e.getMessage());
        }
        return false; // No pertenece
    }

    public void agregarRutina(int idCliente, int idEntrenador, String descripcion) {
        if (!validarCamposRutina(String.valueOf(idCliente), String.valueOf(idEntrenador), descripcion)) {
            return;
        }
        if (rutinaExiste(idCliente, idEntrenador, descripcion)) {
            JOptionPane.showMessageDialog(null, "La rutina ya existe", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "INSERT INTO Rutina (id_cliente, id_entrenador, descripcion) VALUES (?, ?, ?)";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            stmt.setInt(2, idEntrenador);
            stmt.setString(3, descripcion);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Rutina agregada con éxito");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al agregar rutina: " + e.getMessage());
        }
    }

    public void editarRutina(int idRutina, int idCliente, int idEntrenador, String descripcion) {
        if (!validarCamposRutina(String.valueOf(idCliente), String.valueOf(idEntrenador), descripcion)) {
            JOptionPane.showMessageDialog(null, "Debe ingresar todos los campos para editar");
            return;
        }

        String sql = "UPDATE Rutina SET id_cliente=?, id_entrenador=?, descripcion=? WHERE id_rutina=?";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            stmt.setInt(2, idEntrenador);
            stmt.setString(3, descripcion);
            stmt.setInt(4, idRutina);

            int filas = stmt.executeUpdate();
            JOptionPane.showMessageDialog(null,
                    filas > 0 ? "Rutina actualizada con éxito" : "No se encontró la rutina con el ID especificado");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar rutina: " + e.getMessage());
        }
    }

    public void eliminarRutina(int idRutina) {
        if (idRutina <= 0) {
            JOptionPane.showMessageDialog(null, "Seleccione una rutina para eliminar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "DELETE FROM Rutina WHERE id_rutina=?";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idRutina);
            int filas = stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, filas > 0 ? "Rutina eliminada con éxito" : "No se encontró la rutina");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar rutina: " + e.getMessage());
        }
    }

    public void listarRutinas(JTable tabla, int idEntrenador) {
        String[] columnas = {"ID", "Cliente", "Entrenador", "Fecha Inicio", "Descripcion"};
        DefaultTableModel modelo = new DefaultTableModel(null, columnas);

        String sql = "SELECT r.id_rutina, c.nombre AS cliente, e.nombre AS entrenador, r.fecha_inicio, r.descripcion "
                + "FROM Rutina r "
                + "JOIN Cliente c ON r.id_cliente = c.id_cliente "
                + "JOIN Empleado e ON r.id_entrenador = e.id_empleado "
                + "WHERE r.id_entrenador = ? "
                + "ORDER BY r.id_rutina ASC";

        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEntrenador);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Object[] fila = {
                        rs.getInt("id_rutina"),
                        rs.getString("cliente"),
                        rs.getString("entrenador"),
                        rs.getDate("fecha_inicio"),
                        rs.getString("descripcion")
                    };
                    modelo.addRow(fila);
                }
            }

            tabla.setModel(modelo);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al listar rutinas: " + e.getMessage());
        }
    }

    // ==================== Limpiar campos ====================
    public void limpiarCampos(JTextField txtCliente, JTextField txtEntrenador, JTextPane txtDescripcion) {
        txtCliente.setText("");
        txtEntrenador.setText("");
        txtDescripcion.setText("");
        txtCliente.requestFocus();
    }
}
