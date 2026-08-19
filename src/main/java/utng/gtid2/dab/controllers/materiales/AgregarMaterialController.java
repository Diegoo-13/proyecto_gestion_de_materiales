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
        //jime estuvo aqui
    // =========================================================
    // CAMPOS DEL FORMULARIO
    // =========================================================

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
    private Spinner<Integer> spnCantidadInicial;

    @FXML
    private Spinner<Integer> spnStockMinimo;

    @FXML
    private TextArea txtaObservaciones;


    // =========================================================
    // BOTONES
    // =========================================================

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnCerrarVentana;


    // =========================================================
    // DATOS
    // =========================================================

    private final ToggleGroup grupoTipo = new ToggleGroup();

    private final MaterialDAO materialDAO = new MaterialDAO();


    // =========================================================
    // INICIALIZACIÓN
    // =========================================================

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // -----------------------------------------------------
        // RadioButtons
        // -----------------------------------------------------

        rdbActivo.setToggleGroup(grupoTipo);
        rdbConsumible.setToggleGroup(grupoTipo);

        rdbActivo.setSelected(true);


        // -----------------------------------------------------
        // Spinner de cantidad inicial
        // -----------------------------------------------------

        spnCantidadInicial.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        1,
                        1000,
                        10
                )
        );


        // -----------------------------------------------------
        // Spinner de stock mínimo
        // -----------------------------------------------------

        spnStockMinimo.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        0,
                        1000,
                        1
                )
        );


        // -----------------------------------------------------
        // Categorías y ubicaciones
        // -----------------------------------------------------

        cmbCategoria.setEditable(false);
        cmbUbicacion.setEditable(false);

        cmbCategoria.getItems().setAll(
                materialDAO.obtenerCategorias()
        );

        cmbUbicacion.getItems().setAll(
                materialDAO.obtenerUbicaciones()
        );


        // Seleccionar primer elemento

        if (!cmbCategoria.getItems().isEmpty()) {
            cmbCategoria.getSelectionModel().selectFirst();
        }

        if (!cmbUbicacion.getItems().isEmpty()) {
            cmbUbicacion.getSelectionModel().selectFirst();
        }


        // -----------------------------------------------------
        // Unidades
        // -----------------------------------------------------

        cmbUnidad.getItems().setAll(
                "Pieza",
                "Caja",
                "Paquete",
                "Juego",
                "Metro",
                "Rollo",
                "Litro"
        );

        cmbUnidad.getSelectionModel().selectFirst();
    }


    // =========================================================
    // GUARDAR MATERIAL
    // =========================================================

    @FXML
    private void guardarMaterial(ActionEvent event) {

        String nombre =
                txtNombreMaterial.getText().trim();

        String categoriaTexto =
                cmbCategoria.getValue();

        String ubicacionTexto =
                cmbUbicacion.getValue();


        // -----------------------------------------------------
        // Validar nombre
        // -----------------------------------------------------

        if (nombre.isEmpty()) {

            mostrarAlerta(
                    "Campo obligatorio",
                    "Por favor ingresa un nombre para el material."
            );

            return;
        }


        // -----------------------------------------------------
        // Validar categoría
        // -----------------------------------------------------

        if (categoriaTexto == null ||
                categoriaTexto.isBlank()) {

            mostrarAlerta(
                    "Campo obligatorio",
                    "Selecciona una categoría."
            );

            return;
        }


        // -----------------------------------------------------
        // Validar ubicación
        // -----------------------------------------------------

        if (ubicacionTexto == null ||
                ubicacionTexto.isBlank()) {

            mostrarAlerta(
                    "Campo obligatorio",
                    "Selecciona una ubicación."
            );

            return;
        }


        // -----------------------------------------------------
        // Obtener cantidades
        // -----------------------------------------------------

        int cantidadInicial =
                spnCantidadInicial.getValue();

        int stockMinimo =
                spnStockMinimo.getValue();


        // -----------------------------------------------------
        // Validar stock mínimo
        // -----------------------------------------------------

        if (stockMinimo > cantidadInicial) {

            mostrarAlerta(
                    "Error de validación",
                    "El stock mínimo no puede ser mayor "
                    + "que la cantidad inicial."
            );

            return;
        }


        // -----------------------------------------------------
        // Obtener IDs de categoría y ubicación
        // -----------------------------------------------------

        int idCategoria =
                materialDAO.obtenerOCrearCategoria(
                        categoriaTexto
                );

        int idUbicacion =
                materialDAO.obtenerOCrearUbicacion(
                        ubicacionTexto
                );


        if (idCategoria == -1 ||
                idUbicacion == -1) {

            mostrarAlerta(
                    "Error",
                    "No fue posible obtener la categoría "
                    + "o la ubicación."
            );

            return;
        }


        // -----------------------------------------------------
        // Tipo de material
        // -----------------------------------------------------

        String tipo;

        if (rdbActivo.isSelected()) {
            tipo = "Activo";
        } else {
            tipo = "Consumible";
        }


        // -----------------------------------------------------
        // Unidad
        // -----------------------------------------------------

        String unidad =
                cmbUnidad.getValue();

        if (unidad == null ||
                unidad.isBlank()) {

            unidad = "Pieza";
        }


        // -----------------------------------------------------
        // Crear objeto Material
        // -----------------------------------------------------

        Material material = new Material();

        material.setNomMaterial(nombre);

        material.setDescripcion(
                txtaObservaciones.getText().trim()
        );

        material.setStockMinimo(stockMinimo);

        material.setStockActual(cantidadInicial);

        material.setTipo(tipo);

        material.setUnidad(unidad);

        material.setEstado("Disponible");

        material.setIdCategoria(idCategoria);

        material.setIdUbicacion(idUbicacion);


        // -----------------------------------------------------
        // Guardar en base de datos
        // -----------------------------------------------------

        boolean guardado =
                materialDAO.agregarMaterial(material);


        if (guardado) {

            Alert alert =
                    new Alert(Alert.AlertType.INFORMATION);

            alert.setTitle("Registro exitoso");

            alert.setHeaderText(null);

            alert.setContentText(
                    "El material se registró correctamente."
            );

            alert.showAndWait();

            cerrarVentana(event);

        } else {

            mostrarAlerta(
                    "Error",
                    "No fue posible registrar el material."
            );
        }
    }


    // =========================================================
    // CANCELAR
    // =========================================================

    @FXML
    private void cancelarRegistro(ActionEvent event) {

        cerrarVentana(event);
    }


    // =========================================================
    // CERRAR VENTANA
    // =========================================================

    @FXML
    private void cerrarVentana(ActionEvent event) {

        Stage stage;

        if (event != null &&
                event.getSource() instanceof Button) {

            stage = (Stage)
                    ((Button) event.getSource())
                            .getScene()
                            .getWindow();

        } else {

            stage = (Stage)
                    btnGuardar
                            .getScene()
                            .getWindow();
        }

        stage.close();
    }


    // =========================================================
    // ALERTA
    // =========================================================

    private void mostrarAlerta(
            String titulo,
            String mensaje) {

        Alert alert =
                new Alert(Alert.AlertType.WARNING);

        alert.setTitle(titulo);

        alert.setHeaderText(null);

        alert.setContentText(mensaje);

        alert.showAndWait();
    }
}