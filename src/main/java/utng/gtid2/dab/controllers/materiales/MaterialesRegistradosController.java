package utng.gtid2.dab.controllers.materiales;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import utng.gtid2.dab.App;
import utng.gtid2.dab.dao.MaterialDAO;
import utng.gtid2.dab.modelo.Material;
import utng.gtid2.dab.util.RelojSistema;

public class MaterialesRegistradosController implements Initializable {

    @FXML private TableView<Material> tblMateriales;
    @FXML private TableColumn<Material, Integer> colId;
    @FXML private TableColumn<Material, String> colNombre;
    @FXML private TableColumn<Material, String> colCategoria;
    @FXML private TableColumn<Material, String> colTipo;
    @FXML private TableColumn<Material, Integer> colTotal;
    @FXML private TableColumn<Material, Integer> colDisponibles;
    @FXML private TableColumn<Material, Integer> colStockMinimo;
    @FXML private TableColumn<Material, String> colEstado;
    @FXML private TableColumn<Material, String> colUbicacion;

    @FXML private TextField txtBuscarMaterial;
    @FXML private Button btnBuscarMaterial;
    @FXML private Button btnLimpiar;
    @FXML private Button btnStockBajo;
    @FXML private Button btnAgregar;
    @FXML private Button btnEditar;
    @FXML
    private Button btnEliminar;

    @FXML private HBox lblHoraFecha;
    @FXML private Label lblHora;
    @FXML private Label lblFecha;

    @FXML private Button btnInicio;

    @FXML private Button btnMaterialesRegistrados;
    @FXML private Button btnPrestamosActivos;
    @FXML private Button btnMaterialesDanados;
    @FXML private Button btnReportes;
    @FXML private Button btnUsuarios;
    @FXML private Button btnCuenta;

    private MaterialDAO materialDAO = new MaterialDAO();
        private ObservableList<Material> listaMateriales = FXCollections.observableArrayList();

        @Override
        public void initialize(URL url, ResourceBundle rb) {

        //==============FECHA Y HORA ====================
        RelojSistema.iniciar(lblHora, lblFecha);

            configurarColumnas();

            // Bloquear edición del contenido
            tblMateriales.setEditable(false);

            // Bloquear cambio de tamaño de las columnas
            colId.setResizable(false);
            colNombre.setResizable(false);
            colCategoria.setResizable(false);
            colTipo.setResizable(false);
            colTotal.setResizable(false);
            colDisponibles.setResizable(false);
            colStockMinimo.setResizable(false);
            colEstado.setResizable(false);
            colUbicacion.setResizable(false);

            cargarDatosTabla();
        
    }

    private void configurarColumnas() {
        if (colId != null) colId.setCellValueFactory(new PropertyValueFactory<>("idMaterial"));
        if (colNombre != null) colNombre.setCellValueFactory(new PropertyValueFactory<>("nomMaterial"));
        if (colCategoria != null) colCategoria.setCellValueFactory(new PropertyValueFactory<>("nomCategoria"));
        if (colTipo != null) colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        if (colTotal != null) colTotal.setCellValueFactory(new PropertyValueFactory<>("stockMaximo"));
        if (colDisponibles != null) colDisponibles.setCellValueFactory(new PropertyValueFactory<>("stockMaximo"));
        if (colStockMinimo != null) colStockMinimo.setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));
        if (colEstado != null) colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        if (colUbicacion != null) colUbicacion.setCellValueFactory(new PropertyValueFactory<>("nomUbicacion"));

    }

    public void cargarDatosTabla() {
        List<Material> resultado = materialDAO.obtenerTodosLosMateriales();
            listaMateriales.setAll(resultado);
        if (tblMateriales != null) {
            tblMateriales.setItems(listaMateriales);
        }
        actualizarConteoStockBajo();
    }

    private void actualizarConteoStockBajo() {
        long enStockBajo = listaMateriales.stream().filter(m -> m.getStockMaximo() <= m.getStockMinimo()).count();

        if (btnStockBajo != null) {
            btnStockBajo.setText("Stock Bajo ( " + enStockBajo + " )");
        }
    }

    @FXML
    private void buscarMaterial(ActionEvent event) {
        String query = txtBuscarMaterial.getText().trim();
        if (query.isEmpty()) {
            cargarDatosTabla();
        return;
        }
        List<Material> filtrados = materialDAO.buscarMateriales(query);
            listaMateriales.setAll(filtrados);
            tblMateriales.setItems(listaMateriales);
    }

    @FXML
    private void limpiar(ActionEvent event) {
        if (txtBuscarMaterial != null) txtBuscarMaterial.clear();
        cargarDatosTabla();
    }

    @FXML
    private void filtrarStockBajo(ActionEvent event) {
        ObservableList<Material> filtrados = listaMateriales.filtered(m -> m.getStockMaximo() <=m.getStockMinimo());
        tblMateriales.setItems(filtrados);
    }

    @FXML
    private void agregarMaterial(ActionEvent event) {
        try {
            FXMLLoader loader = new
            FXMLLoader(getClass().getResource("/utng/gtid2/dab/materiales/AgregarMaterial.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Agregar Material");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            cargarDatosTabla();
            } catch (IOException e) {
                e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la vista AgregarMaterial.fxml");
        }
    }

    @FXML
    private void editarMaterial(ActionEvent event) {
        Material seleccionado = tblMateriales.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Atención", "Por favor selecciona un material de la tabla para editar.");
        return;
        }

        try {
            FXMLLoader loader = new
            FXMLLoader(getClass().getResource("/utng/gtid2/dab/materiales/EditarMaterial.fxml"));
            Parent root = loader.load();

            EditarMaterialController controller = loader.getController();
            controller.cargarMaterial(seleccionado);

            Stage stage = new Stage();
            stage.setTitle("Editar Material");
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.setScene(new Scene(root));
            stage.showAndWait();
            cargarDatosTabla();
            } catch (IOException e) {
                e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la vista EditarMaterial.fxml");
        }
    }

    @FXML
    private void eliminarMaterial(ActionEvent event) {

        Material seleccionado = tblMateriales.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Atención", "Selecciona un material para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Eliminar material");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText(
                "¿Deseas eliminar el material \"" +
                seleccionado.getNomMaterial() + "\"?");

        Optional<ButtonType> respuesta = confirmacion.showAndWait();

        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {

            if (materialDAO.eliminarMaterial(seleccionado.getIdMaterial())) {

                mostrarAlerta("Éxito", "Material eliminado correctamente.");
                cargarDatosTabla();

            } else {

                mostrarAlerta("Error", "No fue posible eliminar el material.");

            }
        }
    }

    // Navegación conectada con las mismas rutas del InicioController
    @FXML
    private void inicio(ActionEvent event) throws IOException {
        App.setRoot("inicio/Inicio");
    }

    @FXML
    private void materialesRegistrados(ActionEvent event) throws IOException {
        cargarDatosTabla();
    }

    @FXML
    private void prestamosActivos(ActionEvent event) throws IOException {
        App.setRoot("prestamos/PrestamosActivos");
    }

    @FXML
    private void materialesDanados(ActionEvent event) throws IOException {
        App.setRoot("danos/MaterialesDanados");
    }

    @FXML
    private void reportes(ActionEvent event) throws IOException {
        App.setRoot("reportes/Reportes");
    }

    @FXML
    private void usuarios(ActionEvent event) throws IOException {
        App.setRoot("usuarios/Usuarios");
    }

    @FXML
    private void cuenta(ActionEvent event) throws IOException {
        App.setRoot("cuenta/Cuenta");
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}