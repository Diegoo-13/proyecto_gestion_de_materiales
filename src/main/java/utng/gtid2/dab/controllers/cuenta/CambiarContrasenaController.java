package utng.gtid2.dab.controllers.cuenta;
//holisssssssssssss
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import utng.gtid2.dab.dao.UsuarioDAO;
import utng.gtid2.dab.util.CorreoUtil;
import utng.gtid2.dab.util.Navegador;

public class CambiarContrasenaController implements Initializable {

    @FXML
    private Button btnCerrar;

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnGuardarCambios;

    @FXML
    private Button btnEnviarCodigo;

    @FXML
    private Button btnVer;

    @FXML
    private Button btnVer1;

    @FXML
    private TextField txtCorreoInstitucional;

    @FXML
    private TextField txtCodigoVerificacion;

    @FXML
    private PasswordField pwdNuevaContrasena;

    @FXML
    private PasswordField pwdConfirmarContrasena;

    @FXML
    private TextField txtNuevaContrasena;

    @FXML
    private TextField txtConfirmarContrasena;

    private boolean mostrarNueva = false;
    private boolean mostrarConfirmacion = false;

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    // Código generado para esta ventana
    private static String codigoVerificacion = "";

    // Correo al que se envió el código
    private static String correoSolicitado = "";

    // Momento en que se generó el código
    private static long momentoGeneracionCodigo = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Mantiene sincronizados ambos campos
        txtNuevaContrasena.textProperty()
                .bindBidirectional(pwdNuevaContrasena.textProperty());

