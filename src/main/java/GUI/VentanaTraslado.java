/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

import Modelo.Traslado;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author SELVYN
 */
public class VentanaTraslado extends JDialog {

    private JTextField txtSucursalOrigen, txtSucursalDestino, txtEquipo, txtCantidad;
    private JButton btnConfirmar, btnCancelar;

    public VentanaTraslado(JFrame parent, int sucursalOrigen, int equipo) {
        super(parent, "Traslado de Inventario", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(5, 2, 10, 10));

        // Campos
        add(new JLabel("Sucursal Origen:"));
        txtSucursalOrigen = new JTextField(String.valueOf(sucursalOrigen));
        txtSucursalOrigen.setEditable(false);
        add(txtSucursalOrigen);

        add(new JLabel("Sucursal Destino:"));
        txtSucursalDestino = new JTextField();
        add(txtSucursalDestino);

        add(new JLabel("Equipo:"));
        txtEquipo = new JTextField(String.valueOf(equipo));
        txtEquipo.setEditable(false);
        add(txtEquipo);

        add(new JLabel("Cantidad:"));
        txtCantidad = new JTextField();
        add(txtCantidad);

        // Botones
        btnConfirmar = new JButton("Confirmar");
        btnCancelar = new JButton("Cancelar");

        add(btnConfirmar);
        add(btnCancelar);

        // Acción Confirmar
        btnConfirmar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int idSucursalOrigen = Integer.parseInt(txtSucursalOrigen.getText());
                    int idSucursalDestino = Integer.parseInt(txtSucursalDestino.getText());
                    int idEquipo = Integer.parseInt(txtEquipo.getText());
                    int cantidad = Integer.parseInt(txtCantidad.getText());

                    Traslado traslado = new Traslado();

                    // Paso 1: Verificar stock
                    int stockDisponible = traslado.obtenerStock(idSucursalOrigen, idEquipo);
                    if (stockDisponible < cantidad) {
                        JOptionPane.showMessageDialog(VentanaTraslado.this,
                                "Stock insuficiente. Disponible: " + stockDisponible,
                                "Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    // Paso 2: Descontar de origen
                    if (!traslado.descontarInventario(idSucursalOrigen, idEquipo, cantidad)) {
                        return;
                    }

                    // Paso 3: Aumentar en destino
                    if (!traslado.aumentarInventario(idSucursalDestino, idEquipo, cantidad)) {
                        traslado.aumentarInventario(idSucursalOrigen, idEquipo, cantidad); // rollback
                        JOptionPane.showMessageDialog(VentanaTraslado.this,
                                "No se pudo aumentar en destino. Operación revertida.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    JOptionPane.showMessageDialog(VentanaTraslado.this,
                            "Traslado realizado con éxito.",
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    dispose();

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(VentanaTraslado.this,
                            "Ingrese datos válidos en todos los campos.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Acción Cancelar
        btnCancelar.addActionListener(e -> dispose());
    }
}
