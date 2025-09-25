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

/**
 *
 * @author SELVYN
 */
public class Traslado {

    public int obtenerStock(int idSucursal, int idEquipo) {
        String sql = "SELECT cantidad FROM Inventario WHERE id_sucursal = ? AND id_equipo = ?";
        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idSucursal);
            stmt.setInt(2, idEquipo);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("cantidad");
            } else {
                return 0; // No hay registro para ese equipo en esa sucursal
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al consultar stock: " + e.getMessage());
            return 0;
        }
    }

    public boolean descontarInventario(int idSucursal, int idEquipo, int cantidad) {
        if (cantidad <= 0) {
            JOptionPane.showMessageDialog(null, "La cantidad debe ser mayor que 0", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        String sql = "UPDATE Inventario SET cantidad = cantidad - ? "
                + "WHERE id_sucursal = ? AND id_equipo = ? AND cantidad >= ?";

        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cantidad);
            stmt.setInt(2, idSucursal);
            stmt.setInt(3, idEquipo);
            stmt.setInt(4, cantidad);

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                JOptionPane.showMessageDialog(null, "Stock descontado con éxito");
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "No hay suficiente stock en esta sucursal", "Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al descontar inventario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean aumentarInventario(int idSucursal, int idEquipo, int cantidad) {
        if (cantidad <= 0) {
            JOptionPane.showMessageDialog(null, "La cantidad debe ser mayor que 0", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        String sql = "UPDATE Inventario SET cantidad = cantidad + ? "
                + "WHERE id_sucursal = ? AND id_equipo = ?";

        try (Connection conn = Conection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cantidad);
            stmt.setInt(2, idSucursal);
            stmt.setInt(3, idEquipo);

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                JOptionPane.showMessageDialog(null, "Stock aumentado con éxito");
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el registro en esta sucursal", "Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al aumentar inventario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

}
