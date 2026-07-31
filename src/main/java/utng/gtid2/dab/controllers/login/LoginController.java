package utng.gtid2.dab.controllers.login;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import utng.gtid2.dab.App;
import utng.gtid2.dab.util.Navegador;

public class LoginController implements Initializable {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField pwdContrasena;

    @FXML
    private Hyperlink hlOlvidasteContrasena;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    private void iniciarSesion(ActionEvent event) throws IOException {

        String usuario = txtUsuario.getText().trim();
        String contrasena = pwdContrasena.getText().trim();

        if (usuario.isEmpty() && contrasena.isEmpty()) {

            Alert alerta = new Alert(AlertType.WARNING);
            alerta.setTitle("Campos vacíos");
            alerta.setHeaderText(null);
            alerta.setContentText("Ingrese su usuario y contraseña.");
            alerta.showAndWait();
            return;

        }

        if (usuario.isEmpty()) {

            Alert alerta = new Alert(AlertType.WARNING);
            alerta.setTitle("Usuario requerido");
            alerta.setHeaderText(null);
            alerta.setContentText("Ingrese su usuario.");
            alerta.showAndWait();
            return;

        }

        if (contrasena.isEmpty()) {

            Alert alerta = new Alert(AlertType.WARNING);
            alerta.setTitle("Contraseña requerida");
            alerta.setHeaderText(null);
            alerta.setContentText("Ingrese su contraseña.");
            alerta.showAndWait();
            return;

        }

        // Por ahora cualquier usuario y contraseña permiten entrar
        App.setRoot("inicio/Inicio");
    }

    @FXML
    private void recuperarContrasena(ActionEvent event) throws IOException {

        Navegador.abrirModal(
        "login/RecuperarContrasena",
        "Recuperar contraseña");

    }

}