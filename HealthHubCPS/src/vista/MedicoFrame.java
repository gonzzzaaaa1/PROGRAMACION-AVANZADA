package vista;

import modelo.*;
import servicio.CatalogoServicio;
import servicio.MedicoServicio;
import servicio.Respuesta;
import servicio.TurnoServicio;
import util.TemaUI;
import util.Validaciones;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MedicoFrame extends VentanaBase {

    private final MedicoServicio medicoServicio = new MedicoServicio();
    private final TurnoServicio turnoServicio = new TurnoServicio();

    public MedicoFrame(Usuario usuario) {
        super(usuario, "Medico");

        agregarTarjeta("agenda", cardAgenda());
        agregarTarjeta("historia", cardHistoria());
        agregarTarjeta("adjuntar", cardAdjuntar());
        agregarTarjeta("resultado", cardSubirResultado());
        agregarTarjeta("autorizar", cardAutorizar());
        agregarTarjeta("cancelar", cardCancelar());

        agregarOpcion("Ver agenda", "agenda");
        agregarOpcion("Historia clinica", "historia");
        agregarOpcion("Adjuntar archivo", "adjuntar");
        agregarOpcion("Subir resultado", "resultado");
        agregarOpcion("Autorizar resultados", "autorizar");
        agregarOpcion("Cancelar turno", "cancelar");

        mostrar("agenda");
    }

    // ---- Agenda ----
    private JPanel cardAgenda() {
        JPanel p = panelConTitulo("Mi agenda");
        DefaultTableModel modelo = TemaUI.modeloNoEditable(
                new String[]{"Fecha", "Hora", "Paciente", "Estudio", "Consultorio"});
        JTable tabla = new JTable(modelo);
        Runnable recargar = () -> {
            modelo.setRowCount(0);
            for (Turno t : turnoServicio.listarAgendaMedico(usuario.getId())) {
                modelo.addRow(new Object[]{t.getFecha(), t.getHora(), t.getNombrePaciente(),
                        t.getNombreEstudio(), t.getNumeroConsultorio()});
            }
        };
        recargar.run();
        JButton refrescar = TemaUI.botonAccion("Actualizar");
        refrescar.addActionListener(e -> recargar.run());
        p.add(TemaUI.envolverTabla(tabla), BorderLayout.CENTER);
        p.add(refrescar, BorderLayout.SOUTH);
        return p;
    }

    // ---- Historia clinica ----
    private JPanel cardHistoria() {
        JPanel p = panelConTitulo("Historia clinica del paciente");
        JComboBox<Usuario> cboPaciente = new JComboBox<>();
        for (Usuario u : medicoServicio.listarPacientes(usuario.getId())) cboPaciente.addItem(u);

        JTextArea obs = new JTextArea(3, 20);
        obs.setEditable(false);
        obs.setLineWrap(true);
        DefaultTableModel modelo = TemaUI.modeloNoEditable(
                new String[]{"Tipo", "Formato", "URL", "Fecha carga"});
        JTable tabla = new JTable(modelo);

        JButton ver = TemaUI.botonAccion("Ver historia");
        ver.addActionListener(e -> {
            Usuario pac = (Usuario) cboPaciente.getSelectedItem();
            if (pac == null) { aviso("Selecciona un paciente."); return; }
            HistoriaClinica h = medicoServicio.verHistoria(pac.getId());
            obs.setText(h == null ? "El paciente no tiene historia clinica."
                    : "Creada: " + h.getFechaCreacion() + "\nObservaciones: "
                      + (h.getObservaciones() == null ? "-" : h.getObservaciones()));
            modelo.setRowCount(0);
            for (ArchivoAdjunto a : medicoServicio.listarArchivos(pac.getId())) {
                modelo.addRow(new Object[]{a.getTipo(), a.getFormato(), a.getUrl(), a.getFechaCarga()});
            }
        });

        cboPaciente.setPreferredSize(new Dimension(280, 30));
        JPanel selector = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        selector.setOpaque(false);
        selector.add(TemaUI.etiqueta("Paciente:"));
        selector.add(cboPaciente);
        selector.add(ver);

        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.setOpaque(false);
        centro.add(selector, BorderLayout.NORTH);
        JPanel detalle = new JPanel(new BorderLayout(0, 10));
        detalle.setOpaque(false);
        detalle.add(new JScrollPane(obs), BorderLayout.NORTH);
        detalle.add(TemaUI.envolverTabla(tabla), BorderLayout.CENTER);
        centro.add(detalle, BorderLayout.CENTER);

        p.add(centro, BorderLayout.CENTER);
        return p;
    }

    // ---- Adjuntar archivo ----
    private JPanel cardAdjuntar() {
        JComboBox<Turno> cboTurno = comboTurnos(turnoServicio.listarAgendaMedico(usuario.getId()));
        JComboBox<TipoArchivo> cboTipo = new JComboBox<>(TipoArchivo.values());
        JComboBox<FormatoArchivo> cboFormato = new JComboBox<>(FormatoArchivo.values());
        JTextField txtUrl = TemaUI.campo();

        JButton adjuntar = TemaUI.botonAccion("Adjuntar");
        adjuntar.addActionListener(e -> {
            Turno t = (Turno) cboTurno.getSelectedItem();
            if (t == null) { aviso("Selecciona un turno."); return; }
            if (Validaciones.vacio(txtUrl.getText())) { aviso("Indica la URL o ruta del archivo."); return; }
            Respuesta r = medicoServicio.adjuntarArchivo(t.getId(), usuario.getId(),
                    (TipoArchivo) cboTipo.getSelectedItem(),
                    (FormatoArchivo) cboFormato.getSelectedItem(), txtUrl.getText().trim());
            mostrarRespuesta(r);
        });

        return panelFormulario("Adjuntar archivo a historia clinica", new JComponent[]{
                TemaUI.fila("Turno", cboTurno),
                TemaUI.fila("Tipo", cboTipo),
                TemaUI.fila("Formato", cboFormato),
                TemaUI.fila("URL / ruta", txtUrl)
        }, adjuntar);
    }

    // ---- Subir resultado ----
    private JPanel cardSubirResultado() {
        JComboBox<Turno> cboTurno = comboTurnos(medicoServicio.listarTurnosSinResultado(usuario.getId()));
        JTextArea txtDesc = new JTextArea(4, 20);
        txtDesc.setLineWrap(true);
        JScrollPane spDesc = new JScrollPane(txtDesc);
        spDesc.setPreferredSize(new Dimension(270, 90));
        JCheckBox chkAutorizar = new JCheckBox("Autorizar ahora (visible para el paciente)");
        chkAutorizar.setOpaque(false);

        JButton subir = TemaUI.botonAccion("Subir resultado");
        subir.addActionListener(e -> {
            Turno t = (Turno) cboTurno.getSelectedItem();
            if (t == null) { aviso("Selecciona un turno."); return; }
            if (Validaciones.vacio(txtDesc.getText())) { aviso("Escribi la descripcion."); return; }
            Respuesta r = medicoServicio.subirResultado(t.getId(), txtDesc.getText().trim(),
                    chkAutorizar.isSelected(), usuario.getId());
            mostrarRespuesta(r);
        });

        return panelFormulario("Subir resultado de estudio", new JComponent[]{
                TemaUI.fila("Turno", cboTurno),
                TemaUI.fila("Descripcion", spDesc),
                chkAutorizar
        }, subir);
    }

    // ---- Autorizar resultados ----
    private JPanel cardAutorizar() {
        JPanel p = panelConTitulo("Resultados pendientes de autorizar");
        DefaultTableModel modelo = TemaUI.modeloNoEditable(
                new String[]{"ID", "Fecha turno", "Paciente", "Estudio", "Descripcion"});
        JTable tabla = new JTable(modelo);
        Runnable recargar = () -> {
            modelo.setRowCount(0);
            for (Resultado r : medicoServicio.listarResultadosPendientes(usuario.getId())) {
                modelo.addRow(new Object[]{r.getId(), r.getFechaTurno(), r.getNombrePaciente(),
                        r.getNombreEstudio(), r.getDescripcion()});
            }
        };
        recargar.run();

        JButton autorizar = TemaUI.botonAccion("Autorizar seleccionado");
        autorizar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) { aviso("Selecciona un resultado."); return; }
            int idResultado = (int) modelo.getValueAt(fila, 0);
            mostrarRespuesta(medicoServicio.autorizarResultado(idResultado, usuario.getId()));
            recargar.run();
        });

        p.add(TemaUI.envolverTabla(tabla), BorderLayout.CENTER);
        p.add(autorizar, BorderLayout.SOUTH);
        return p;
    }

    // ---- Cancelar turno ----
    private JPanel cardCancelar() {
        JPanel p = panelConTitulo("Cancelar turno de la agenda");
        DefaultTableModel modelo = TemaUI.modeloNoEditable(
                new String[]{"ID", "Fecha", "Hora", "Paciente", "Estudio"});
        JTable tabla = new JTable(modelo);
        Runnable recargar = () -> {
            modelo.setRowCount(0);
            for (Turno t : turnoServicio.listarAgendaMedico(usuario.getId())) {
                modelo.addRow(new Object[]{t.getId(), t.getFecha(), t.getHora(),
                        t.getNombrePaciente(), t.getNombreEstudio()});
            }
        };
        recargar.run();

        JTextField txtMotivo = TemaUI.campo();
        JButton cancelar = TemaUI.botonAccion("Cancelar seleccionado");
        cancelar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) { aviso("Selecciona un turno."); return; }
            if (Validaciones.vacio(txtMotivo.getText())) { aviso("Indica el motivo."); return; }
            int idTurno = (int) modelo.getValueAt(fila, 0);
            mostrarRespuesta(turnoServicio.cancelarTurno(idTurno, txtMotivo.getText().trim()));
            recargar.run();
        });

        JPanel sur = new JPanel(new BorderLayout(10, 8));
        sur.setOpaque(false);
        sur.add(TemaUI.fila("Motivo", txtMotivo), BorderLayout.CENTER);
        sur.add(cancelar, BorderLayout.SOUTH);
        p.add(TemaUI.envolverTabla(tabla), BorderLayout.CENTER);
        p.add(sur, BorderLayout.SOUTH);
        return p;
    }

    private JComboBox<Turno> comboTurnos(List<Turno> turnos) {
        JComboBox<Turno> cbo = new JComboBox<>();
        for (Turno t : turnos) cbo.addItem(t);
        cbo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean sel, boolean foco) {
                super.getListCellRendererComponent(list, value, index, sel, foco);
                if (value instanceof Turno) {
                    Turno t = (Turno) value;
                    setText("#" + t.getId() + "  " + t.getFecha() + " " + t.getHora()
                            + " - " + t.getNombrePaciente() + " - " + t.getNombreEstudio());
                }
                return this;
            }
        });
        return cbo;
    }

    private void aviso(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Atencion", JOptionPane.WARNING_MESSAGE);
    }

    private void mostrarRespuesta(Respuesta r) {
        JOptionPane.showMessageDialog(this, r.getMensaje(),
                r.isExito() ? "Listo" : "Error",
                r.isExito() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }
}
