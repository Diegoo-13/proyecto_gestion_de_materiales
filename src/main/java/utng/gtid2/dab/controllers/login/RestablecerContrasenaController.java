package utng.gtid2.dab.controllers.login;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import utng.gtid2.dab.util.Navegador;

public class RestablecerContrasenaController implements Initializable {

    @FXML
    private PasswordField pwdNuevaContrasena;

    @FXML
    private PasswordField pwdConfirmarContrasena;

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnGuardarContrasena;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    private void cancelar(ActionEvent event) throws IOException {

        // Regresa al Login
        Navegador.cerrar(btnCancelar);
    }

    @FXML
    private void guardarContrasena(ActionEvent event) throws IOException {

        // Después aquí irá la lógica para guardar la contraseña

        Navegador.cerrar(btnGuardarContrasena);
    }

}