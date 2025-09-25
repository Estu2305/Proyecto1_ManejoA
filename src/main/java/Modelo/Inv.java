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
public class Inv {

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

    private boolean existeEquipo(int idEquipo) {
        String sql = "SELECT COUNT(*) FROM Equipo WHERE id_equipo=?";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEquipo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error al verificar ID de equipo: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    public void agregarEquipo(int idEquipo, String nombre, String descripcion) {
        if (!validarCamposTexto(nombre, descripcion)) {
            return;
        }
        if (existeEquipo(idEquipo)) {
            JOptionPane.showMessageDialog(null,
                    "El ID de equipo ya existe. Elige otro ID.",
                    "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "INSERT INTO Equipo (id_equipo, nombre, descripcion) VALUES (?, ?, ?)";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEquipo);
            stmt.setString(2, nombre);
            stmt.setString(3, descripcion);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Equipo agregado con éxito");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al agregar equipo: " + e.getMessage());
        }
    }

    public void editarEquipo(int idEquipo, String nombre, String descripcion) {
        if (!validarCamposTexto(nombre, descripcion)) {
            return;
        }
        if (!existeEquipo(idEquipo)) {
            JOptionPane.showMessageDialog(null,
                    "El ID de equipo no existe.",
                    "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "UPDATE Equipo SET nombre=?, descripcion=? WHERE id_equipo=?";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombre);
            stmt.setString(2, descripcion);
            stmt.setInt(3, idEquipo);

            int filas = stmt.executeUpdate();
            JOptionPane.showMessageDialog(null,
                    filas > 0 ? "Equipo actualizado con éxito" : "No se encontró el equipo");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar equipo: " + e.getMessage());
        }
    }

    public void eliminarEquipo(int idEquipo) {
        if (!existeEquipo(idEquipo)) {
            JOptionPane.showMessageDialog(null,
                    "El ID de equipo no existe.",
                    "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "DELETE FROM Equipo WHERE id_equipo=?";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEquipo);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Equipo eliminado con éxito");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar equipo: " + e.getMessage());
        }
    }

    public void listarEquipos(JTable tabla) {
        String[] columnas = {"ID Equipo", "Nombre", "Descripcion"};
        DefaultTableModel modelo = new DefaultTableModel(null, columnas);

        String sql = "SELECT id_equipo, nombre, descripcion FROM Equipo ORDER BY id_equipo ASC";
        try (Connection conn = Conection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("id_equipo"),
                    rs.getString("nombre"),
                    rs.getString("descripcion")
                };
                modelo.addRow(fila);
            }
            tabla.setModel(modelo);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al listar equipos: " + e.getMessage());
        }
    }

    public void limpiarCampos(JTextField txtId, JTextField txtNombre, JTextArea txtDescripcion) {
        txtId.setText("");
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtId.requestFocus();
    }

}
