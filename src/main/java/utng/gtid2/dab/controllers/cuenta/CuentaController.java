package utng.gtid2.dab.controllers.cuenta;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import utng.gtid2.dab.App;
import utng.gtid2.dab.modelo.Usuario;
import utng.gtid2.dab.util.Navegador;
import utng.gtid2.dab.util.RelojSistema;
import utng.gtid2.dab.util.Sesion;

public class CuentaController implements Initializable {

    //================ MENÚ =================

    @FXML
    private Button btnInicio;

    @FXML
    private Button btnMaterialesRegistrados;

    @FXML
    private Button btnPrestamosActivos;

    @FXML
    private Button btnMaterialesDanados;

    @FXML
    private Button btnReportes;

    @FXML
    private Button btnUsuarios;

    @FXML
    private Button btnCuenta;

    //================ INFORMACIÓN =================

    @FXML
    private Label lblHora;

    @FXML
    private Label lblFecha;

    @FXML
    private Label lblNombreCompleto;

    @FXML
    private Label lblUsuario;

    @FXML
    private Label lblCorreoInstitucional;

    @FXML
    private Label lblRol;

    @FXML
    private Label lblEstado;

    @FXML
    private Label lblFechaDeCreacionDeLaCuenta;

    @FXML
    private Label lblUltimoInicioDeSesion;

    //================ BOTONES =================

    @FXML
    private Button btnCambiarContrasena;

    @FXML
    private Button btnCerrarSesion;

    @Override
public void initialize(URL url, ResourceBundle rb) {

    //============== FECHA Y HORA ====================
    RelojSistema.iniciar(lblHora, lblFecha);

    //============== INFORMACIÓN DEL USUARIO ====================

    Usuario usuario = Sesion.getUsuarioActual();

    if (usuario != null) {

        String nombreCompleto =
                usuario.getNombre() + " " +
                usuario.getApellidoP() + " " +
                usuario.getApellidoM();

        lblNombreCompleto.setText(nombreCompleto);
        lblUsuario.setText(usuario.getNomUsuario());
        lblCorreoInstitucional.setText(usuario.getCorreo());
        lblRol.setText(usuario.getRol());
        lblEstado.setText(usuario.getEstado());

        // Fecha de creación
        if (usuario.getFechaCreacion() != null) {
            lblFechaDeCreacionDeLaCuenta.setText(
                    usuario.getFechaCreacion().toString()
            );
        } else {
            lblFechaDeCreacionDeLaCuenta.setText("No disponible");
        }

        // Último inicio de sesión
        if (usuario.getUltimoInicioSesion() != null) {
            lblUltimoInicioDeSesion.setText(
                    usuario.getUltimoInicioSesion()
                            .format(
                                java.time.format.DateTimeFormatter
                                    .ofPattern("dd/MM/yyyy HH:mm")
                            )
            );
        } else {
            lblUltimoInicioDeSesion.setText("No disponible");
        }

    } else {

        lblNombreCompleto.setText("No disponible");
        lblUsuario.setText("No disponible");
        lblCorreoInstitucional.setText("No disponible");
        lblRol.setText("No disponible");
        lblEstado.setText("No disponible");
        lblFechaDeCreacionDeLaCuenta.setText("No disponible");
        lblUltimoInicioDeSesion.setText("No disponible");
    }
}

    //================ NAVEGACIÓN =================

    @FXML
    private void inicio(ActionEvent event) throws IOException {

        App.setRoot("inicio/Inicio");

    }

    @FXML
    private void materialesRegistrados(ActionEvent event) throws IOException {

        App.setRoot("materiales/MaterialesRegistrados");

    }

    @FXML
    private void prestamosActivos(ActionEvent event) throws IOException {

        App.setRoot("prestamos/PrestamosActivos");

    }

    @FXML
    private void materialesDanados(ActionEvent event) throws IOException {

        App.setRoot("danos/MaterialesDanados");

    }

    @FXML
    private void reportes(ActionEvent event) throws IOException {

        App.setRoot("reportes/Reportes");

    }

    @FXML
    private void usuarios(ActionEvent event) throws IOException {

        App.setRoot("usuarios/Usuarios");

    }

    @FXML
    private void cuenta(ActionEvent event) {

        // Ya estamos aquí.

    }

    //================ BOTONES =================

    @FXML
    private void abrirCambiarContrasena(ActionEvent event) throws IOException {

        Navegador.abrirModal(
                "cuenta/CambiarContrasena",
                "Cambiar contraseña"
        );

    }

    @FXML
    private void abrirCerrarSesion(ActionEvent event) throws IOException {

        Navegador.abrirModal(
                "cuenta/CerrarSesion",
                "Cerrar sesión"
        );

    }

}