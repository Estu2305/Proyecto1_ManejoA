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
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author SELVYN
 */
public class Recep {

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

    public boolean existeCliente(int idCliente) {
        String sql = "SELECT COUNT(*) FROM Cliente WHERE id_cliente=?";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error al verificar ID de cliente: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    // ==================== Agregar ====================
    public void agregarCliente(int idCliente, String nombre, String apellido, String correo, String telefono) {
        if (!validarCamposTexto(nombre, apellido, correo, telefono)) {
            return;
        }
        if (existeCliente(idCliente)) {
            JOptionPane.showMessageDialog(null,
                    "El ID de cliente ya existe. Elige otro ID.",
                    "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "INSERT INTO Cliente (id_cliente, nombre, apellido, correo, telefono) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            stmt.setString(2, nombre);
            stmt.setString(3, apellido);
            stmt.setString(4, correo);
            stmt.setString(5, telefono);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Cliente agregado con éxito");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al agregar cliente: " + e.getMessage());
        }
    }

    // ==================== Editar ====================
    public void editarCliente(int idCliente, String nombre, String apellido, String correo, String telefono) {
        if (!validarCamposTexto(nombre, apellido, correo, telefono)) {
            return;
        }
        if (!existeCliente(idCliente)) {
            JOptionPane.showMessageDialog(null,
                    "El ID de cliente no existe.",
                    "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "UPDATE Cliente SET nombre=?, apellido=?, correo=?, telefono=? WHERE id_cliente=?";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            stmt.setString(2, apellido);
            stmt.setString(3, correo);
            stmt.setString(4, telefono);
            stmt.setInt(5, idCliente);

            int filas = stmt.executeUpdate();
            JOptionPane.showMessageDialog(null,
                    filas > 0 ? "Cliente actualizado con éxito"
                            : "No se encontró el cliente con el ID especificado");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar cliente: " + e.getMessage());
        }
    }

    // ==================== Eliminar ====================
    public void eliminarCliente(int idCliente) {
        if (!existeCliente(idCliente)) {
            JOptionPane.showMessageDialog(null,
                    "El ID de cliente no existe. Ingresa un ID válido.",
                    "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "DELETE FROM Cliente WHERE id_cliente=?";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Cliente eliminado con éxito");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar cliente: " + e.getMessage());
        }
    }

    // ==================== Listar ====================
    public void listarClientes(JTable tabla) {
        String[] columnas = {"ID", "Nombre", "Apellido", "Correo", "Telefono", "Fecha Registro"};
        DefaultTableModel modelo = new DefaultTableModel(null, columnas);

        String sql = "SELECT id_cliente, nombre, apellido, correo, telefono, fecha_registro FROM Cliente ORDER BY id_cliente ASC";

        try (Connection conn = Conection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("id_cliente"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("correo"),
                    rs.getString("telefono"),
                    rs.getDate("fecha_registro")
                };
                modelo.addRow(fila);
            }
            tabla.setModel(modelo);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al listar clientes: " + e.getMessage());
        }
    }
}
