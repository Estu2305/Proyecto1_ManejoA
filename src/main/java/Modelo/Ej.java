/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Controlador.Conection;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author SELVYN
 */
public class Ej {

    // ==================== Validar Campos ====================
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

    private boolean existeEjercicio(int idEjercicio) {
        String sql = "SELECT COUNT(*) FROM Ejercicio WHERE id_ejercicio=?";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEjercicio);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error al verificar ID de ejercicio: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    // ==================== Agregar ====================
    public void agregarEjercicio(String nombre) {
        if (!validarCamposTexto(nombre)) {
            return;
        }

        String sql = "INSERT INTO Ejercicio (nombre) VALUES (?)";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombre);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Ejercicio agregado con éxito");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al agregar ejercicio: " + e.getMessage());
        }
    }

    // ==================== Editar ====================
    public void editarEjercicio(int idEjercicio, String nombre) {
        if (!validarCamposTexto(nombre)) {
            return;
        }

        String sql = "UPDATE Ejercicio SET nombre=? WHERE id_ejercicio=?";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombre);
            stmt.setInt(2, idEjercicio);

            int filas = stmt.executeUpdate();
            JOptionPane.showMessageDialog(null,
                    filas > 0 ? "Ejercicio actualizado con éxito"
                            : "No se encontró el ejercicio con el ID especificado");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar ejercicio: " + e.getMessage());
        }
    }

    // ==================== Eliminar ====================
    public void eliminarEjercicio(int idEjercicio) {
        if (!existeEjercicio(idEjercicio)) {
            JOptionPane.showMessageDialog(null,
                    "El ID de ejercicio no existe. Ingresa un ID válido.",
                    "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "DELETE FROM Ejercicio WHERE id_ejercicio=?";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEjercicio);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Ejercicio eliminado con éxito");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar ejercicio: " + e.getMessage());
        }
    }

    // ==================== Listar ====================
    public void listarEjercicios(JTable tabla) {
        String[] columnas = {"ID Ejercicio", "Nombre"};
        DefaultTableModel modelo = new DefaultTableModel(null, columnas);

        String sql = "SELECT id_ejercicio, nombre FROM Ejercicio ORDER BY id_ejercicio ASC";

        try (Connection conn = Conection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("id_ejercicio"),
                    rs.getString("nombre")
                };
                modelo.addRow(fila);
            }
            tabla.setModel(modelo);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al listar ejercicios: " + e.getMessage());
        }
    }

    // ==================== Limpiar ====================
    public void limpiarCampos(JTextField txtIdEjercicio, JTextField txtNombre) {
        txtIdEjercicio.setText("");
        txtNombre.setText("");
        txtNombre.requestFocus();
    }

    public static void asignarEjercicioEquipo(int idRutina, int idEjercicio, int idEquipo) {
        String sql = "INSERT INTO Ejercicio_Equipo (id_rutina, id_ejercicio, id_equipo) VALUES (?, ?, ?)";

        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idRutina);
            stmt.setInt(2, idEjercicio);
            stmt.setInt(3, idEquipo);

            stmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "Ejercicio asignado al equipo en la rutina con éxito");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al asignar ejercicio al equipo en la rutina: " + e.getMessage());
        }
    }

    public static void cargarEquiposPorEntrenador(JComboBox<String> combo, int idEntrenador) {
        String sql = "SELECT e.id_equipo, e.nombre, "
                + "       i.cantidad - COUNT(ree.id_equipo) AS disponibles "
                + "FROM Equipo e "
                + "JOIN Inventario i ON e.id_equipo = i.id_equipo "
                + "JOIN Empleado em ON em.id_sucursal = i.id_sucursal "
                + "LEFT JOIN Ejercicio_Equipo ree "
                + "       ON ree.id_equipo = e.id_equipo "
                + "WHERE em.id_empleado = ? "
                + "  AND i.estado = 'Disponible' "
                + "GROUP BY e.id_equipo, e.nombre, i.cantidad "
                + "HAVING i.cantidad - COUNT(ree.id_equipo) > 0 "
                + "ORDER BY e.nombre";

        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEntrenador);
            ResultSet rs = stmt.executeQuery();

            combo.removeAllItems(); // limpiar antes de llenar

            while (rs.next()) {
                int id = rs.getInt("id_equipo");
                String nombre = rs.getString("nombre");
                int disponibles = rs.getInt("disponibles");

                combo.addItem(id + " - " + nombre + " (Disponibles: " + disponibles + ")");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error cargando equipos disponibles: " + e.getMessage());
        }
    }

}
