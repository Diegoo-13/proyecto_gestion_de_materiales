package utng.gtid2.dab.controllers.danos;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import utng.gtid2.dab.util.Navegador;

public class ReportarDanoController implements Initializable {

    @FXML
    private ComboBox<String> cbMaterial;

    @FXML
    private ComboBox<String> cbTipoDanio;

    @FXML
    private TextField txtReporto;

    @FXML
    private TextField txtEstado;

    @FXML
    private DatePicker dpFechaReporte;

    @FXML
    private TextArea txtaDescripcion;

    @FXML
    private Button btnCerrarVentana;

    @FXML
    private Button btnCancelarDano;

    @FXML
    private Button btnGuardarReporte;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Fecha actual
        dpFechaReporte.setValue(LocalDate.now());

        // Estado por defecto
        txtEstado.setText("En evaluación");

        // Materiales de ejemplo
        cbMaterial.getItems().addAll(
                "Laptop Dell Latitude",
                "Mouse Logitech M185",
                "Teclado HP",
                "Monitor Samsung",
                "Proyector Epson");

        // Tipos de daño
        cbTipoDanio.getItems().addAll(
                "Golpe",
                "Pantalla dañada",
                "No enciende",
                "Cable dañado",
                "Falla eléctrica",
                "Piezas faltantes",
                "Otro");

    }

    @FXML
    private void cerrarVentana(ActionEvent event) {

        Navegador.cerrar(btnCerrarVentana);

    }

    @FXML
    private void cancelarDano(ActionEvent event) {

        limpiarFormulario();
        Navegador.cerrar(btnCancelarDano);

    }

    @FXML
    private void guardarReporte(ActionEvent event) {

        /*
         * Aquí después irá:
         *
         * MaterialDanadoDAO.insertar(...)
         */

        limpiarFormulario();
        Navegador.cerrar(btnGuardarReporte);

    }

    private void limpiarFormulario() {

        cbMaterial.getSelectionModel().clearSelection();
        cbTipoDanio.getSelectionModel().clearSelection();

        txtReporto.clear();
        txtaDescripcion.clear();

        txtEstado.setText("En evaluación");

        dpFechaReporte.setValue(LocalDate.now());

    }

}