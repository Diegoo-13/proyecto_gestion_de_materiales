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

    // =========================================================
    // CONTROLES FXML
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
    private Spinner<Integer> spnStockActual;

    @FXML
    private Spinner<Integer> spnStockMinimo;

    @FXML
    private TextArea txtaObservaciones;

    @FXML
    private Button btnGuardarCambios;

    @FXML
    private Button btnCancelarCambios;

    @FXML
    private Button btnCerrarVentana;


    // =========================================================
    // OBJETOS
    // =========================================================

    private final ToggleGroup grupoTipo = new ToggleGroup();

    private final MaterialDAO materialDAO = new MaterialDAO();

    private Material materialAEditar;


    // =========================================================
    // INICIALIZACIÓN
    // =========================================================

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Grupo de tipo de material
        rdbActivo.setToggleGroup(grupoTipo);
        rdbConsumible.setToggleGroup(grupoTipo);

        // Selección predeterminada
        rdbActivo.setSelected(true);


        // =====================================================
        // SPINNER STOCK ACTUAL
        // =====================================================

        spnStockActual.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        0,
                        1000,
                        1
                )
        );


        // =====================================================
        // SPINNER STOCK MÍNIMO
        // =====================================================

        spnStockMinimo.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        0,
                        1000,
                        1
                )
        );


        // =====================================================
        // CATEGORÍAS
        // =====================================================

        cmbCategoria.setEditable(false);

        cmbCategoria.getItems().setAll(
                materialDAO.obtenerCategorias()
        );


        // =====================================================
        // UBICACIONES
        // =====================================================

        cmbUbicacion.setEditable(false);

        cmbUbicacion.getItems().setAll(
                materialDAO.obtenerUbicaciones()
        );


        // =====================================================
        // UNIDADES
        // =====================================================

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
    // CARGAR MATERIAL A EDITAR
    // =========================================================

    public void cargarMaterial(Material material) {

        if (material == null) {
            return;
        }

        this.materialAEditar = material;


        // Nombre
        txtNombreMaterial.setText(
                material.getNomMaterial()
        );


        // Descripción
        txtaObservaciones.setText(
                material.getDescripcion() != null
                        ? material.getDescripcion()
                        : ""
        );


        // =====================================================
        // TIPO
        // =====================================================

        if ("Activo".equalsIgnoreCase(material.getTipo())) {

            rdbActivo.setSelected(true);

        } else {

            rdbConsumible.setSelected(true);
        }


        // =====================================================
        // STOCK ACTUAL
        // =====================================================

        int stockActual = material.getStockActual();

        spnStockActual.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        0,
                        1000,
                        stockActual
                )
        );


        // =====================================================
        // STOCK MÍNIMO
        // =====================================================

        int stockMinimo = material.getStockMinimo();

        spnStockMinimo.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        0,
                        1000,
                        stockMinimo
                )
        );


        // =====================================================
        // CATEGORÍA
        // =====================================================

        if (material.getNomCategoria() != null) {

            cmbCategoria.setValue(
                    material.getNomCategoria()
            );
        }


        // =====================================================
        // UBICACIÓN
        // =====================================================

        if (material.getNomUbicacion() != null) {

            cmbUbicacion.setValue(
                    material.getNomUbicacion()
            );
        }


        // =====================================================
        // UNIDAD
        // =====================================================

        if (material.getUnidad() != null &&
                !material.getUnidad().isBlank()) {

            cmbUnidad.setValue(
                    material.getUnidad()
            );

        } else {

            cmbUnidad.setValue("Pieza");
        }
    }


    // =========================================================
    // GUARDAR CAMBIOS
    // =========================================================

    @FXML
    private void guardarCambios(ActionEvent event) {

        if (materialAEditar == null) {

            mostrarAlerta(
                    "Error",
                    "No hay ningún material seleccionado para editar."
            );

            return;
        }


        // =====================================================
        // DATOS DEL FORMULARIO
        // =====================================================

        String nombre =
                txtNombreMaterial.getText().trim();

        String categoriaTexto =
                cmbCategoria.getValue();

        String ubicacionTexto =
                cmbUbicacion.getValue();

        String unidad =
                cmbUnidad.getValue();


        // =====================================================
        // VALIDAR NOMBRE
        // =====================================================

        if (nombre.isEmpty()) {

            mostrarAlerta(
                    "Campo obligatorio",
                    "El nombre del material no puede estar vacío."
            );

            return;
        }


        // =====================================================
        // VALIDAR CATEGORÍA
        // =====================================================

        if (categoriaTexto == null ||
                categoriaTexto.isBlank()) {

            mostrarAlerta(
                    "Campo obligatorio",
                    "Selecciona una categoría."
            );

            return;
        }


        // =====================================================
        // VALIDAR UBICACIÓN
        // =====================================================

        if (ubicacionTexto == null ||
                ubicacionTexto.isBlank()) {

            mostrarAlerta(
                    "Campo obligatorio",
                    "Selecciona una ubicación."
            );

            return;
        }


        // =====================================================
        // VALIDAR UNIDAD
        // =====================================================

        if (unidad == null ||
                unidad.isBlank()) {

            unidad = "Pieza";
        }


        // =====================================================
        // OBTENER STOCK
        // =====================================================

        int stockActual =
                spnStockActual.getValue();

        int stockMinimo =
                spnStockMinimo.getValue();


        // =====================================================
        // VALIDAR STOCK
        // =====================================================

        if (stockActual < 0) {

            mostrarAlerta(
                    "Error de validación",
                    "El stock disponible no puede ser negativo."
            );

            return;
        }


        if (stockMinimo < 0) {

            mostrarAlerta(
                    "Error de validación",
                    "El stock mínimo no puede ser negativo."
            );

            return;
        }


        // =====================================================
        // OBTENER ID DE CATEGORÍA
        // =====================================================

        int idCategoria =
                materialDAO.obtenerOCrearCategoria(
                        categoriaTexto.trim()
                );


        // =====================================================
        // OBTENER ID DE UBICACIÓN
        // =====================================================

        int idUbicacion =
                materialDAO.obtenerOCrearUbicacion(
                        ubicacionTexto.trim()
                );


        if (idCategoria == -1 ||
                idUbicacion == -1) {

            mostrarAlerta(
                    "Error de base de datos",
                    "No fue posible obtener la categoría o la ubicación."
            );

            return;
        }


        // =====================================================
        // ACTUALIZAR OBJETO MATERIAL
        // =====================================================

        materialAEditar.setNomMaterial(nombre);

        materialAEditar.setDescripcion(
                txtaObservaciones.getText().trim()
        );

        materialAEditar.setTipo(
                rdbActivo.isSelected()
                        ? "Activo"
                        : "Consumible"
        );

        materialAEditar.setStockActual(
                stockActual
        );

        materialAEditar.setStockMinimo(
                stockMinimo
        );

        materialAEditar.setUnidad(
                unidad
        );

        materialAEditar.setIdCategoria(
                idCategoria
        );

        materialAEditar.setIdUbicacion(
                idUbicacion
        );


        // =====================================================
        // ACTUALIZAR EN BASE DE DATOS
        // =====================================================

        boolean actualizado =
                materialDAO.actualizarMaterial(
                        materialAEditar
                );


        if (actualizado) {

            mostrarMensajeExito();

            cerrarVentana(event);

        } else {

            mostrarAlerta(
                    "Error",
                    "No se pudieron actualizar los cambios en la base de datos."
            );
        }
    }


    // =========================================================
    // CANCELAR
    // =========================================================

    @FXML
    private void cancelarCambios(ActionEvent event) {

        cerrarVentana(event);
    }


    // =========================================================
    // CERRAR VENTANA
    // =========================================================

    @FXML
    private void cerrarVentana(ActionEvent event) {

        Stage stage = null;


        if (event != null &&
                event.getSource() instanceof Button) {

            Button boton =
                    (Button) event.getSource();

            if (boton.getScene() != null) {

                stage =
                        (Stage) boton
                                .getScene()
                                .getWindow();
            }
        }


        if (stage == null &&
                btnCerrarVentana != null &&
                btnCerrarVentana.getScene() != null) {

            stage =
                    (Stage) btnCerrarVentana
                            .getScene()
                            .getWindow();
        }


        if (stage != null) {

            stage.close();
        }
    }


    // =========================================================
    // EVENTOS OPCIONALES
    // =========================================================

    @FXML
    private void nombreMaterial(ActionEvent event) {
        // No requiere lógica adicional actualmente.
    }


    @FXML
    private void seleccionarCategoria(ActionEvent event) {
        // No requiere lógica adicional actualmente.
    }


    @FXML
    private void seleccionarUbicacion(ActionEvent event) {
        // No requiere lógica adicional actualmente.
    }


    @FXML
    private void seleccionarUnidad(ActionEvent event) {
        // No requiere lógica adicional actualmente.
    }


    // =========================================================
    // MENSAJE DE ÉXITO
    // =========================================================

    private void mostrarMensajeExito() {

        Alert alert =
                new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Actualización exitosa");
        alert.setHeaderText(null);

        alert.setContentText(
                "El material se actualizó correctamente."
        );

        alert.showAndWait();
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