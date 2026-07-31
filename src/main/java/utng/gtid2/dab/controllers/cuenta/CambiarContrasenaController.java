package utng.gtid2.dab.controllers.cuenta;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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

        // Aquí posteriormente se enviará el código al correo.

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

        // Aquí posteriormente se validará:
        // - Código correcto
        // - Contraseñas iguales
        // - Actualizar contraseña en PostgreSQL

        Navegador.cerrar(btnGuardarCambios);

    }

}