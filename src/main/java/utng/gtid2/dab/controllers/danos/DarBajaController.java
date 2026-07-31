package utng.gtid2.dab.controllers.danos;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import utng.gtid2.dab.util.Navegador;

public class DarBajaController implements Initializable {

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtReporto;

    @FXML
    private TextField txtMaterial;

    @FXML
    private TextField txtEstado;

    @FXML
    private TextField txtCategoria;

    @FXML
    private TextField txtFechaReporte;

    @FXML
    private TextArea txtaMotivo;

    @FXML
    private Button btnCerrarVentana;

    @FXML
    private Button btnCancelarBaja;

    @FXML
    private Button btnConfirmarBaja;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Datos de ejemplo mientras se conecta PostgreSQL

        txtId.setText("MAT-001");
        txtReporto.setText("Juan Pérez");
        txtMaterial.setText("Mouse Logitech M185");
        txtEstado.setText("En evaluación");
        txtCategoria.setText("Periféricos");
        txtFechaReporte.setText("24/07/2026");

    }

    @FXML
    private void cerrarVentana(ActionEvent event) {

        Navegador.cerrar(btnCerrarVentana);

    }

    @FXML
    private void cancelarBaja(ActionEvent event) {

        txtaMotivo.clear();

        Navegador.cerrar(btnCancelarBaja);

    }

    @FXML
    private void confirmarBaja(ActionEvent event) {

        /*
         * Aquí después irá:
         *
         * MaterialDanadoDAO.darDeBaja(...)
         * MaterialDAO.actualizarEstado(...)
         */

        txtaMotivo.clear();

        Navegador.cerrar(btnConfirmarBaja);

    }

}