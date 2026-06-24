package util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TemaUI {

    public static final Color AZUL_OSCURO = new Color(0x1F2A44);
    public static final Color AZUL = new Color(0x2C6FB5);
    public static final Color GRIS_FONDO = new Color(0xF4F6F9);
    public static final Color GRIS_BORDE = new Color(0xD7DCE3);
    public static final Color BLANCO = Color.WHITE;
    public static final Color TEXTO = new Color(0x2B2B2B);
    public static final Color TEXTO_CLARO = new Color(0xE8ECF3);
    public static final Color SELECCION = new Color(0xCFE0F2);

    public static final Font TITULO = new Font("SansSerif", Font.BOLD, 20);
    public static final Font SUBTITULO = new Font("SansSerif", Font.BOLD, 14);
    public static final Font NORMAL = new Font("SansSerif", Font.PLAIN, 14);

    public static void aplicarLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
        }
    }

    public static JButton botonMenu(String texto) {
        JButton b = new JButton(texto);
        b.setFont(NORMAL);
        b.setForeground(TEXTO_CLARO);
        b.setBackground(AZUL_OSCURO);
        b.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setFocusPainted(false);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static JButton botonAccion(String texto) {
        JButton b = new JButton(texto);
        b.setFont(SUBTITULO);
        b.setForeground(BLANCO);
        b.setBackground(AZUL);
        b.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static JLabel titulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(TITULO);
        l.setForeground(AZUL_OSCURO);
        return l;
    }

    public static JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(NORMAL);
        l.setForeground(TEXTO);
        return l;
    }

    public static JTextField campo() {
        JTextField t = new JTextField();
        t.setFont(NORMAL);
        return t;
    }

    public static JPanel fila(String etiqueta, JComponent campo) {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(440, 38));
        JLabel l = etiqueta(etiqueta);
        l.setPreferredSize(new Dimension(150, 30));
        p.add(l, BorderLayout.WEST);
        campo.setPreferredSize(new Dimension(270, 30));
        p.add(campo, BorderLayout.CENTER);
        return p;
    }

    public static DefaultTableModel modeloNoEditable(String[] columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    public static JScrollPane envolverTabla(JTable tabla) {
        tabla.setFont(NORMAL);
        tabla.setRowHeight(26);
        tabla.getTableHeader().setFont(SUBTITULO);
        tabla.setSelectionBackground(SELECCION);
        tabla.setGridColor(GRIS_BORDE);
        JScrollPane sp = new JScrollPane(tabla);
        sp.getViewport().setBackground(BLANCO);
        return sp;
    }
}
