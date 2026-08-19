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

import utng.gtid2.dab.App;
import utng.gtid2.dab.dao.UsuarioDAO;
import utng.gtid2.dab.modelo.Usuario;

public class ConfiguracionInicialController implements Initializable {

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtApellidoP;

    @FXML
    private TextField txtApellidoM;

    @FXML
    private TextField txtUsuario;

    @FXML
    private TextField txtCorreo;

    @FXML
    private PasswordField pwdContrasena;

    @FXML
    private PasswordField pwdConfirmar;

    @FXML
    private Button btnCrearAdministrador;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        txtNombre.setOnAction(e -> txtApellidoP.requestFocus());

        txtApellidoP.setOnAction(e -> txtApellidoM.requestFocus());

        txtApellidoM.setOnAction(e -> txtUsuario.requestFocus());

        txtUsuario.setOnAction(e -> txtCorreo.requestFocus());

        txtCorreo.setOnAction(e -> pwdContrasena.requestFocus());

        pwdContrasena.setOnAction(e -> pwdConfirmar.requestFocus());

        pwdConfirmar.setOnAction(e -> {
            try {
                crearAdministrador(e);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    @FXML
    private void crearAdministrador(ActionEvent event) throws IOException {

        String nombre = txtNombre.getText().trim();
        String apellidoP = txtApellidoP.getText().trim();
        String apellidoM = txtApellidoM.getText().trim();
        String nomUsuario = txtUsuario.getText().trim();
        String correo = txtCorreo.getText().trim();
        String contrasena = pwdContrasena.getText();
        String confirmar = pwdConfirmar.getText();

        // Validar campos vacíos
        if (nombre.isEmpty() ||
            apellidoP.isEmpty() ||
            apellidoM.isEmpty() ||
            nomUsuario.isEmpty() ||
            correo.isEmpty() ||
            contrasena.isEmpty() ||
            confirmar.isEmpty()) {

            mostrarAlerta(
                    AlertType.WARNING,
                    "Campos incompletos",
                    "Complete todos los campos para crear el administrador."
            );

            return;
        }

        // Validar contraseñas
        if (!contrasena.equals(confirmar)) {

            mostrarAlerta(
                    AlertType.WARNING,
                    "Contraseñas diferentes",
                    "Las contraseñas no coinciden."
            );

            pwdConfirmar.clear();
            pwdConfirmar.requestFocus();

            return;
        }

        // Validar longitud de contraseña
        if (contrasena.length() < 6) {

            mostrarAlerta(
                    AlertType.WARNING,
                    "Contraseña inválida",
                    "La contraseña debe tener al menos 6 caracteres."
            );

            pwdContrasena.clear();
            pwdConfirmar.clear();
            pwdContrasena.requestFocus();

            return;
        }

        // Verificar que todavía no exista un usuario
        if (usuarioDAO.existenUsuarios()) {

            mostrarAlerta(
                    AlertType.INFORMATION,
                    "Configuración completada",
                    "Ya existe un usuario registrado. Puede iniciar sesión."
            );

            App.setRoot("login/Login");
            return;
        }

        // Verificar correo
        if (usuarioDAO.existeCorreo(correo)) {

            mostrarAlerta(
                    AlertType.WARNING,
                    "Correo registrado",
                    "El correo ingresado ya está registrado."
            );

            txtCorreo.requestFocus();
            return;
        }

        // Crear administrador
        Usuario administrador = new Usuario();

        administrador.setNomUsuario(nomUsuario);
        administrador.setNombre(nombre);
        administrador.setApellidoP(apellidoP);
        administrador.setApellidoM(apellidoM);
        administrador.setCorreo(correo);
        administrador.setContrasena(contrasena);
        administrador.setRol("Administrador");
        administrador.setEstado("Activo");

        boolean creado = usuarioDAO.insertar(administrador);

        if (creado) {

            Alert alerta = new Alert(AlertType.INFORMATION);
            alerta.setTitle("Configuración inicial");
            alerta.setHeaderText(null);
            alerta.setContentText(
                    "Administrador creado correctamente.\n\n" +
                    "Ya puede iniciar sesión con sus credenciales."
            );
            alerta.showAndWait();

            App.setRoot("login/Login");

        } else {

            mostrarAlerta(
                    AlertType.ERROR,
                    "Error",
                    "No fue posible crear el administrador."
            );
        }
    }

    private void mostrarAlerta(
            AlertType tipo,
            String titulo,
            String mensaje) {

        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}