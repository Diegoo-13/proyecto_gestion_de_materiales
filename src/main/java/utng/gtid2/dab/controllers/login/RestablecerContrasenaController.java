package utng.gtid2.dab.controllers.login;

import java.io.IOException;
import java.net.URL;
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
import utng.gtid2.dab.util.Navegador;

public class RestablecerContrasenaController implements Initializable {

    @FXML
    private TextField txtCodigoVerificacion;

    @FXML
    private PasswordField pwdNuevaContrasena;

    @FXML
    private PasswordField pwdConfirmarContrasena;

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnGuardarContrasena;

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    private void cancelar(ActionEvent event) throws IOException {

        Navegador.cerrar(btnCancelar);

    }

    @FXML
    private void guardarContrasena(ActionEvent event) throws IOException {

        String codigoIngresado =
                txtCodigoVerificacion.getText().trim();

        // No usar trim() en contraseñas para conservar
        // exactamente los caracteres ingresados por el usuario.
        String nuevaPass =
                pwdNuevaContrasena.getText();

        String confirmarPass =
                pwdConfirmarContrasena.getText();

        // ==========================================
        // VALIDAR QUE SE HAYA INGRESADO EL CÓDIGO
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
        // VALIDAR EXPIRACIÓN DEL CÓDIGO
        // ==========================================

        long tiempoActual = System.currentTimeMillis();

        long tiempoTranscurrido =
                tiempoActual
                - RecuperarContrasenaController.momentoGeneracionCodigo;

        // 15 minutos expresados en milisegundos
        long quinceMinutos = 15 * 60 * 1000;

        if (tiempoTranscurrido > quinceMinutos) {

            Alert alerta = new Alert(AlertType.WARNING);
            alerta.setTitle("Código expirado");
            alerta.setHeaderText(null);
            alerta.setContentText(
                    "El código de verificación ha expirado. "
                    + "Solicite un nuevo código."
            );
            alerta.showAndWait();

            // Invalidar el código
            RecuperarContrasenaController.codigoVerificacion = "";
            RecuperarContrasenaController.momentoGeneracionCodigo = 0;

            return;
        }

        // ==========================================
        // VALIDAR CÓDIGO
        // ==========================================

        if (!codigoIngresado.equals(
                RecuperarContrasenaController.codigoVerificacion)) {

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

        String correo =
                RecuperarContrasenaController.correoSolicitado;

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
                    "Contraseña actualizada correctamente. "
                    + "Ya puedes iniciar sesión."
            );
            alerta.showAndWait();

            // ======================================
            // INVALIDAR EL CÓDIGO DESPUÉS DE USARLO
            // ======================================

            RecuperarContrasenaController.codigoVerificacion = "";
            RecuperarContrasenaController.momentoGeneracionCodigo = 0;
            RecuperarContrasenaController.correoSolicitado = "";

            Navegador.cerrar(btnGuardarContrasena);

        } else {

            Alert alerta = new Alert(AlertType.ERROR);
            alerta.setTitle("Error");
            alerta.setHeaderText(null);
            alerta.setContentText(
                    "Ocurrió un error al actualizar "
                    + "la contraseña en la base de datos."
            );
            alerta.showAndWait();
        }
    }
}