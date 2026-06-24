package vista;

import modelo.*;
import servicio.CatalogoServicio;
import servicio.PacienteServicio;
import servicio.Respuesta;
import servicio.TurnoServicio;
import util.TemaUI;
import util.Validaciones;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class PacienteFrame extends VentanaBase {

    private final PacienteServicio pacienteServicio = new PacienteServicio();
    private final TurnoServicio turnoServicio = new TurnoServicio();
    private final CatalogoServicio catalogoServicio = new CatalogoServicio();

    public PacienteFrame(Usuario usuario) {
        super(usuario, "Paciente");

        agregarTarjeta("datos", cardDatos());
        agregarTarjeta("contacto", cardContacto());
        agregarTarjeta("solicitar", cardSolicitar());
        agregarTarjeta("misTurnos", cardMisTurnos());
        agregarTarjeta("cancelar", cardCancelar());
        agregarTarjeta("resultados", cardResultados());

        agregarOpcion("Completar / editar datos", "datos");
        agregarOpcion("Actualizar contacto", "contacto");
        agregarOpcion("Solicitar turno", "solicitar");
        agregarOpcion("Mis turnos", "misTurnos");
        agregarOpcion("Cancelar turno", "cancelar");
        agregarOpcion("Ver resultados", "resultados");

        mostrar("misTurnos");
    }

    private JPanel cardDatos() {
        JTextField txtFecha = TemaUI.campo();
        JTextField txtDomicilio = TemaUI.campo();
        JComboBox<ObraSocial> cboObra = new JComboBox<>();
        for (ObraSocial o : catalogoServicio.listarObrasSociales()) cboObra.addItem(o);

        Paciente actual = pacienteServicio.buscarDatos(usuario.getId());
        if (actual != null) {
            if (actual.getFechaNacimiento() != null) txtFecha.setText(actual.getFechaNacimiento().toString());
            txtDomicilio.setText(actual.getDomicilio());
            seleccionarObra(cboObra, actual.getIdObraSocial());
        }

        JButton guardar = TemaUI.botonAccion("Guardar");
        guardar.addActionListener(e -> {
            LocalDate fecha = Validaciones.parsearFecha(txtFecha.getText());
            if (fecha == null) {
                aviso("Fecha invalida. Usa el formato AAAA-MM-DD.");
                return;
            }
            if (Validaciones.vacio(txtDomicilio.getText())) {
                aviso("Ingresa el domicilio.");
                return;
            }
            ObraSocial os = (ObraSocial) cboObra.getSelectedItem();
            if (os == null) {
                aviso("Selecciona una obra social.");
                return;
            }
            Respuesta r = pacienteServicio.tieneDatos(usuario.getId())
                    ? pacienteServicio.actualizarDatos(usuario.getId(), fecha, txtDomicilio.getText().trim(), os.getId())
                    : pacienteServicio.completarDatos(usuario.getId(), fecha, txtDomicilio.getText().trim(), os.getId());
            mostrarRespuesta(r);
        });

        return panelFormulario("Datos del paciente", new JComponent[]{
                TemaUI.fila("Fecha nac. (AAAA-MM-DD)", txtFecha),
                TemaUI.fila("Domicilio", txtDomicilio),
                TemaUI.fila("Obra social", cboObra)
        }, guardar);
    }

    private JPanel cardContacto() {
        JTextField txtNombre = TemaUI.campo();
        JTextField txtApellido = TemaUI.campo();
        JTextField txtEmail = TemaUI.campo();
        JTextField txtTel = TemaUI.campo();
        txtNombre.setText(usuario.getNombre());
        txtApellido.setText(usuario.getApellido());
        txtEmail.setText(usuario.getEmail());
        txtTel.setText(usuario.getTelefono());

        JButton guardar = TemaUI.botonAccion("Actualizar");
        guardar.addActionListener(e -> {
            if (Validaciones.vacio(txtNombre.getText()) || Validaciones.vacio(txtApellido.getText())) {
                aviso("Nombre y apellido no pueden estar vacios.");
                return;
            }
            if (!Validaciones.vacio(txtEmail.getText()) && !Validaciones.emailValido(txtEmail.getText())) {
                aviso("El email no tiene un formato valido.");
                return;
            }
            usuario.setNombre(txtNombre.getText().trim());
            usuario.setApellido(txtApellido.getText().trim());
            usuario.setEmail(txtEmail.getText().trim());
            usuario.setTelefono(txtTel.getText().trim());
            mostrarRespuesta(pacienteServicio.actualizarContacto(usuario));
        });

        return panelFormulario("Datos de contacto", new JComponent[]{
                TemaUI.fila("Nombre", txtNombre),
                TemaUI.fila("Apellido", txtApellido),
                TemaUI.fila("Email", txtEmail),
                TemaUI.fila("Telefono", txtTel)
        }, guardar);
    }

    private JPanel cardSolicitar() {
        JComboBox<Medico> cboMedico = new JComboBox<>();
        for (Medico m : catalogoServicio.listarMedicosActivos()) cboMedico.addItem(m);
        JComboBox<TipoEstudio> cboEstudio = new JComboBox<>();
        for (TipoEstudio t : catalogoServicio.listarTiposEstudio()) cboEstudio.addItem(t);
        JTextField txtFecha = TemaUI.campo();
        JTextField txtHora = TemaUI.campo();
        JLabel lblMonto = TemaUI.etiqueta("Monto estimado: -");

        JButton calcular = TemaUI.botonAccion("Calcular monto");
        calcular.addActionListener(e -> {
            TipoEstudio te = (TipoEstudio) cboEstudio.getSelectedItem();
            if (te == null) { aviso("Selecciona un estudio."); return; }
            double monto = turnoServicio.calcularMonto(usuario.getId(), te.getId());
            lblMonto.setText("Monto estimado: $" + monto);
        });

        JButton solicitar = TemaUI.botonAccion("Solicitar turno");
        solicitar.addActionListener(e -> {
            Medico m = (Medico) cboMedico.getSelectedItem();
            TipoEstudio te = (TipoEstudio) cboEstudio.getSelectedItem();
            LocalDate fecha = Validaciones.parsearFecha(txtFecha.getText());
            LocalTime hora = Validaciones.parsearHora(txtHora.getText());
            if (m == null || te == null) { aviso("Selecciona medico y estudio."); return; }
            if (fecha == null) { aviso("Fecha invalida (AAAA-MM-DD)."); return; }
            if (hora == null) { aviso("Hora invalida (HH:MM)."); return; }
            mostrarRespuesta(turnoServicio.solicitarTurno(usuario.getId(), fecha, hora, m.getId(), te.getId()));
        });

        JPanel caja = new JPanel();
        caja.setLayout(new BoxLayout(caja, BoxLayout.Y_AXIS));
        caja.setBackground(TemaUI.GRIS_FONDO);
        JComponent[] filas = {
                TemaUI.fila("Medico", cboMedico),
                TemaUI.fila("Estudio", cboEstudio),
                TemaUI.fila("Fecha (AAAA-MM-DD)", txtFecha),
                TemaUI.fila("Hora (HH:MM)", txtHora),
                lblMonto
        };
        for (JComponent f : filas) {
            f.setAlignmentX(Component.LEFT_ALIGNMENT);
            caja.add(f);
            caja.add(Box.createVerticalStrut(10));
        }
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        acciones.setOpaque(false);
        acciones.setAlignmentX(Component.LEFT_ALIGNMENT);
        acciones.add(calcular);
        acciones.add(solicitar);
        caja.add(Box.createVerticalStrut(6));
        caja.add(acciones);

        JPanel p = panelConTitulo("Solicitar turno");
        p.add(caja, BorderLayout.CENTER);
        return p;
    }

    private JPanel cardMisTurnos() {
        JPanel p = panelConTitulo("Mis turnos");
        DefaultTableModel modelo = TemaUI.modeloNoEditable(
                new String[]{"Fecha", "Hora", "Medico", "Estudio", "Consultorio", "Estado", "Monto"});
        JTable tabla = new JTable(modelo);
        JButton refrescar = TemaUI.botonAccion("Actualizar");
        refrescar.addActionListener(e -> cargarTurnos(modelo, turnoServicio.listarTurnosPaciente(usuario.getId())));
        cargarTurnos(modelo, turnoServicio.listarTurnosPaciente(usuario.getId()));
        p.add(TemaUI.envolverTabla(tabla), BorderLayout.CENTER);
        p.add(refrescar, BorderLayout.SOUTH);
        return p;
    }

    private void cargarTurnos(DefaultTableModel modelo, List<Turno> turnos) {
        modelo.setRowCount(0);
        for (Turno t : turnos) {
            modelo.addRow(new Object[]{t.getFecha(), t.getHora(), t.getNombreMedico(),
                    t.getNombreEstudio(), t.getNumeroConsultorio(), t.getEstado(), "$" + t.getMontoFinal()});
        }
    }

    private JPanel cardCancelar() {
        JPanel p = panelConTitulo("Cancelar turno");
        DefaultTableModel modelo = TemaUI.modeloNoEditable(
                new String[]{"ID", "Fecha", "Hora", "Medico", "Estudio"});
        JTable tabla = new JTable(modelo);
        Runnable recargar = () -> {
            modelo.setRowCount(0);
            for (Turno t : turnoServicio.listarTurnosActivosPaciente(usuario.getId())) {
                modelo.addRow(new Object[]{t.getId(), t.getFecha(), t.getHora(),
                        t.getNombreMedico(), t.getNombreEstudio()});
            }
        };
        recargar.run();

        JTextField txtMotivo = TemaUI.campo();
        JButton cancelar = TemaUI.botonAccion("Cancelar seleccionado");
        cancelar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) { aviso("Selecciona un turno de la tabla."); return; }
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

    private JPanel cardResultados() {
        JPanel p = panelConTitulo("Resultados autorizados");
        DefaultTableModel modelo = TemaUI.modeloNoEditable(
                new String[]{"Fecha turno", "Estudio", "Medico", "Descripcion", "Autorizado"});
        JTable tabla = new JTable(modelo);
        JButton refrescar = TemaUI.botonAccion("Actualizar");
        Runnable recargar = () -> {
            modelo.setRowCount(0);
            for (Resultado r : pacienteServicio.listarResultados(usuario.getId())) {
                modelo.addRow(new Object[]{r.getFechaTurno(), r.getNombreEstudio(),
                        r.getNombreMedico(), r.getDescripcion(), r.getFechaAutorizacion()});
            }
        };
        refrescar.addActionListener(e -> recargar.run());
        recargar.run();
        p.add(TemaUI.envolverTabla(tabla), BorderLayout.CENTER);
        p.add(refrescar, BorderLayout.SOUTH);
        return p;
    }

    private void seleccionarObra(JComboBox<ObraSocial> cbo, int idObra) {
        for (int i = 0; i < cbo.getItemCount(); i++) {
            if (cbo.getItemAt(i).getId() == idObra) { cbo.setSelectedIndex(i); return; }
        }
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