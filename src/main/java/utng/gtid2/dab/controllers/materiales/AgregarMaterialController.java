package utng.gtid2.dab.controllers.materiales;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import utng.gtid2.dab.dao.MaterialDAO;
import utng.gtid2.dab.modelo.Material;

public class AgregarMaterialController implements Initializable {

    @FXML
    private TextField txtNombreMaterial;

    @FXML
    private ComboBox<String> cmbCategoria;

    @FXML
    private RadioButton rdbActivo;

    @FXML
    private RadioButton rdbConsumible;

    @FXML
    private ComboBox<String> cmbUbicacion;

    @FXML
    private ComboBox<String> cmbUnidad;

    @FXML
    private Spinner<Integer> spnStockMaximo;

    @FXML
    private Spinner<Integer> spnStockMinimo;

    @FXML
    private TextArea txtaObservaciones;

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnCerrarVentana;

    private final ToggleGroup grupoTipo = new ToggleGroup();

    private final MaterialDAO materialDAO = new MaterialDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Grupo de RadioButtons
        rdbActivo.setToggleGroup(grupoTipo);
        rdbConsumible.setToggleGroup(grupoTipo);
        rdbActivo.setSelected(true);

        // Configuración de Spinners
        spnStockMinimo.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 1));

        spnStockMaximo.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 10));

        // Ya no permitimos escribir categorías ni ubicaciones
        cmbCategoria.setEditable(false);
        cmbUbicacion.setEditable(false);

        // Cargar datos desde PostgreSQL
        cmbCategoria.getItems().setAll(materialDAO.obtenerCategorias());
        cmbUbicacion.getItems().setAll(materialDAO.obtenerUbicaciones());

        // Seleccionar el primer elemento si existe
        if (!cmbCategoria.getItems().isEmpty()) {
            cmbCategoria.getSelectionModel().selectFirst();
        }

        if (!cmbUbicacion.getItems().isEmpty()) {
            cmbUbicacion.getSelectionModel().selectFirst();
        }

        // Unidades predeterminadas
        cmbUnidad.getItems().addAll(
                "Pieza",
                "Caja",
                "Paquete",
                "Juego",
                "Metros",
                "Rollo",
                "Litro"
        );

        cmbUnidad.getSelectionModel().selectFirst();
    }

        @FXML
    private void guardarMaterial(ActionEvent event) {

        String nombre = txtNombreMaterial.getText().trim();
        String categoriaTexto = cmbCategoria.getValue();
        String ubicacionTexto = cmbUbicacion.getValue();

        if (nombre.isEmpty()) {
            mostrarAlerta("Campo Obligatorio",
                    "Por favor ingresa un nombre para el material.");
            return;
        }

        if (categoriaTexto == null || categoriaTexto.isBlank()) {
            mostrarAlerta("Campo Obligatorio",
                    "Selecciona una categoría.");
            return;
        }

        if (ubicacionTexto == null || ubicacionTexto.isBlank()) {
            mostrarAlerta("Campo Obligatorio",
                    "Selecciona una ubicación.");
            return;
        }

        int stockMin = spnStockMinimo.getValue();
        int stockMax = spnStockMaximo.getValue();

        if (stockMax < stockMin) {
            mostrarAlerta("Error de Validación",
                    "El Stock Máximo no puede ser menor que el Stock Mínimo.");
            return;
        }

        // Obtener los ID correspondientes
        int idCategoria = materialDAO.obtenerOCrearCategoria(categoriaTexto);
        int idUbicacion = materialDAO.obtenerOCrearUbicacion(ubicacionTexto);

        if (idCategoria == -1 || idUbicacion == -1) {
            mostrarAlerta("Error",
                    "No fue posible obtener la categoría o la ubicación.");
            return;
        }

        String tipo = rdbActivo.isSelected()
                ? "Activo"
                : "Consumible";

        String unidad = cmbUnidad.getValue();

        if (unidad == null || unidad.isBlank()) {
            unidad = "Pieza";
        }

        Material material = new Material();

        material.setNomMaterial(nombre);
        material.setDescripcion(txtaObservaciones.getText().trim());
        material.setStockMinimo(stockMin);
        material.setStockMaximo(stockMax);
        material.setTipo(tipo);
        material.setUnidad(unidad);
        material.setEstado("Disponible");
        material.setIdCategoria(idCategoria);
        material.setIdUbicacion(idUbicacion);

        boolean guardado = materialDAO.agregarMaterial(material);

        if (guardado) {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registro exitoso");
            alert.setHeaderText(null);
            alert.setContentText("El material se registró correctamente.");
            alert.showAndWait();

            cerrarVentana(event);

        } else {

            mostrarAlerta("Error",
                    "No fue posible registrar el material.");

        }

    }

        @FXML
    private void cancelarRegistro(ActionEvent event) {
        cerrarVentana(event);
    }

    @FXML
    private void cerrarVentana(ActionEvent event) {

        Stage stage;

        if (event != null && event.getSource() instanceof Button) {
            stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        } else {
            stage = (Stage) btnGuardar.getScene().getWindow();
        }

        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}