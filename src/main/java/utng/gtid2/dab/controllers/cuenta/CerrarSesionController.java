package utng.gtid2.dab.controllers.cuenta;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import utng.gtid2.dab.App;
import utng.gtid2.dab.util.Navegador;

public class CerrarSesionController implements Initializable {

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnCerrarSesion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // No es necesario inicializar nada por el momento.

    }

    @FXML
    private void cancelar(ActionEvent event) {

        Navegador.cerrar(btnCancelar);

    }

    @FXML
    private void cerrarSesion(ActionEvent event) throws IOException {

        // Cierra el modal
        Navegador.cerrar(btnCerrarSesion);

        // Regresa al Login
        App.setRoot("login/Login");

    }

}