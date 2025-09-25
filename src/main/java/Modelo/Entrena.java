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
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author SELVYN
 */
public class Entrena {

    public static void asignarEntrenador(int idCliente, int idEntrenador, Date fechaInicio, Date fechaFin) {
        String sql = "INSERT INTO Cliente_Entrenador (id_cliente, id_entrenador, fecha_asignacion, fecha_fin) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCliente);
            stmt.setInt(2, idEntrenador);
            stmt.setDate(3, new java.sql.Date(fechaInicio.getTime())); // fecha de inicio del pago
            stmt.setDate(4, new java.sql.Date(fechaFin.getTime()));    // fecha de fin del pago

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Entrenador asignado al cliente");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al asignar entrenador: " + e.getMessage());
        }

    }

    public void cargarEntrenadores(JComboBox<String> combo) {
        String sql = "SELECT id_empleado, nombre, apellido FROM Empleado WHERE id_rol = 3";
        try (Connection conn = Conection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            combo.removeAllItems();
            while (rs.next()) {
                int id = rs.getInt("id_empleado");
                String nombre = rs.getString("nombre") + " " + rs.getString("apellido");
                combo.addItem(id + " - " + nombre);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error cargando entrenadores: " + e.getMessage());
        }
    }

    public void cargarClientesAsignados(JTable tabla, int idEntrenador) {
        try (Connection conn = Conection.getConnection()) {
            String sql = "SELECT c.id_cliente, c.nombre, c.apellido, ce.fecha_asignacion, ce.fecha_fin "
                    + "FROM Cliente_Entrenador ce "
                    + "JOIN Cliente c ON ce.id_cliente = c.id_cliente "
                    + "WHERE ce.id_entrenador = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idEntrenador);

            ResultSet rs = stmt.executeQuery();

            DefaultTableModel modelo = (DefaultTableModel) tabla.getModel(); // 👈 ahora sobre la tabla que pasas
            modelo.setRowCount(0);

            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("id_cliente"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getDate("fecha_asignacion"),
                    rs.getDate("fecha_fin")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al cargar clientes: " + e.getMessage());
        }
    }

    public void cargarHistorialAsistencia(JTable tabla, int idCliente) {
        try (Connection conn = Conection.getConnection()) {
            String sql = "SELECT a.id_asistencia, s.nombre AS sucursal, a.fecha_hora "
                    + "FROM Asistencia a "
                    + "JOIN Sucursal s ON a.id_sucursal = s.id_sucursal "
                    + "WHERE a.id_cliente = ? "
                    + "ORDER BY a.fecha_hora DESC";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idCliente);

            ResultSet rs = stmt.executeQuery();

            DefaultTableModel modelo = new DefaultTableModel(
                    new Object[][]{},
                    new String[]{"ID Asistencia", "Sucursal", "Fecha y Hora"}
            );

            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getInt("id_asistencia"),
                    rs.getString("sucursal"),
                    rs.getTimestamp("fecha_hora")
                });
            }

            tabla.setModel(modelo);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al cargar historial: " + e.getMessage());
        }
    }

}
