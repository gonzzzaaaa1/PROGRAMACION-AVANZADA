package vista;

import modelo.Rol;
import modelo.Usuario;
import servicio.UsuarioServicio;
import util.TemaUI;
import util.Validaciones;

import javax.swing.*;
import java.awt.*;

/** Ventana de inicio de sesion. Punto de entrada visual del sistema. */
public class LoginFrame extends JFrame {

    private final UsuarioServicio usuarioServicio = new UsuarioServicio();
    private final JTextField txtDni = TemaUI.campo();
    private final JPasswordField txtPass = new JPasswordField();
    private final JButton btnIngresar = TemaUI.botonAccion("Ingresar");

    public LoginFrame() {
        setTitle("HealthHubCPS - Ingreso");
        setSize(440, 380);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(crearContenido());
        getRootPane().setDefaultButton(btnIngresar); // Enter inicia sesion
    }

    private JPanel crearContenido() {
        JPanel fondo = new JPanel(new GridBagLayout());
        fondo.setBackground(TemaUI.GRIS_FONDO);

        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TemaUI.GRIS_BORDE),
                BorderFactory.createEmptyBorder(28, 32, 28, 32)));

        JLabel titulo = new JLabel("HealthHubCPS");
        titulo.setFont(TemaUI.TITULO);
        titulo.setForeground(TemaUI.AZUL_OSCURO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Iniciar sesion");
        sub.setFont(TemaUI.NORMAL);
        sub.setForeground(TemaUI.TEXTO);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblDni = TemaUI.etiqueta("DNI");
        lblDni.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblPass = TemaUI.etiqueta("Contrasena");
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtDni.setMaximumSize(new Dimension(320, 34));
        txtDni.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtPass.setFont(TemaUI.NORMAL);
        txtPass.setMaximumSize(new Dimension(320, 34));
        txtPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnIngresar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnIngresar.addActionListener(e -> ingresar());

        tarjeta.add(titulo);
        tarjeta.add(Box.createVerticalStrut(2));
        tarjeta.add(sub);
        tarjeta.add(Box.createVerticalStrut(20));
        tarjeta.add(lblDni);
        tarjeta.add(Box.createVerticalStrut(4));
        tarjeta.add(txtDni);
        tarjeta.add(Box.createVerticalStrut(14));
        tarjeta.add(lblPass);
        tarjeta.add(Box.createVerticalStrut(4));
        tarjeta.add(txtPass);
        tarjeta.add(Box.createVerticalStrut(22));
        tarjeta.add(btnIngresar);

        fondo.add(tarjeta);
        return fondo;
    }

    private void ingresar() {
        String dni = txtDni.getText().trim();
        String pass = new String(txtPass.getPassword());
        if (Validaciones.vacio(dni) || Validaciones.vacio(pass)) {
            JOptionPane.showMessageDialog(this, "Complete DNI y contrasena.",
                    "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Usuario u = usuarioServicio.login(dni, pass);
        if (u == null) {
            JOptionPane.showMessageDialog(this, "DNI o contrasena incorrectos, o usuario inactivo.",
                    "Error de ingreso", JOptionPane.ERROR_MESSAGE);
            return;
        }
        abrirSegunRol(u);
    }

    private void abrirSegunRol(Usuario u) {
        JFrame ventana;
        if (u.getRol() == Rol.PACIENTE) {
            ventana = new PacienteFrame(u);
        } else if (u.getRol() == Rol.MEDICO) {
            ventana = new MedicoFrame(u);
        } else {
            ventana = new AdminFrame(u);
        }
        ventana.setVisible(true);
        dispose();
    }
}
