package utng.gtid2.dab.controllers.login;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import utng.gtid2.dab.dao.UsuarioDAO;
import utng.gtid2.dab.util.CorreoUtil;
import utng.gtid2.dab.util.Navegador;

public class RecuperarContrasenaController implements Initializable {

    @FXML
    private TextField txtCorreoInstitucional;

    @FXML
    private Button btnEnviarCodigo;

    @FXML
    private Button btnCancelar;

    // Correo que solicitó recuperar la contraseña
    public static String correoSolicitado = "";

    // Código de verificación
    public static String codigoVerificacion = "";

    // Momento en que se generó el código
    public static long momentoGeneracionCodigo = 0;

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    private void enviarCodigo(ActionEvent event) throws IOException {

        String correo = txtCorreoInstitucional.getText().trim();

        if (correo.isEmpty()) {

            Alert alerta = new Alert(AlertType.WARNING);
            alerta.setTitle("Correo requerido");
            alerta.setHeaderText(null);
            alerta.setContentText(
                    "Por favor, ingrese su correo institucional."
            );
            alerta.showAndWait();

            return;
        }

        // Validar correo contra PostgreSQL
        if (!usuarioDAO.existeCorreo(correo)) {

            Alert alerta = new Alert(AlertType.WARNING);
            alerta.setTitle("Correo no registrado");
            alerta.setHeaderText(null);
            alerta.setContentText(
                    "El correo ingresado no está registrado en el sistema."
            );
            alerta.showAndWait();

            return;
        }

        // Guardar correo
        correoSolicitado = correo;

        // Generar código aleatorio de 6 dígitos
        Random random = new Random();

        codigoVerificacion = String.format(
                "%06d",
                random.nextInt(1000000)
        );

        // Guardar el momento exacto de generación
        momentoGeneracionCodigo = System.currentTimeMillis();

        // Enviar código por correo
        boolean enviado = CorreoUtil.enviarCodigo(
                correoSolicitado,
                codigoVerificacion
        );

        if (!enviado) {

            Alert alerta = new Alert(AlertType.ERROR);
            alerta.setTitle("Error al enviar código");
            alerta.setHeaderText(null);
            alerta.setContentText(
                    "No se pudo enviar el código al correo."
            );
            alerta.showAndWait();

            // Limpiar el código si el correo no pudo enviarse
            codigoVerificacion = "";
            momentoGeneracionCodigo = 0;

            return;
        }

        // Cerrar ventana actual
        Navegador.cerrar(btnEnviarCodigo);

        // Mostrar confirmación
        Navegador.abrirModal(
                "login/CodigoEnviado",
                "Código enviado"
        );
    }

    @FXML
    private void cancelar(ActionEvent event) throws IOException {

        Navegador.cerrar(btnCancelar);

    }
}