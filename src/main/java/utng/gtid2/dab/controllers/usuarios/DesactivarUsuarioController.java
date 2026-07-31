package utng.gtid2.dab.controllers.usuarios;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import utng.gtid2.dab.util.Navegador;

import javafx.scene.control.Alert;
import utng.gtid2.dab.dao.UsuarioDAO;
import utng.gtid2.dab.modelo.Usuario;

public class DesactivarUsuarioController implements Initializable {

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnDesactivar;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    private Usuario usuarioActual;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // No se requiere inicialización por el momento.

    }

    public void setUsuario(Usuario usuario) {

        this.usuarioActual = usuario;

    }

    @FXML
    private void cancelar(ActionEvent event) {

        Navegador.cerrar(btnCancelar);

    }

   @FXML
    private void desactivar(ActionEvent event) {

        usuarioActual.setEstado("Inactivo");

        boolean actualizado = usuarioDAO.actualizar(usuarioActual);

        if (actualizado) {

            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Desactivar usuario");
            alerta.setHeaderText(null);
            alerta.setContentText("Usuario desactivado correctamente.");
            alerta.showAndWait();

            Navegador.cerrar(btnDesactivar);

        } else {

            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Desactivar usuario");
            alerta.setHeaderText(null);
            alerta.setContentText("No fue posible desactivar el usuario.");
            alerta.showAndWait();

        }

    }
    
}