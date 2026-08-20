package utng.gtid2.dab.controllers.login;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import utng.gtid2.dab.App;
import utng.gtid2.dab.dao.UsuarioDAO;
import utng.gtid2.dab.modelo.Usuario;
import utng.gtid2.dab.util.Navegador;
import utng.gtid2.dab.util.Sesion;

public class LoginController implements Initializable {

    @FXML
    private AnchorPane root;

    @FXML
    private Pane loginPane;

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

        // ==========================================
        // CENTRAR EL LOGIN
        // ==========================================

        root.widthProperty().addListener(
                (observable, oldValue, newValue) -> centrarLogin()
        );

        root.heightProperty().addListener(
                (observable, oldValue, newValue) -> centrarLogin()
        );

        // Centrar después de que JavaFX haya cargado
        // y calculado el tamaño real de la ventana.
        Platform.runLater(this::centrarLogin);

        // ==========================================
        // ENTER EN USUARIO
        // ==========================================

        txtUsuario.setOnAction(event -> {
            pwdContrasena.requestFocus();
        });

        // ==========================================
        // ENTER EN CONTRASEÑA
        // ==========================================

        pwdContrasena.setOnAction(event -> {

            try {

                iniciarSesion(event);

            } catch (IOException e) {

                e.printStackTrace();
            }
        });
    }

    // ==============================================
    // MÉTODO PARA CENTRAR EL FORMULARIO
    // ==============================================

    private void centrarLogin() {

        double anchoRoot = root.getWidth();
        double altoRoot = root.getHeight();

        double anchoLogin = loginPane.getWidth();
        double altoLogin = loginPane.getHeight();

        if (anchoRoot <= 0 || altoRoot <= 0) {
            return;
        }

        if (anchoLogin <= 0 || altoLogin <= 0) {
            return;
        }

        double posicionX =
                (anchoRoot - anchoLogin) / 2;

        double posicionY =
                (altoRoot - altoLogin) / 2;

        loginPane.setLayoutX(posicionX);
        loginPane.setLayoutY(posicionY);
    }

    // ==============================================
    // INICIAR SESIÓN
    // ==============================================

    @FXML
    private void iniciarSesion(ActionEvent event)
            throws IOException {

        String usuario =
                txtUsuario.getText().trim();

        // NO usar trim() en la contraseña.
        // Esto permite caracteres especiales y espacios.
        String contrasena =
                pwdContrasena.getText();

        // ==========================================
        // CAMPOS VACÍOS
        // ==========================================

        if (usuario.isEmpty() && contrasena.isEmpty()) {

            Alert alerta =
                    new Alert(AlertType.WARNING);

            alerta.setTitle("Campos vacíos");
            alerta.setHeaderText(null);
            alerta.setContentText(
                    "Ingrese su usuario y contraseña."
            );

            alerta.showAndWait();

            return;
        }

        if (usuario.isEmpty()) {

            Alert alerta =
                    new Alert(AlertType.WARNING);

            alerta.setTitle("Usuario requerido");
            alerta.setHeaderText(null);
            alerta.setContentText(
                    "Ingrese su usuario."
            );

            alerta.showAndWait();

            return;
        }

        if (contrasena.isEmpty()) {

            Alert alerta =
                    new Alert(AlertType.WARNING);

            alerta.setTitle("Contraseña requerida");
            alerta.setHeaderText(null);
            alerta.setContentText(
                    "Ingrese su contraseña."
            );

            alerta.showAndWait();

            return;
        }

        // ==========================================
        // AUTENTICAR USUARIO
        // ==========================================

        Usuario usuarioAutenticado =
                usuarioDAO.obtenerUsuarioAutenticado(
                        usuario,
                        contrasena
                );

        // ==========================================
        // LOGIN CORRECTO
        // ==========================================

        if (usuarioAutenticado != null) {

            // Guardar usuario en la sesión
            Sesion.iniciarSesion(
                    usuarioAutenticado
            );

            // Entrar al sistema
            App.setRoot(
                    "inicio/Inicio"
            );

        } else {

            // ======================================
            // LOGIN INCORRECTO
            // ======================================

            Alert alerta =
                    new Alert(AlertType.ERROR);

            alerta.setTitle(
                    "Error de autenticación"
            );

            alerta.setHeaderText(null);

            alerta.setContentText(
                    "Usuario o contraseña incorrectos, "
                    + "o el usuario está inactivo."
            );

            alerta.showAndWait();
        }
    }

    // ==============================================
    // RECUPERAR CONTRASEÑA
    // ==============================================

    @FXML
    private void recuperarContrasena(ActionEvent event)
            throws IOException {

        Navegador.abrirModal(
                "login/RecuperarContrasena",
                "Recuperar contraseña"
        );
    }
}