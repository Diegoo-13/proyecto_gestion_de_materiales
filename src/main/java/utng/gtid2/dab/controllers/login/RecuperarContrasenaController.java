package utng.gtid2.dab.controllers.login;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import utng.gtid2.dab.util.Navegador;

public class RecuperarContrasenaController implements Initializable {

    @FXML
    private TextField txtCorreoInstitucional;

    @FXML
    private Button btnEnviarCodigo;

    @FXML
    private Button btnCancelar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
        private void enviarCodigo(ActionEvent event) throws IOException {

        // Cierra esta ventana
        Navegador.cerrar(btnEnviarCodigo);

        // Abre la siguiente
        Navegador.abrirModal(
                "login/CodigoEnviado",
                "Código enviado");

    }

    @FXML
    private void cancelar(ActionEvent event) throws IOException {

        Navegador.cerrar(btnCancelar);

    }

}