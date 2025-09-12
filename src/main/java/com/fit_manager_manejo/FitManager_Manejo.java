/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.fit_manager_manejo;

import Controlador.Conection;
import GUI.Loggin;
import java.sql.Connection;

/**
 *
 * @author SELVYN
 */
public class FitManager_Manejo {

    public static void main(String[] args) {
        System.out.println("Hello World!");
        Connection conn = Conection.getConnection();
        if (conn != null) {
            System.out.println("¡Listo para usar la base de datos!");
        }

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Loggin ventana = new Loggin();
                ventana.setVisible(true);
            }
        });
    }
}
