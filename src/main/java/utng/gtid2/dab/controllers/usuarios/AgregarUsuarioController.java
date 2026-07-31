package utng.gtid2.dab.controllers.usuarios;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import utng.gtid2.dab.util.Navegador;
import java.security.SecureRandom;
import javafx.scene.control.Alert;
import utng.gtid2.dab.dao.UsuarioDAO;
import utng.gtid2.dab.modelo.Usuario;


public class AgregarUsuarioController implements Initializable {

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtApellidoPaterno;

    @FXML
    private TextField txtApellidoMaterno;

    @FXML
    private TextField txtUsuario;

    @FXML
    private TextField txtCorreo;

    @FXML
    private ComboBox<String> cbRol;

    @FXML
    private ComboBox<String> cbEstado;

    @FXML
    private TextField txtContrasena;

    @FXML
    private Button btnGenerar;

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnCerrarVentana;

        private static final String CARACTERES =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        + "abcdefghijklmnopqrstuvwxyz"
        + "0123456789";

    private final SecureRandom random = new SecureRandom();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        cbRol.getItems().addAll(
                "Administrador",
                "Usuario");

        cbRol.getSelectionModel().select("Usuario");

        cbEstado.getItems().addAll(
                "Activo",
                "Inactivo");

        cbEstado.getSelectionModel().select("Activo");

        //================ NAVEGACIÓN CON ENTER =================

        txtNombre.setOnAction(e ->
                txtApellidoPaterno.requestFocus());

        txtApellidoPaterno.setOnAction(e ->
                txtApellidoMaterno.requestFocus());

        txtApellidoMaterno.setOnAction(e ->
                txtUsuario.requestFocus());

        txtUsuario.setOnAction(e ->
                txtCorreo.requestFocus());

        txtCorreo.setOnAction(e ->
                cbRol.requestFocus());

        cbRol.setOnAction(e ->
                cbEstado.requestFocus());

        cbEstado.setOnAction(e ->
                txtContrasena.requestFocus());

        txtContrasena.setOnAction(e ->
                btnGuardar.fire());

    }

    @FXML
    private void cerrarVentana(ActionEvent event) {

        Navegador.cerrar(btnCerrarVentana);

    }

    @FXML
    private void cancelar(ActionEvent event) {

        limpiarFormulario();
        Navegador.cerrar(btnCancelar);

    }

    @FXML
    private void generar(ActionEvent event) {

        StringBuilder pass = new StringBuilder();

        for (int i = 0; i < 10; i++) {

            pass.append(
                CARACTERES.charAt(
                    random.nextInt(CARACTERES.length())
                )
            );

        }

        txtContrasena.setText(pass.toString());

    }

    @FXML
    private void guardar(ActionEvent event) {

        if (txtNombre.getText().trim().isEmpty()
                || txtApellidoPaterno.getText().trim().isEmpty()
                || txtApellidoMaterno.getText().trim().isEmpty()
                || txtUsuario.getText().trim().isEmpty()
                || txtCorreo.getText().trim().isEmpty()
                || txtContrasena.getText().trim().isEmpty()) {

            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Campos incompletos");
            alerta.setHeaderText(null);
            alerta.setContentText("Complete todos los campos.");
            alerta.showAndWait();
            return;
        }

        Usuario usuario = new Usuario();

        usuario.setNombre(txtNombre.getText().trim());
        usuario.setApellidoP(txtApellidoPaterno.getText().trim());
        usuario.setApellidoM(txtApellidoMaterno.getText().trim());
        usuario.setNomUsuario(txtUsuario.getText().trim());
        usuario.setCorreo(txtCorreo.getText().trim());
        usuario.setContrasena(txtContrasena.getText().trim());
        usuario.setRol(cbRol.getValue());
        usuario.setEstado(cbEstado.getValue());

        UsuarioDAO dao = new UsuarioDAO();

        boolean registrado = dao.insertar(usuario);

        if (registrado) {

            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Éxito");
            alerta.setHeaderText(null);
            alerta.setContentText("Usuario registrado correctamente.");
            alerta.showAndWait();

            limpiarFormulario();
            Navegador.cerrar(btnGuardar);

        } else {

            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error");
            alerta.setHeaderText(null);
            alerta.setContentText("No fue posible registrar el usuario.");
            alerta.showAndWait();

        }

    }

    private void limpiarFormulario() {

        txtNombre.clear();
        txtApellidoPaterno.clear();
        txtApellidoMaterno.clear();
        txtUsuario.clear();
        txtCorreo.clear();
        txtContrasena.clear();

        cbRol.getSelectionModel().select("Usuario");
        cbEstado.getSelectionModel().select("Activo");

    }
}