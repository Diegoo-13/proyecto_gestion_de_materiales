package utng.gtid2.dab.controllers.danos;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import utng.gtid2.dab.App;
import utng.gtid2.dab.dao.MaterialDanadoDAO;
import utng.gtid2.dab.modelo.MaterialDanado;
import utng.gtid2.dab.util.Navegador;
import utng.gtid2.dab.util.RelojSistema;

public class MaterialesDanadosController implements Initializable {

    // Instancia para conectar con la Base de Datos
    private final MaterialDanadoDAO materialDanadoDAO = new MaterialDanadoDAO();

    //================ MENÚ =================

    @FXML
    private Button btnInicio;

    @FXML
    private Button btnMaterialesRegistrados;

    @FXML
    private Button btnPrestamosActivos;

    @FXML
    private Button btnMaterialesDanados;

    @FXML
    private Button btnReportes;

    @FXML
    private Button btnUsuarios;

    @FXML
    private Button btnCuenta;

    //================ FILTROS =================

    @FXML
    private TextField txtBuscar;

    @FXML
    private ComboBox<String> cbEstado;

    @FXML
    private ComboBox<String> cbCategoria;

    @FXML
    private Button btnBuscar;

    @FXML
    private Button btnLimpiar;

    //================ TARJETAS =================

    @FXML
    private Label lblHora;

    @FXML
    private Label lblFecha;

    @FXML
    private Label lblTotalReportados;

    @FXML
    private Label lblEnEvaluacion;

    @FXML
    private Label lblBajaDefinitiva;

    //================ TABLA =================

    @FXML
    private TableView<MaterialDanado> tblMateriales;

    @FXML
    private TableColumn<MaterialDanado, Integer> colId;

    @FXML
    private TableColumn<MaterialDanado, Integer> colMaterial;

    @FXML
    private TableColumn<MaterialDanado, String> colCategoria;

    @FXML
    private TableColumn<MaterialDanado, LocalDate> colFecha;

    @FXML
    private TableColumn<MaterialDanado, Integer> colReporto;

    @FXML
    private TableColumn<MaterialDanado, String> colEstado;

    @FXML
    private TableColumn<MaterialDanado, String> colObservaciones;

    //================ BOTONES =================

    @FXML
    private Button btnReportarDano;

    @FXML
    private Button btnRestaurar;

    @FXML
    private Button btnBaja;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //==============FECHA Y HORA ====================
        RelojSistema.iniciar(lblHora, lblFecha);

        // ComboBox Estado
        cbEstado.getItems().addAll(
                "Todos",
                "En evaluación",
                "Reparado",
                "Baja definitiva");

        cbEstado.getSelectionModel().selectFirst();

        // ComboBox Categoría
        cbCategoria.getItems().addAll(
                "Todas",
                "Computadoras",
                "Periféricos",
                "Redes",
                "Herramientas",
                "Papelería");

        cbCategoria.getSelectionModel().selectFirst();

        //================ TABLA =================

        tblMateriales.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        colId.setResizable(false);
        colMaterial.setResizable(false);
        colCategoria.setResizable(false);
        colFecha.setResizable(false);
        colReporto.setResizable(false);
        colEstado.setResizable(false);
        colObservaciones.setResizable(false);

        //================ VINCULAR COLUMNAS =================
        colId.setCellValueFactory(new PropertyValueFactory<>("idMaterialDanado"));
        colMaterial.setCellValueFactory(new PropertyValueFactory<>("idMaterial"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaReporte"));
        colReporto.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colObservaciones.setCellValueFactory(new PropertyValueFactory<>("descripcionDanio"));

        // Cargar datos
        cargarTabla();
    }

    private void cargarTabla() {
        List<MaterialDanado> lista = materialDanadoDAO.listar();
        tblMateriales.setItems(FXCollections.observableArrayList(lista));

        // Actualizar contadores
        lblTotalReportados.setText(String.valueOf(lista.size()));
        long enEval = lista.stream().filter(m -> "Reportado".equalsIgnoreCase(m.getEstado()) || "En revisión".equalsIgnoreCase(m.getEstado())).count();
        long bajas = lista.stream().filter(m -> "Dado de baja".equalsIgnoreCase(m.getEstado())).count();
        lblEnEvaluacion.setText(String.valueOf(enEval));
        lblBajaDefinitiva.setText(String.valueOf(bajas));
    }

    //================ NAVEGACIÓN =================

    @FXML
    private void inicio(ActionEvent event) throws IOException {

        App.setRoot("inicio/Inicio");

    }

    @FXML
    private void materialesRegistrados(ActionEvent event) throws IOException {

        App.setRoot("materiales/MaterialesRegistrados");

    }

    @FXML
    private void prestamosActivos(ActionEvent event) throws IOException {

        App.setRoot("prestamos/PrestamosActivos");

    }

    @FXML
    private void materialesDanados(ActionEvent event) {

        // Ya estamos aquí.

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

    //================ BOTONES =================

    @FXML
    private void buscar(ActionEvent event) {

        String texto = txtBuscar.getText().toLowerCase().trim();
        List<MaterialDanado> todos = materialDanadoDAO.listar();
        ObservableList<MaterialDanado> filtrados = FXCollections.observableArrayList();

        for (MaterialDanado md : todos) {
            if (texto.isEmpty() || md.getDescripcionDanio().toLowerCase().contains(texto)) {
                filtrados.add(md);
            }
        }
        tblMateriales.setItems(filtrados);

    }

    @FXML
    private void limpiar(ActionEvent event) {

        txtBuscar.clear();

        cbEstado.getSelectionModel().selectFirst();
        cbCategoria.getSelectionModel().selectFirst();

        cargarTabla();

    }

    @FXML
    private void reportarDano(ActionEvent event) throws IOException {

        Navegador.abrirModal(
                "danos/ReportarDano",
                "Reportar daño");
        cargarTabla();

    }

    @FXML
    private void restaurar(ActionEvent event) {

        // Lógica de restauración
        cargarTabla();

    }

    @FXML
    private void baja(ActionEvent event) throws IOException {

        Navegador.abrirModal(
                "danos/DarBaja",
                "Dar de baja");
        cargarTabla();

    }

}