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
import utng.gtid2.dab.dao.UsuarioDAO;
import utng.gtid2.dab.modelo.Usuario;
import utng.gtid2.dab.util.Navegador;
import utng.gtid2.dab.util.Sesion;

public class LoginController implements Initializable {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField pwdContrasena;

    @FXML
    private Hyperlink hlOlvidasteContrasena;

    // Instancia del DAO para conectar con la BD
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Enter en usuario → pasar al campo contraseña
        txtUsuario.setOnAction(event -> pwdContrasena.requestFocus());

        // Enter en contraseña → iniciar sesión
        pwdContrasena.setOnAction(event -> {
            try {
                iniciarSesion(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void iniciarSesion(ActionEvent event) throws IOException {

        String usuario = txtUsuario.getText().trim();

        // No usar trim() en contraseñas
        String contrasena = pwdContrasena.getText();

        // Validar campos vacíos
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

        // Obtener el usuario autenticado desde PostgreSQL
        Usuario usuarioAutenticado =
                usuarioDAO.obtenerUsuarioAutenticado(usuario, contrasena);

        if (usuarioAutenticado != null) {

            // Guardar el usuario que inició sesión
            Sesion.iniciarSesion(usuarioAutenticado);

            // Entrar al sistema
            App.setRoot("inicio/Inicio");

        } else {

            Alert alerta = new Alert(AlertType.ERROR);
            alerta.setTitle("Error de autenticación");
            alerta.setHeaderText(null);
            alerta.setContentText(
                    "Usuario o contraseña incorrectos, o el usuario está inactivo."
            );
            alerta.showAndWait();
        }
    }

    @FXML
    private void recuperarContrasena(ActionEvent event) throws IOException {

        Navegador.abrirModal(
                "login/RecuperarContrasena",
                "Recuperar contraseña"
        );
    }
}