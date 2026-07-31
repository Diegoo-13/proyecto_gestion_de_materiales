package utng.gtid2.dab.controllers.usuarios;

import java.net.URL;
import java.security.SecureRandom;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import utng.gtid2.dab.util.Navegador;
import utng.gtid2.dab.modelo.Usuario;
import utng.gtid2.dab.dao.UsuarioDAO;

public class EditarUsuarioController implements Initializable {

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
    private Button btnGuardarCambios;

    @FXML
    private Button btnCerrarVentana;

    private Usuario usuarioActual;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    // Generador de contraseñas
    private static final String CARACTERES =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
          + "abcdefghijklmnopqrstuvwxyz"
          + "0123456789"
          + "@#$%&*!";

    private final SecureRandom random = new SecureRandom();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Datos de ejemplo (después vendrán de PostgreSQL)

        txtNombre.setText("");
        txtUsuario.setText("");
        txtCorreo.setText("");
        txtContrasena.setText("");

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
                btnGuardarCambios.fire());

    }

    public void setUsuario(Usuario usuario) {

        this.usuarioActual = usuario;

        txtNombre.setText(usuario.getNombre());

        txtApellidoPaterno.setText(usuario.getApellidoP());

        txtApellidoMaterno.setText(usuario.getApellidoM());

        txtUsuario.setText(usuario.getNomUsuario());

        txtCorreo.setText(usuario.getCorreo());

        cbRol.setValue(usuario.getRol());

        cbEstado.setValue(usuario.getEstado());

        txtContrasena.setText(usuario.getContrasena());

    }

    @FXML
    private void cerrarVentana(ActionEvent event) {

        Navegador.cerrar(btnCerrarVentana);

    }

    @FXML
    private void cancelar(ActionEvent event) {

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
    private void guardarCambios(ActionEvent event) {

        usuarioActual.setNombre(txtNombre.getText().trim());
        usuarioActual.setApellidoP(txtApellidoPaterno.getText().trim());
        usuarioActual.setApellidoM(txtApellidoMaterno.getText().trim());
        usuarioActual.setNomUsuario(txtUsuario.getText().trim());
        usuarioActual.setCorreo(txtCorreo.getText().trim());
        usuarioActual.setContrasena(txtContrasena.getText().trim());
        usuarioActual.setRol(cbRol.getValue());
        usuarioActual.setEstado(cbEstado.getValue());

        boolean actualizado = usuarioDAO.actualizar(usuarioActual);

        if (actualizado) {

            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Editar usuario");
            alerta.setHeaderText(null);
            alerta.setContentText("Usuario actualizado correctamente.");
            alerta.showAndWait();

            Navegador.cerrar(btnGuardarCambios);

        } else {

            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Editar usuario");
            alerta.setHeaderText(null);
            alerta.setContentText("No fue posible actualizar el usuario.");
            alerta.showAndWait();

        }

    }
}