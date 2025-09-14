/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Controlador.Conection;
import static GUI.Administrador.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author SELVYN
 */
public class Admin {

    public void agregarEmpleado(int idEmpleado, int idSucursal, int idRol,
            String nombre, String apellido,
            String usuario, String contrasenia) {

        if (nombre == null || nombre.trim().isEmpty()
                || apellido == null || apellido.trim().isEmpty()
                || usuario == null || usuario.trim().isEmpty()
                || contrasenia == null || contrasenia.trim().isEmpty()) {

            JOptionPane.showMessageDialog(null,
                    "Todos los campos deben estar completos",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return; // Salimos del método y no actualizamos
        }

        String sqlCheck = "SELECT COUNT(*) FROM Empleado WHERE id_empleado = ?";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmtCheck = conn.prepareStatement(sqlCheck)) {

            stmtCheck.setInt(1, idEmpleado);
            ResultSet rs = stmtCheck.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(null,
                        "El ID de empleado ya existe. Elige otro ID.",
                        "Error",
                        JOptionPane.WARNING_MESSAGE);
                return; // salimos sin insertar
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error al verificar ID de empleado: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sqlInsert = "INSERT INTO Empleado (id_empleado, id_sucursal, id_rol, nombre, apellido, usuario, contrasenia) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sqlInsert)) {

            stmt.setInt(1, idEmpleado);
            stmt.setInt(2, idSucursal);
            stmt.setInt(3, idRol);
            stmt.setString(4, nombre);
            stmt.setString(5, apellido);
            stmt.setString(6, usuario);
            stmt.setString(7, contrasenia);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Empleado agregado con éxito");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al agregar empleado: " + e.getMessage());
        }
    }

    public void editarEmpleado(int idEmpleado, int idSucursal, int idRol,
            String nombre, String apellido,
            String usuario, String contrasenia) {

        // Validación antes de intentar actualizar
        if (nombre == null || nombre.trim().isEmpty()
                || apellido == null || apellido.trim().isEmpty()
                || usuario == null || usuario.trim().isEmpty()
                || contrasenia == null || contrasenia.trim().isEmpty()) {

            JOptionPane.showMessageDialog(null,
                    "Todos los campos deben estar completos",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return; // Salimos del método y no actualizamos
        }

        String sql = "UPDATE Empleado SET id_sucursal=?, id_rol=?, nombre=?, apellido=?, usuario=?, contrasenia=? "
                + "WHERE id_empleado=?";

        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idSucursal);
            stmt.setInt(2, idRol);
            stmt.setString(3, nombre);
            stmt.setString(4, apellido);
            stmt.setString(5, usuario);
            stmt.setString(6, contrasenia);
            stmt.setInt(7, idEmpleado);

            int filas = stmt.executeUpdate();

            if (filas > 0) {
                JOptionPane.showMessageDialog(null, "Empleado actualizado con éxito");
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el empleado con el ID especificado");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar empleado: " + e.getMessage());
        }
    }

    public void eliminarEmpleado(int idEmpleado) {

        String sqlCheck = "SELECT COUNT(*) FROM Empleado WHERE id_empleado=?";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmtCheck = conn.prepareStatement(sqlCheck)) {

            stmtCheck.setInt(1, idEmpleado);
            ResultSet rs = stmtCheck.executeQuery();

            if (rs.next() && rs.getInt(1) == 0) {
                JOptionPane.showMessageDialog(null,
                        "El ID de empleado no existe. Ingresa un ID válido.",
                        "Error",
                        JOptionPane.WARNING_MESSAGE);
                return; // salir sin eliminar
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error al verificar ID de empleado: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sqlDelete = "DELETE FROM Empleado WHERE id_empleado=?";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sqlDelete)) {

            stmt.setInt(1, idEmpleado);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "Empleado eliminado con éxito");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar empleado: " + e.getMessage());
        }
    }

    public void listarEmpleados(JTable tabla) {
        // Columnas de la tabla
        String[] columnas = {"ID", "Sucursal", "Rol", "Nombre", "Apellido", "Usuario", "Contrasenia"};
        DefaultTableModel modelo = new DefaultTableModel(null, columnas);

        String sql = "SELECT e.id_empleado, e.id_sucursal, r.nombre AS rol, "
                + "e.nombre, e.apellido, e.usuario, e.contrasenia "
                + "FROM Empleado e "
                + "JOIN Rol r ON e.id_rol = r.id_rol "
                + "ORDER BY e.id_empleado ASC";

        try (Connection conn = Conection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("id_empleado"),
                    rs.getInt("id_sucursal"),
                    rs.getString("rol"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("usuario"),
                    rs.getString("contrasenia")
                };
                modelo.addRow(fila);
            }

            tabla.setModel(modelo);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al listar empleados: " + e.getMessage());
        }
    }

    //================================== Adicionales de Validaciones ======================================================================
    public void limpiarCampos(JTextField txtEmpleado, JTextField txtSucursal,
            JComboBox CbRol, JTextField txtNombre,
            JTextField txtApellido, JTextField txtUsuario,
            JTextField txtContrasenia) {

        txtEmpleado.setText("");
        txtSucursal.setText("");
        CbRol.setSelectedIndex(0);
        txtNombre.setText("");
        txtApellido.setText("");
        txtUsuario.setText("");
        txtContrasenia.setText("");

        txtEmpleado.requestFocus();
    }

    public boolean validarCampos() {
        if (txtEmpleado.getText().trim().isEmpty()
                || txtSucursal.getText().trim().isEmpty()
                || txtNombre.getText().trim().isEmpty()
                || txtApellido.getText().trim().isEmpty()
                || txtUsuario.getText().trim().isEmpty()
                || txtConrasenia.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(null,
                    "Todos los campos deben estar llenos",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);

            return false;
        }
        return true;
    }
}
