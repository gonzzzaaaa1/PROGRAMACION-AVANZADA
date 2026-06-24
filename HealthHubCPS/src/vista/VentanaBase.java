package vista;

import modelo.Usuario;
import util.TemaUI;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana base de la que heredan las ventanas de cada rol.
 * Arma el esqueleto comun: encabezado, menu lateral y area de contenido
 * con navegacion por tarjetas.
 */
public abstract class VentanaBase extends JFrame {

    protected final Usuario usuario;
    private final CardLayout cards = new CardLayout();
    private final JPanel contenido = new JPanel(cards);
    private final JPanel menu = new JPanel();

    protected VentanaBase(Usuario usuario, String rol) {
        this.usuario = usuario;
        setTitle("HealthHubCPS - " + rol);
        setSize(980, 620);
        setMinimumSize(new Dimension(820, 540));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(crearHeader(), BorderLayout.NORTH);

        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBackground(TemaUI.AZUL_OSCURO);
        menu.setPreferredSize(new Dimension(240, 0));
        menu.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        add(menu, BorderLayout.WEST);

        contenido.setBackground(TemaUI.GRIS_FONDO);
        add(contenido, BorderLayout.CENTER);
    }

    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(TemaUI.AZUL);
        header.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel titulo = new JLabel("HealthHubCPS");
        titulo.setFont(TemaUI.TITULO);
        titulo.setForeground(Color.WHITE);
        header.add(titulo, BorderLayout.WEST);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        derecha.setOpaque(false);
        JLabel lblUser = new JLabel(usuario.getNombreCompleto() + "   |   " + usuario.getRol());
        lblUser.setForeground(Color.WHITE);
        lblUser.setFont(TemaUI.NORMAL);
        JButton salir = new JButton("Cerrar sesion");
        salir.setFocusPainted(false);
        salir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        salir.addActionListener(e -> cerrarSesion());
        derecha.add(lblUser);
        derecha.add(salir);
        header.add(derecha, BorderLayout.EAST);
        return header;
    }

    /** Agrega un boton al menu lateral que muestra la tarjeta indicada. */
    protected void agregarOpcion(String texto, String idTarjeta) {
        JButton b = TemaUI.botonMenu(texto);
        b.addActionListener(e -> cards.show(contenido, idTarjeta));
        menu.add(b);
        menu.add(Box.createVerticalStrut(2));
    }

    protected void agregarTarjeta(String idTarjeta, JComponent panel) {
        contenido.add(panel, idTarjeta);
    }

    protected void mostrar(String idTarjeta) {
        cards.show(contenido, idTarjeta);
    }

    /** Crea un panel de contenido vacio con su titulo arriba. */
    protected JPanel panelConTitulo(String titulo) {
        JPanel p = new JPanel(new BorderLayout(0, 14));
        p.setBackground(TemaUI.GRIS_FONDO);
        p.setBorder(BorderFactory.createEmptyBorder(22, 26, 22, 26));
        p.add(TemaUI.titulo(titulo), BorderLayout.NORTH);
        return p;
    }

    /** Crea un panel-formulario vertical con sus filas y un boton de accion. */
    protected JPanel panelFormulario(String titulo, JComponent[] filas, JButton accion) {
        JPanel p = panelConTitulo(titulo);
        JPanel caja = new JPanel();
        caja.setLayout(new BoxLayout(caja, BoxLayout.Y_AXIS));
        caja.setBackground(TemaUI.GRIS_FONDO);
        for (JComponent f : filas) {
            f.setAlignmentX(Component.LEFT_ALIGNMENT);
            caja.add(f);
            caja.add(Box.createVerticalStrut(10));
        }
        accion.setAlignmentX(Component.LEFT_ALIGNMENT);
        caja.add(Box.createVerticalStrut(8));
        caja.add(accion);
        p.add(caja, BorderLayout.CENTER);
        return p;
    }

    private void cerrarSesion() {
        dispose();
        new LoginFrame().setVisible(true);
    }
}
