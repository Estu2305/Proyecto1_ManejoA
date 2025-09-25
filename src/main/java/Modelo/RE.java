/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Controlador.Conection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author SELVYN
 */
public class RE {

    // ==================== Métodos de ayuda ====================
    private boolean existeRutina(int idRutina) {
        String sql = "SELECT COUNT(*) FROM Rutina WHERE id_rutina=?";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idRutina);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al verificar rutina: " + e.getMessage());
        }
        return false;
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
            JOptionPane.showMessageDialog(null, "Error al verificar ejercicio: " + e.getMessage());
        }
        return false;
    }

    private boolean existeRutinaEjercicio(int idRutina, int idEjercicio) {
        String sql = "SELECT COUNT(*) FROM Rutina_Ejercicio WHERE id_rutina=? AND id_ejercicio=?";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idRutina);
            stmt.setInt(2, idEjercicio);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al verificar relación: " + e.getMessage());
        }
        return false;
    }

    // ==================== Agregar ====================
    public void agregarRutinaEjercicio(int idRutina, int idEjercicio,
            int series, int repeticiones, String duracion) {
        if (!existeRutina(idRutina)) {
            JOptionPane.showMessageDialog(null, "La rutina indicada no existe.");
            return;
        }
        if (!existeEjercicio(idEjercicio)) {
            JOptionPane.showMessageDialog(null, "El ejercicio indicado no existe.");
            return;
        }
        if (existeRutinaEjercicio(idRutina, idEjercicio)) {
            JOptionPane.showMessageDialog(null, "Ese ejercicio ya está asignado a la rutina.");
            return;
        }

        String sql = "INSERT INTO Rutina_Ejercicio (id_rutina, id_ejercicio, series, repeticiones, duracion) "
                + "VALUES (?, ?, ?, ?, ?::INTERVAL)";

        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idRutina);
            stmt.setInt(2, idEjercicio);
            stmt.setInt(3, series);
            stmt.setInt(4, repeticiones);
            stmt.setString(5, duracion); // Ej: '00:30:00' → 30 minutos
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "Ejercicio agregado a la rutina con éxito");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al agregar: " + e.getMessage());
        }
    }

    // ==================== Editar ====================
    public void editarRutinaEjercicio(int idRutina, int idEjercicio,
            int series, int repeticiones, String duracion) {
        if (!existeRutinaEjercicio(idRutina, idEjercicio)) {
            JOptionPane.showMessageDialog(null, "No existe esa relación rutina–ejercicio.");
            return;
        }

        String sql = "UPDATE Rutina_Ejercicio SET series=?, repeticiones=?, duracion=?::INTERVAL "
                + "WHERE id_rutina=? AND id_ejercicio=?";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, series);
            stmt.setInt(2, repeticiones);
            stmt.setString(3, duracion);
            stmt.setInt(4, idRutina);
            stmt.setInt(5, idEjercicio);

            int filas = stmt.executeUpdate();
            JOptionPane.showMessageDialog(null,
                    filas > 0 ? "Relación actualizada con éxito"
                            : "No se encontró la relación");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar: " + e.getMessage());
        }
    }

    // ==================== Eliminar ====================
    public void eliminarRutinaEjercicio(int idRutina, int idEjercicio) {
        if (!existeRutinaEjercicio(idRutina, idEjercicio)) {
            JOptionPane.showMessageDialog(null, "No existe esa relación rutina–ejercicio.");
            return;
        }

        String sql = "DELETE FROM Rutina_Ejercicio WHERE id_rutina=? AND id_ejercicio=?";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idRutina);
            stmt.setInt(2, idEjercicio);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "Ejercicio eliminado de la rutina con éxito");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar: " + e.getMessage());
        }
    }

    public void listarRutinaEjercicios(JTable tabla, int idEntrenador) {
        String[] columnas = {"Rutina", "Ejercicio", "Series", "Repeticiones", "Duración"};
        DefaultTableModel modelo = new DefaultTableModel(null, columnas);

        String sql = "SELECT re.id_rutina AS rutina, ex.nombre AS ejercicio, re.series, re.repeticiones, re.duracion "
                + "FROM Rutina_Ejercicio re "
                + "JOIN Ejercicio ex ON re.id_ejercicio = ex.id_ejercicio "
                + "JOIN Rutina r ON re.id_rutina = r.id_rutina "
                + "WHERE r.id_entrenador = ? "
                + "ORDER BY re.id_rutina, ex.nombre";

        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEntrenador);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Object[] fila = {
                        rs.getInt("rutina"),
                        rs.getString("ejercicio"),
                        rs.getInt("series"),
                        rs.getInt("repeticiones"),
                        rs.getString("duracion")
                    };
                    modelo.addRow(fila);
                }
            }

            tabla.setModel(modelo);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al listar: " + e.getMessage());
        }
    }

}