        txtConfirmarContrasena.textProperty()
                .bindBidirectional(pwdConfirmarContrasena.textProperty());
    }

    @FXML
    private void cancelar(ActionEvent event) {

        Navegador.cerrar(btnCancelar);
    }

    @FXML
    private void enviarCodigo(ActionEvent event) {

        String correo = txtCorreoInstitucional.getText().trim();

        // ==========================================
        // VALIDAR CORREO
        // ==========================================

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

        // ==========================================
        // VALIDAR CORREO EN LA BASE DE DATOS
        // ==========================================

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

        // ==========================================
        // GENERAR CÓDIGO DE 6 DÍGITOS
        // ==========================================

        Random random = new Random();

        codigoVerificacion = String.format(
                "%06d",
                random.nextInt(1000000)
        );

        // Guardar momento de generación
        momentoGeneracionCodigo = System.currentTimeMillis();

        // ==========================================
        // ENVIAR CÓDIGO POR CORREO
        // ==========================================

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

            codigoVerificacion = "";
            momentoGeneracionCodigo = 0;

            return;
        }

        Alert alerta = new Alert(AlertType.INFORMATION);
        alerta.setTitle("Código enviado");
        alerta.setHeaderText(null);
        alerta.setContentText(
                "El código de verificación fue enviado "
                + "a tu correo institucional."
        );
        alerta.showAndWait();
    }

    @FXML
    private void mostrarContrasena(ActionEvent event) {

        mostrarNueva = !mostrarNueva;

        pwdNuevaContrasena.setVisible(!mostrarNueva);
        pwdNuevaContrasena.setManaged(!mostrarNueva);

        txtNuevaContrasena.setVisible(mostrarNueva);
        txtNuevaContrasena.setManaged(mostrarNueva);
    }

    @FXML
    private void mostrarConfirmacion(ActionEvent event) {

        mostrarConfirmacion = !mostrarConfirmacion;

        pwdConfirmarContrasena.setVisible(!mostrarConfirmacion);
        pwdConfirmarContrasena.setManaged(!mostrarConfirmacion);

        txtConfirmarContrasena.setVisible(mostrarConfirmacion);
        txtConfirmarContrasena.setManaged(mostrarConfirmacion);
    }

    @FXML
    private void guardarCambios(ActionEvent event) {

        String correo = txtCorreoInstitucional.getText().trim();
        String codigoIngresado = txtCodigoVerificacion.getText().trim();

        String nuevaPass = pwdNuevaContrasena.getText().trim();
        String confirmarPass = pwdConfirmarContrasena.getText().trim();

        // ==========================================
        // VALIDAR CORREO
        // ==========================================

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

        // ==========================================
        // VALIDAR CÓDIGO
        // ==========================================

        if (codigoIngresado.isEmpty()) {

            Alert alerta = new Alert(AlertType.WARNING);
            alerta.setTitle("Código requerido");
            alerta.setHeaderText(null);
            alerta.setContentText(
                    "Por favor, ingrese el código de verificación."
            );
            alerta.showAndWait();

            return;
        }

        // ==========================================
        // VERIFICAR QUE SE HAYA SOLICITADO CÓDIGO
        // ==========================================

        if (codigoVerificacion.isEmpty()
                || correoSolicitado.isEmpty()
                || momentoGeneracionCodigo == 0) {

            Alert alerta = new Alert(AlertType.WARNING);
            alerta.setTitle("Código no solicitado");
            alerta.setHeaderText(null);
            alerta.setContentText(
                    "Primero debes solicitar un código de verificación."
            );
            alerta.showAndWait();

            return;
        }

        // ==========================================
        // VERIFICAR QUE EL CORREO SEA EL MISMO
        // ==========================================

        if (!correo.equalsIgnoreCase(correoSolicitado)) {

            Alert alerta = new Alert(AlertType.WARNING);
            alerta.setTitle("Correo diferente");
            alerta.setHeaderText(null);
            alerta.setContentText(
                    "El correo ingresado no coincide con el "
                    + "correo al que se envió el código."
            );
            alerta.showAndWait();

            return;
        }

        // ==========================================
        // VALIDAR EXPIRACIÓN DE 15 MINUTOS
        // ==========================================

        long tiempoActual = System.currentTimeMillis();

        long tiempoTranscurrido =
                tiempoActual - momentoGeneracionCodigo;

        long quinceMinutos = 15 * 60 * 1000;

        if (tiempoTranscurrido > quinceMinutos) {

            Alert alerta = new Alert(AlertType.WARNING);
            alerta.setTitle("Código expirado");
            alerta.setHeaderText(null);
            alerta.setContentText(
                    "El código de verificación ha expirado. "
                    + "Solicita un nuevo código."
            );
            alerta.showAndWait();

            codigoVerificacion = "";
            correoSolicitado = "";
            momentoGeneracionCodigo = 0;

            return;
        }

        // ==========================================
        // VALIDAR CÓDIGO
        // ==========================================

        if (!codigoIngresado.equals(codigoVerificacion)) {

            Alert alerta = new Alert(AlertType.ERROR);
            alerta.setTitle("Código incorrecto");
            alerta.setHeaderText(null);
            alerta.setContentText(
                    "El código de verificación no es correcto."
            );
            alerta.showAndWait();

            return;
        }

        // ==========================================
        // VALIDAR CONTRASEÑAS
        // ==========================================

        if (nuevaPass.isEmpty() || confirmarPass.isEmpty()) {

            Alert alerta = new Alert(AlertType.WARNING);
            alerta.setTitle("Campos vacíos");
            alerta.setHeaderText(null);
            alerta.setContentText(
                    "Por favor, ingrese y confirme la nueva contraseña."
            );
            alerta.showAndWait();

            return;
        }

        if (nuevaPass.length() < 8) {

            Alert alerta = new Alert(AlertType.WARNING);
            alerta.setTitle("Contraseña inválida");
            alerta.setHeaderText(null);
            alerta.setContentText(
                    "La contraseña debe tener mínimo 8 caracteres."
            );
            alerta.showAndWait();

            return;
        }

        if (!nuevaPass.equals(confirmarPass)) {

            Alert alerta = new Alert(AlertType.WARNING);
            alerta.setTitle("Las contraseñas no coinciden");
            alerta.setHeaderText(null);
            alerta.setContentText(
                    "La nueva contraseña y su confirmación deben ser iguales."
            );
            alerta.showAndWait();

            return;
        }

        // ==========================================
        // ACTUALIZAR CONTRASEÑA EN POSTGRESQL
        // ==========================================

        boolean exito =
                usuarioDAO.actualizarContrasenaPorCorreo(
                        correo,
                        nuevaPass
                );

        if (exito) {

            Alert alerta = new Alert(AlertType.INFORMATION);
            alerta.setTitle("Éxito");
            alerta.setHeaderText(null);
            alerta.setContentText(
                    "Contraseña actualizada correctamente."
            );
            alerta.showAndWait();

            // ======================================
            // INVALIDAR EL CÓDIGO
            // ======================================

            codigoVerificacion = "";
            correoSolicitado = "";
            momentoGeneracionCodigo = 0;

            Navegador.cerrar(btnGuardarCambios);

        } else {

            Alert alerta = new Alert(AlertType.ERROR);
            alerta.setTitle("Error");
            alerta.setHeaderText(null);
            alerta.setContentText(
                    "No se pudo actualizar la contraseña."
            );
            alerta.showAndWait();
        }
    }
}