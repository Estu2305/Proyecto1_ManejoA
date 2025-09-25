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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author SELVYN
 */
public class IM {

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

    private boolean existeInventario(int idInventario) {
        String sql = "SELECT COUNT(*) FROM Inventario WHERE id_inventario=?";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idInventario);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error al verificar ID de inventario: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    // AGREGAR
    public void agregarInventario(int idInventario, int idSucursal, int idEquipo,
            int cantidad, String estado, boolean mantenimiento) {

        if (!validarCamposTexto(estado)) {
            return;
        }
        if (cantidad <= 0) {
            JOptionPane.showMessageDialog(null, "La cantidad debe ser mayor que 0", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (existeInventario(idInventario)) {
            JOptionPane.showMessageDialog(null, "El ID de inventario ya existe", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "INSERT INTO Inventario (id_inventario, id_sucursal, id_equipo, cantidad, estado, requiere_mantenimiento) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idInventario);
            stmt.setInt(2, idSucursal);
            stmt.setInt(3, idEquipo);
            stmt.setInt(4, cantidad);
            stmt.setString(5, estado);
            stmt.setBoolean(6, mantenimiento);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Inventario agregado con éxito");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al agregar inventario: " + e.getMessage());
        }
    }

    // EDITAR
    public void editarInventario(int idInventario, int idSucursal, int idEquipo,
            int cantidad, String estado, boolean mantenimiento) {

        if (!validarCamposTexto(estado)) {
            return;
        }
        if (!existeInventario(idInventario)) {
            JOptionPane.showMessageDialog(null, "El ID de inventario no existe", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "UPDATE Inventario SET id_sucursal=?, id_equipo=?, cantidad=?, estado=?, requiere_mantenimiento=? "
                + "WHERE id_inventario=?";

        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idSucursal);
            stmt.setInt(2, idEquipo);
            stmt.setInt(3, cantidad);
            stmt.setString(4, estado);
            stmt.setBoolean(5, mantenimiento);
            stmt.setInt(6, idInventario);

            int filas = stmt.executeUpdate();
            JOptionPane.showMessageDialog(null,
                    filas > 0 ? "Inventario actualizado con éxito" : "No se encontró el inventario");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar inventario: " + e.getMessage());
        }
    }

    // ELIMINAR 
    public void eliminarInventario(int idInventario) {
        if (!existeInventario(idInventario)) {
            JOptionPane.showMessageDialog(null, "El ID de inventario no existe", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String sql = "DELETE FROM Inventario WHERE id_inventario=?";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idInventario);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Inventario eliminado con éxito");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar inventario: " + e.getMessage());
        }
    }

    public void listarInventario(JTable tabla) {
        String[] columnas = {"ID Inventario", "Sucursal", "Equipo", "Cantidad", "Estado", "Mantenimiento"};
        DefaultTableModel modelo = new DefaultTableModel(null, columnas);

        String sql = "SELECT i.id_inventario, s.nombre AS sucursal, e.nombre AS equipo, "
                + "i.cantidad, i.estado, i.requiere_mantenimiento "
                + "FROM Inventario i "
                + "JOIN Sucursal s ON i.id_sucursal = s.id_sucursal "
                + "JOIN Equipo e ON i.id_equipo = e.id_equipo "
                + "ORDER BY i.id_inventario ASC";

        try (Connection conn = Conection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("id_inventario"),
                    rs.getString("sucursal"),
                    rs.getString("equipo"),
                    rs.getInt("cantidad"),
                    rs.getString("estado"),
                    rs.getBoolean("requiere_mantenimiento") ? "Sí" : "No"
                };
                modelo.addRow(fila);
            }
            tabla.setModel(modelo);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al listar inventario: " + e.getMessage());
        }
    }

    // LIMPIAR CAMPOS
    public void limpiarCampos(JTextField txtId, JTextField txtSucursal, JTextField txtEquipo,
            JTextField txtCantidad, JComboBox<String> cbEstado, JCheckBox chkMantenimiento) {
        txtId.setText("");
        txtSucursal.setText("");
        txtEquipo.setText("");
        txtCantidad.setText("");
        cbEstado.setSelectedIndex(0);
        chkMantenimiento.setSelected(false);
        txtId.requestFocus();
    }

}
