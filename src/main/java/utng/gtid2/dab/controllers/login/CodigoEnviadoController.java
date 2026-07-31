package utng.gtid2.dab.controllers.login;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import utng.gtid2.dab.util.Navegador;

public class CodigoEnviadoController implements Initializable {

    @FXML
    private ImageView imgLogoAceptar;

    @FXML
    private Button btnAceptar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    private void aceptarCodigo(ActionEvent event) throws IOException {

        Navegador.cambiarModal(
                    btnAceptar,
                    "login/RestablecerContrasena",
                    "Restablecer contraseña");

    }

}