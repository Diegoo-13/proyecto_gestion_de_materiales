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

public class EditarMaterialController implements Initializable {

    @FXML private TextField txtNombreMaterial;
    @FXML private ComboBox<String> cmbCategoria;
    @FXML private RadioButton rdbActivo;
    @FXML private RadioButton rdbConsumible;
    @FXML private ComboBox<String> cmbUbicacion;
    @FXML private ComboBox<String> cmbUnidad;
    @FXML private Spinner<Integer> spnStockMaximo;
    @FXML private Spinner<Integer> spnStockMinimo;
    @FXML private TextArea txtaObservaciones;
    @FXML private Button btnGuardarCambios;
    @FXML private Button btnCancelarCambios;
    @FXML private Button btnCerrarVentana;

    private ToggleGroup grupoTipo = new ToggleGroup();
    private MaterialDAO materialDAO = new MaterialDAO();
    private Material materialAEditar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        rdbActivo.setToggleGroup(grupoTipo);
        rdbConsumible.setToggleGroup(grupoTipo);

        spnStockMinimo.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 1));

        spnStockMaximo.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 10));

        cmbCategoria.setEditable(false);
        cmbUbicacion.setEditable(false);

        cmbCategoria.getItems().setAll(materialDAO.obtenerCategorias());

        cmbUbicacion.getItems().setAll(materialDAO.obtenerUbicaciones());

        cmbUnidad.getItems().setAll(
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
    
    public void cargarMaterial(Material material) {
        this.materialAEditar = material;
        txtNombreMaterial.setText(material.getNomMaterial());
        txtaObservaciones.setText(material.getDescripcion());

        if ("Activo".equalsIgnoreCase(material.getTipo())) {
            rdbActivo.setSelected(true);
        } else {
            rdbConsumible.setSelected(true);
        }

        int min = material.getStockMinimo();
        int max = material.getStockMaximo();

        spnStockMinimo.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,
        1000, min));
        spnStockMaximo.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,
        1000, max));

        if (material.getNomCategoria() != null) {
            cmbCategoria.setValue(material.getNomCategoria());
        }
        if (material.getNomUbicacion() != null) {
            cmbUbicacion.setValue(material.getNomUbicacion());
        }
        cmbUnidad.setValue(material.getUnidad() != null ? material.getUnidad() : "Pieza");
    }

    @FXML
    private void guardarCambios(ActionEvent event) {
        if (materialAEditar == null) return;

        String nombre = txtNombreMaterial.getText().trim();
        String categoriaTexto = cmbCategoria.getValue();
        String ubicacionTexto = cmbUbicacion.getValue();

        if (nombre.isEmpty()) {
            mostrarAlerta("Campo Obligatorio", "El nombre no puede estar vacío.");
        return;
        }
        if (categoriaTexto == null || categoriaTexto.trim().isEmpty()) {
            mostrarAlerta("Campo Obligatorio", "Selecciona o ingresa una categoría.");
        return;
        }
        if (ubicacionTexto == null || ubicacionTexto.trim().isEmpty()) {
            mostrarAlerta("Campo Obligatorio", "Selecciona o ingresa una ubicación.");
        return;
        }

        int stockMin = spnStockMinimo.getValue();
        int stockMax = spnStockMaximo.getValue();

        if (stockMax < stockMin) {
            mostrarAlerta("Error de Validación", "El Stock Máximo no puede ser menor al Stock Mínimo.");
        return;
        }

        int idCategoria = materialDAO.obtenerOCrearCategoria(categoriaTexto.trim());
        int idUbicacion = materialDAO.obtenerOCrearUbicacion(ubicacionTexto.trim());

        if (idCategoria == -1 || idUbicacion == -1) {
            mostrarAlerta("Error de BD", "No se pudo actualizar la categoría o la ubicación.");
        return;
        }

        materialAEditar.setNomMaterial(nombre);
        materialAEditar.setDescripcion(txtaObservaciones.getText().trim());
        materialAEditar.setTipo(rdbActivo.isSelected() ? "Activo" : "Consumible");
        materialAEditar.setStockMinimo(stockMin);
        materialAEditar.setStockMaximo(stockMax);
        materialAEditar.setUnidad(cmbUnidad.getValue());
        materialAEditar.setIdCategoria(idCategoria);
        materialAEditar.setIdUbicacion(idUbicacion);

        if (materialDAO.actualizarMaterial(materialAEditar)) {cerrarVentana(event);
        } else {
            mostrarAlerta("Error", "No se pudieron actualizar los cambios en la base de datos.");
        }
    }

    @FXML
    private void cancelarCambios(ActionEvent event) {
        cerrarVentana(event);
    }

    @FXML
    private void cerrarVentana(ActionEvent event) {
        Stage stage = (Stage) btnCerrarVentana.getScene().getWindow();
        stage.close();
    }

    @FXML private void nombreMaterial(ActionEvent event) {}
    @FXML private void seleccionarCategoria(ActionEvent event) {}
    @FXML private void sleccionarUbicacion(ActionEvent event) {}
    @FXML private void seleccionarUnidad(ActionEvent event) {}

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}