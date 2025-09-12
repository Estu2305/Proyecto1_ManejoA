/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Controlador.Conection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author SELVYN
 */
public class Inicio {

    public String validarLogin(String usuario, String contrasenia) {

        String rol = null;

        try (Connection conn = Conection.getConnection()) {
            String sql = "SELECT e.usuario, r.nombre AS rol "
                    + "FROM Empleado e "
                    + "JOIN Rol r ON e.id_rol = r.id_rol "
                    + "WHERE e.usuario = ? AND e.contrasenia = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, usuario);
            stmt.setString(2, contrasenia);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                rol = rs.getString("rol");
            }
        } catch (Exception ex) {
            System.out.println("Error en validarLogin: " + ex.getMessage());
        }

        return rol; // si es null, significa que no encontró usuario
    }
}
