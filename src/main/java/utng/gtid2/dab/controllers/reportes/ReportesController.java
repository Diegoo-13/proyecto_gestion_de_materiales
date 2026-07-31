package utng.gtid2.dab.controllers.reportes;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import utng.gtid2.dab.App;
import utng.gtid2.dab.util.RelojSistema;

public class ReportesController implements Initializable {

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
    private ComboBox<String> cbTipoReporte;

    @FXML
    private DatePicker dpDesde;

    @FXML
    private DatePicker dpHasta;

    @FXML
    private Button btnBuscar;

    @FXML
    private Button btnLimpiar;

    //================ INFORMACIÓN =================

    @FXML
    private Label lblHora;

    @FXML
    private Label lblFecha;

    @FXML
    private Label lblTotalRegistros;

    //================ TABLA =================

    @FXML
    private TableView<?> tblVistaPrevia;

    @FXML
    private TableColumn<?, ?> colId;

    @FXML
    private TableColumn<?, ?> colMaterial;

    @FXML
    private TableColumn<?, ?> colCategoria;

    @FXML
    private TableColumn<?, ?> colTipo;

    @FXML
    private TableColumn<?, ?> colCantidad;

    @FXML
    private TableColumn<?, ?> colStockMin;

    @FXML
    private TableColumn<?, ?> colEstado;

    @FXML
    private TableColumn<?, ?> colUbicacion;

    @FXML
    private TableColumn<?, ?> colFecha;

    //================ BOTONES =================

    @FXML
    private Button btnGenerarPDF;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //==============FECHA Y HORA ====================
        RelojSistema.iniciar(lblHora, lblFecha);


        // Etiqueta
        lblTotalRegistros.setText(
                "ℹ Se encontraron 0 registros para el reporte seleccionado.");

        // Tipos de reporte

        cbTipoReporte.getItems().addAll(
                "Materiales registrados",
                "Préstamos",
                "Materiales dañados",
                "Usuarios",
                "Inventario general",
                "Bajas Definitivas");

        cbTipoReporte.getSelectionModel().selectFirst();

        //================ BLOQUEAR TABLA =================

        tblVistaPrevia.setColumnResizePolicy(
                TableView.UNCONSTRAINED_RESIZE_POLICY);

        colId.setResizable(false);
        colMaterial.setResizable(false);
        colCategoria.setResizable(false);
        colTipo.setResizable(false);
        colCantidad.setResizable(false);
        colStockMin.setResizable(false);
        colEstado.setResizable(false);
        colUbicacion.setResizable(false);
        colFecha.setResizable(false);

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
    private void materialesDanados(ActionEvent event) throws IOException {

        App.setRoot("danos/MaterialesDanados");

    }

    @FXML
    private void reportes(ActionEvent event) {

        // Ya estamos aquí.

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

        // Después con PostgreSQL

    }

    @FXML
    private void limpiar(ActionEvent event) {

        cbTipoReporte.getSelectionModel().selectFirst();

        dpDesde.setValue(null);
        dpHasta.setValue(null);

        lblTotalRegistros.setText(
                "ℹ Se encontraron 0 registros para el reporte seleccionado.");

    }

    @FXML
    private void generarPDF(ActionEvent event) {

        /*
         * Después:
         *
         * ReporteDAO.generarPDF(...)
         */

    }

}