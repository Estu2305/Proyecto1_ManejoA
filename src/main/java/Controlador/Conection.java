/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author SELVYN
 */
public class Conection {

    private static final String URL = "jdbc:postgresql://localhost:5432/GYM";
    private static final String USER = "postgres";
    private static final String PASSWORD = "selta2305";

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexion exitosa a PostgreSQL");
            return conn;
        } catch (SQLException e) {
            System.out.println("Error al conectarse a la base de datos: " + e.getMessage());
            return null;
        }
    }
}
