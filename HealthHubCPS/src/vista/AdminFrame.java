package vista;

import modelo.*;
import servicio.CatalogoServicio;
import servicio.Respuesta;
import servicio.UsuarioServicio;
import util.TemaUI;
import util.Validaciones;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AdminFrame extends VentanaBase {

    private final UsuarioServicio usuarioServicio = new UsuarioServicio();
    private final CatalogoServicio catalogoServicio = new CatalogoServicio();

    public AdminFrame(Usuario usuario) {
        super(usuario, "Administrador");

        agregarTarjeta("registrar", cardRegistrar());
        agregarTarjeta("estado", cardEstado());
        agregarTarjeta("consultorios", cardConsultorios());
        agregarTarjeta("estudios", cardEstudios());
        agregarTarjeta("coberturas", cardCoberturas());

        agregarOpcion("Registrar usuario", "registrar");
        agregarOpcion("Gestionar estado", "estado");
        agregarOpcion("Consultorios por medico", "consultorios");
        agregarOpcion("Tarifas y tiempos", "estudios");
        agregarOpcion("Coberturas", "coberturas");

        mostrar("registrar");
    }


    private JPanel cardRegistrar() {
        JComboBox<Rol> cboRol = new JComboBox<>(Rol.values());
        JTextField txtDni = TemaUI.campo();
        JPasswordField txtPass = new JPasswordField();
        txtPass.setFont(TemaUI.NORMAL);
        JTextField txtNombre = TemaUI.campo();
        JTextField txtApellido = TemaUI.campo();
        JTextField txtEmail = TemaUI.campo();
        JTextField txtTel = TemaUI.campo();
        JTextField txtMatricula = TemaUI.campo();
        JComboBox<Especialidad> cboEsp = new JComboBox<>();
        for (Especialidad esp : catalogoServicio.listarEspecialidades()) cboEsp.addItem(esp);


        Runnable ajustar = () -> {
            boolean esMedico = cboRol.getSelectedItem() == Rol.MEDICO;
            txtMatricula.setEnabled(esMedico);
            cboEsp.setEnabled(esMedico);
        };
        cboRol.addItemListener(e -> ajustar.run());
        ajustar.run();

        JButton registrar = TemaUI.botonAccion("Registrar");
        registrar.addActionListener(e -> {
            Rol rol = (Rol) cboRol.getSelectedItem();
            String dni = txtDni.getText().trim();
            String pass = new String(txtPass.getPassword());
            if (!Validaciones.soloDigitos(dni)) { aviso("El DNI debe ser numerico."); return; }
            if (Validaciones.vacio(pass)) { aviso("Ingresa una contrasena."); return; }
            if (Validaciones.vacio(txtNombre.getText()) || Validaciones.vacio(txtApellido.getText())) {
                aviso("Nombre y apellido son obligatorios."); return;
            }
            if (!Validaciones.vacio(txtEmail.getText()) && !Validaciones.emailValido(txtEmail.getText())) {
                aviso("Email invalido."); return;
            }

            Respuesta r;
            if (rol == Rol.MEDICO) {
                if (Validaciones.vacio(txtMatricula.getText())) { aviso("Ingresa la matricula."); return; }
                Especialidad esp = (Especialidad) cboEsp.getSelectedItem();
                if (esp == null) { aviso("Selecciona una especialidad."); return; }
                Medico m = new Medico();
                cargarDatos(m, dni, pass, txtNombre, txtApellido, txtEmail, txtTel, rol);
                m.setMatricula(txtMatricula.getText().trim());
                m.setIdEspecialidad(esp.getId());
                r = usuarioServicio.registrarMedico(m);
            } else {
                Usuario u = new Usuario();
                cargarDatos(u, dni, pass, txtNombre, txtApellido, txtEmail, txtTel, rol);
                r = usuarioServicio.registrarUsuario(u);
            }
            mostrarRespuesta(r);
        });

        return panelFormulario("Registrar usuario", new JComponent[]{
                TemaUI.fila("Rol", cboRol),
                TemaUI.fila("DNI", txtDni),
                TemaUI.fila("Contrasena", txtPass),
                TemaUI.fila("Nombre", txtNombre),
                TemaUI.fila("Apellido", txtApellido),
                TemaUI.fila("Email", txtEmail),
                TemaUI.fila("Telefono", txtTel),
                TemaUI.fila("Matricula (medico)", txtMatricula),
                TemaUI.fila("Especialidad (medico)", cboEsp)
        }, registrar);
    }

    private void cargarDatos(Usuario u, String dni, String pass, JTextField nombre, JTextField apellido,
                             JTextField email, JTextField tel, Rol rol) {
        u.setDni(dni);
        u.setContrasenia(pass);
        u.setNombre(nombre.getText().trim());
        u.setApellido(apellido.getText().trim());
        u.setEmail(email.getText().trim());
        u.setTelefono(tel.getText().trim());
        u.setRol(rol);
    }


    private JPanel cardEstado() {
        JPanel p = panelConTitulo("Gestionar estado de usuarios");
        DefaultTableModel modelo = TemaUI.modeloNoEditable(
                new String[]{"ID", "DNI", "Nombre", "Apellido", "Rol", "Activo"});
        JTable tabla = new JTable(modelo);
        Runnable recargar = () -> {
            modelo.setRowCount(0);
            for (Usuario u : usuarioServicio.listarUsuarios()) {
                modelo.addRow(new Object[]{u.getId(), u.getDni(), u.getNombre(),
                        u.getApellido(), u.getRol(), u.isActivo() ? "Si" : "No"});
            }
        };
        recargar.run();

        JButton activar = TemaUI.botonAccion("Activar");
        JButton desactivar = TemaUI.botonAccion("Desactivar");
        activar.addActionListener(e -> cambiarEstado(tabla, modelo, true, recargar));
        desactivar.addActionListener(e -> cambiarEstado(tabla, modelo, false, recargar));

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        sur.setOpaque(false);
        sur.add(activar);
        sur.add(desactivar);

        p.add(TemaUI.envolverTabla(tabla), BorderLayout.CENTER);
        p.add(sur, BorderLayout.SOUTH);
        return p;
    }

    private void cambiarEstado(JTable tabla, DefaultTableModel modelo, boolean activo, Runnable recargar) {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { aviso("Selecciona un usuario."); return; }
        int id = (int) modelo.getValueAt(fila, 0);
        mostrarRespuesta(usuarioServicio.cambiarEstado(id, activo));
        recargar.run();
    }

    private JPanel cardConsultorios() {
        JPanel p = panelConTitulo("Consultorios por medico (proximos 30 dias)");
        JComboBox<Medico> cboMedico = new JComboBox<>();
        for (Medico m : catalogoServicio.listarMedicosActivos()) cboMedico.addItem(m);
        DefaultTableModel modelo = TemaUI.modeloNoEditable(
                new String[]{"ID", "Numero", "Ubicacion"});
        JTable tabla = new JTable(modelo);

        JButton ver = TemaUI.botonAccion("Ver consultorios");
        ver.addActionListener(e -> {
            Medico m = (Medico) cboMedico.getSelectedItem();
            if (m == null) { aviso("Selecciona un medico."); return; }
            modelo.setRowCount(0);
            for (Consultorio c : catalogoServicio.consultoriosDeMedico(m.getId(), 30)) {
                modelo.addRow(new Object[]{c.getId(), c.getNumero(), c.getUbicacion()});
            }
            if (modelo.getRowCount() == 0) {
                aviso("Ese medico no tiene turnos con consultorio asignado en los proximos 30 dias.");
            }
        });

        cboMedico.setPreferredSize(new Dimension(280, 30));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        top.setOpaque(false);
        top.add(TemaUI.etiqueta("Medico:"));
        top.add(cboMedico);
        top.add(ver);

        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.setOpaque(false);
        centro.add(top, BorderLayout.NORTH);
        centro.add(TemaUI.envolverTabla(tabla), BorderLayout.CENTER);
        p.add(centro, BorderLayout.CENTER);
        return p;
    }

    private JPanel cardEstudios() {
        JPanel p = panelConTitulo("Tarifas y tiempos de estudios");
        DefaultTableModel modelo = TemaUI.modeloNoEditable(
                new String[]{"ID", "Nombre", "Complejidad", "Minutos", "Tarifa"});
        JTable tabla = new JTable(modelo);
        Runnable recargar = () -> {
            modelo.setRowCount(0);
            for (TipoEstudio t : catalogoServicio.listarTiposEstudio()) {
                modelo.addRow(new Object[]{t.getId(), t.getNombre(), t.getComplejidad(),
                        t.getTiempoMinutos(), "$" + t.getTarifaBase()});
            }
        };
        recargar.run();

        JTextField txtTarifa = TemaUI.campo();
        JTextField txtMinutos = TemaUI.campo();
        JButton btnTarifa = TemaUI.botonAccion("Actualizar tarifa");
        JButton btnTiempo = TemaUI.botonAccion("Actualizar tiempo");

        btnTarifa.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) { aviso("Selecciona un estudio."); return; }
            Double tarifa = Validaciones.parsearDecimal(txtTarifa.getText());
            if (tarifa == null) { aviso("Tarifa invalida."); return; }
            int id = (int) modelo.getValueAt(fila, 0);
            mostrarRespuesta(catalogoServicio.actualizarTarifa(id, tarifa));
            recargar.run();
        });
        btnTiempo.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) { aviso("Selecciona un estudio."); return; }
            Integer min = Validaciones.parsearEntero(txtMinutos.getText());
            if (min == null) { aviso("Minutos invalidos."); return; }
            int id = (int) modelo.getValueAt(fila, 0);
            mostrarRespuesta(catalogoServicio.actualizarTiempo(id, min));
            recargar.run();
        });

        JPanel sur = new JPanel();
        sur.setLayout(new BoxLayout(sur, BoxLayout.Y_AXIS));
        sur.setOpaque(false);
        JPanel f1 = TemaUI.fila("Nueva tarifa", txtTarifa);
        f1.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel f2 = TemaUI.fila("Nuevos minutos", txtMinutos);
        f2.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        botones.setOpaque(false);
        botones.setAlignmentX(Component.LEFT_ALIGNMENT);
        botones.add(btnTarifa);
        botones.add(btnTiempo);
        sur.add(f1);
        sur.add(Box.createVerticalStrut(6));
        sur.add(f2);
        sur.add(Box.createVerticalStrut(8));
        sur.add(botones);

        p.add(TemaUI.envolverTabla(tabla), BorderLayout.CENTER);
        p.add(sur, BorderLayout.SOUTH);
        return p;
    }


    private JPanel cardCoberturas() {
        JPanel p = panelConTitulo("Coberturas (obra social x estudio)");
        DefaultTableModel modelo = TemaUI.modeloNoEditable(
                new String[]{"id_obra", "Obra social", "id_estudio", "Estudio", "% Cobertura", "Vigente"});
        JTable tabla = new JTable(modelo);
        Runnable recargar = () -> {
            modelo.setRowCount(0);
            for (Cobertura c : catalogoServicio.listarCoberturas()) {
                modelo.addRow(new Object[]{c.getIdObraSocial(), c.getNombreObraSocial(),
                        c.getIdTipoEstudio(), c.getNombreTipoEstudio(),
                        c.getPorcentajeCobertura(), c.isVigente() ? "Si" : "No"});
            }
        };
        recargar.run();

        JTextField txtPorc = TemaUI.campo();
        JButton actualizar = TemaUI.botonAccion("Actualizar % seleccionado");
        actualizar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) { aviso("Selecciona una cobertura."); return; }
            Double porc = Validaciones.parsearDecimal(txtPorc.getText());
            if (porc == null) { aviso("Porcentaje invalido."); return; }
            int idObra = (int) modelo.getValueAt(fila, 0);
            int idEstudio = (int) modelo.getValueAt(fila, 2);
            mostrarRespuesta(catalogoServicio.actualizarCobertura(idObra, idEstudio, porc));
            recargar.run();
        });

        JPanel sur = new JPanel(new BorderLayout(10, 8));
        sur.setOpaque(false);
        sur.add(TemaUI.fila("Nuevo %", txtPorc), BorderLayout.CENTER);
        sur.add(actualizar, BorderLayout.SOUTH);
        p.add(TemaUI.envolverTabla(tabla), BorderLayout.CENTER);
        p.add(sur, BorderLayout.SOUTH);
        return p;
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