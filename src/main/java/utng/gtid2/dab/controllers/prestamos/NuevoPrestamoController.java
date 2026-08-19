package utng.gtid2.dab.controllers.prestamos;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import utng.gtid2.dab.dao.MaterialDAO;
import utng.gtid2.dab.dao.PrestamosDAO;
import utng.gtid2.dab.modelo.Material;
import utng.gtid2.dab.modelo.Prestamo;
import utng.gtid2.dab.util.Navegador;

public class NuevoPrestamoController implements Initializable {

    @FXML
    private TextField txtResponsable;

    @FXML
    private ComboBox<Material> cmbMaterial;

    @FXML
    private TextField txtTelefono;

    @FXML
    private Spinner<Integer> spnCantidad;

    @FXML
    private DatePicker dpFechaPrestamo;

    @FXML
    private DatePicker dpFechaDevolucion;

    @FXML
    private ComboBox<String> cmbHoraDevolucion;

    @FXML
    private TextArea txtaObservaciones;

    @FXML
    private Button btnCerrar;

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnRegistrarPrestamo;

    //================ DAO =================

    private final MaterialDAO materialDAO = new MaterialDAO();

    private final PrestamosDAO prestamosDAO = new PrestamosDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //================ FECHA =================

        dpFechaPrestamo.setValue(LocalDate.now());

        //================ CANTIDAD =================

        spnCantidad.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));

        //================ HORARIOS =================

cmbHoraDevolucion.getItems().addAll(
        "08:00",
        "09:00",
        "10:00",
        "11:00",
        "12:00",
        "13:00",
        "14:00",
        "15:00",
        "16:00",
        "17:00",
        "18:00",
        "19:00",
        "20:00");

cmbHoraDevolucion.getSelectionModel().selectFirst();
        //================ CARGAR MATERIALES =================

        cmbMaterial.getItems().clear();
        cmbMaterial.getItems().addAll(materialDAO.obtenerTodosLosMateriales());

        if (!cmbMaterial.getItems().isEmpty()) {
            cmbMaterial.getSelectionModel().selectFirst();
        }

        //================ TELÉFONO =================

        txtTelefono.textProperty().addListener((obs, oldValue, newValue) -> {

            String numeros = newValue.replaceAll("\\D", "");

            if (numeros.length() > 10) {
                numeros = numeros.substring(0, 10);
            }

            StringBuilder formato = new StringBuilder();

            for (int i = 0; i < numeros.length(); i++) {

                if (i == 3 || i == 6 || i == 8) {
                    formato.append(" ");
                }

                formato.append(numeros.charAt(i));
            }

            if (!newValue.equals(formato.toString())) {
                txtTelefono.setText(formato.toString());
                txtTelefono.positionCaret(txtTelefono.getText().length());
            }

        });

        //================ ENTER ENTRE CAMPOS =================

        txtResponsable.setOnAction(e -> txtTelefono.requestFocus());

        txtTelefono.setOnAction(e -> spnCantidad.requestFocus());

        spnCantidad.setOnKeyPressed(e -> {

            if (e.getCode() == KeyCode.ENTER) {
                cmbMaterial.requestFocus();
            }

        });

        cmbMaterial.setOnAction(e -> cmbHoraDevolucion.requestFocus());

        cmbHoraDevolucion.setOnAction(e -> dpFechaDevolucion.requestFocus());

        dpFechaDevolucion.setOnAction(e -> txtaObservaciones.requestFocus());

    }

    @FXML
    private void cerrarVentana(ActionEvent event) {

        Navegador.cerrar(btnCerrar);

    }

    @FXML
    private void cancelarPrestamo(ActionEvent event) {

        limpiarFormulario();
        Navegador.cerrar(btnCancelar);

    }

    @FXML
    private void registrarPrestamo(ActionEvent event) {

        Material material = cmbMaterial.getValue();

        if (material == null) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Préstamos");
            alert.setHeaderText(null);
            alert.setContentText("Seleccione un material.");
            alert.showAndWait();
            return;
        }

        Prestamo prestamo = new Prestamo();

        prestamo.setIdMaterial(material.getIdMaterial());
        prestamo.setCantidad(spnCantidad.getValue());
        prestamo.setFechaPrestamo(dpFechaPrestamo.getValue());
        prestamo.setFechaDevolucion(dpFechaDevolucion.getValue());
        prestamo.setHoraDevolucion(LocalTime.parse(cmbHoraDevolucion.getValue()));
        prestamo.setResponsable(txtResponsable.getText().trim());
        prestamo.setTelefono(txtTelefono.getText().replace(" ", "").trim());
        prestamo.setObservaciones(txtaObservaciones.getText().trim());
        prestamo.setEstado("Activo");

        if (prestamosDAO.insertar(prestamo)) {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Préstamos");
            alert.setHeaderText(null);
            alert.setContentText("Préstamo registrado correctamente.");
            alert.showAndWait();

            limpiarFormulario();
            Navegador.cerrar(btnRegistrarPrestamo);

        } else {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Préstamos");
            alert.setHeaderText(null);
            alert.setContentText("No fue posible registrar el préstamo.");
            alert.showAndWait();

        }

    }

    //================ LIMPIAR FORMULARIO =================

    private void limpiarFormulario() {

        txtResponsable.clear();
        txtTelefono.clear();
        txtaObservaciones.clear();

        spnCantidad.getValueFactory().setValue(1);

        dpFechaPrestamo.setValue(LocalDate.now());
        dpFechaDevolucion.setValue(null);

        cmbHoraDevolucion.getSelectionModel().selectFirst();

        if (!cmbMaterial.getItems().isEmpty()) {
            cmbMaterial.getSelectionModel().selectFirst();
        }

    }

}