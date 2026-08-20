package utng.gtid2.dab.controllers.reportes;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import utng.gtid2.dab.App;
import utng.gtid2.dab.conexionbd.Conexion;
import utng.gtid2.dab.modelo.ReporteFila;
import utng.gtid2.dab.util.RelojSistema;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;
import javafx.stage.FileChooser;
import java.io.File;

import java.io.FileOutputStream;

public class ReportesController implements Initializable {

    // ============================================================
    // MENÚ
    // ============================================================

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

    // ============================================================
    // FILTROS
    // ============================================================

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

    // ============================================================
    // INFORMACIÓN
    // ============================================================

    @FXML
    private Label lblHora;

    @FXML
    private Label lblFecha;

    @FXML
    private Label lblTotalRegistros;

    // ============================================================
    // TABLA
    // ============================================================

    @FXML
    private TableView<ReporteFila> tblVistaPrevia;

    @FXML
    private TableColumn<ReporteFila, Integer> colId;

    @FXML
    private TableColumn<ReporteFila, String> colMaterial;

    @FXML
    private TableColumn<ReporteFila, String> colCategoria;

    @FXML
    private TableColumn<ReporteFila, String> colTipo;

    @FXML
    private TableColumn<ReporteFila, Integer> colCantidad;

    @FXML
    private TableColumn<ReporteFila, Integer> colStockMin;

    @FXML
    private TableColumn<ReporteFila, String> colEstado;

    @FXML
    private TableColumn<ReporteFila, String> colUbicacion;

    @FXML
    private TableColumn<ReporteFila, LocalDate> colFecha;

    // ============================================================
    // PDF
    // ============================================================

    @FXML
    private Button btnGenerarPDF;

    // ============================================================
    // INICIALIZAR
    // ============================================================

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // ========================================================
        // BOTÓN ACTIVO
        // ========================================================

        if (btnReportes != null) {
            btnReportes.getStyleClass().add("active");
        }

        // ========================================================
        // RELOJ
        // ========================================================

        RelojSistema.iniciar(lblHora, lblFecha);

        // ========================================================
        // TIPOS DE REPORTE
        // ========================================================

        cbTipoReporte.getItems().clear();

        cbTipoReporte.getItems().addAll(
                "Materiales registrados",
                "Préstamos",
                "Materiales dañados",
                "Usuarios",
                "Inventario general",
                "Bajas Definitivas"
        );

        // ========================================================
        // CONFIGURAR COLUMNAS
        // ========================================================

        configurarColumnas();

        // ========================================================
        // CONFIGURACIÓN DE LA TABLA
        // ========================================================

        /*
         * La tabla conserva siempre un ancho de 1200 px.
         */
        tblVistaPrevia.setPrefWidth(1200);
        tblVistaPrevia.setMinWidth(1200);
        tblVistaPrevia.setMaxWidth(1200);

        /*
         * Las columnas no se podrán redimensionar manualmente.
         */
        bloquearRedimensionColumnas();

        // ========================================================
        // CAMBIO AUTOMÁTICO DE REPORTE
        // ========================================================

        cbTipoReporte.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, nuevo) -> {

                    if (nuevo != null) {

                        configurarColumnasPorReporte(nuevo);

                        buscar(null);
                    }
                });

        // ========================================================
        // SELECCIONAR PRIMER REPORTE
        // ========================================================

        cbTipoReporte.getSelectionModel().selectFirst();
    }

    // ============================================================
    // CONFIGURAR COLUMNAS
    // ============================================================

    private void configurarColumnas() {

        colId.setCellValueFactory(
                new PropertyValueFactory<>("id"));

        colMaterial.setCellValueFactory(
                new PropertyValueFactory<>("material"));

        colCategoria.setCellValueFactory(
                new PropertyValueFactory<>("categoria"));

        colTipo.setCellValueFactory(
                new PropertyValueFactory<>("tipo"));

        colCantidad.setCellValueFactory(
                new PropertyValueFactory<>("cantidad"));

        colStockMin.setCellValueFactory(
                new PropertyValueFactory<>("stockMin"));

        colEstado.setCellValueFactory(
                new PropertyValueFactory<>("estado"));

        colUbicacion.setCellValueFactory(
                new PropertyValueFactory<>("ubicacion"));

        colFecha.setCellValueFactory(
                new PropertyValueFactory<>("fecha"));

        bloquearRedimensionColumnas();
    }

    // ============================================================
    // BLOQUEAR REDIMENSIONAMIENTO
    // ============================================================

    private void bloquearRedimensionColumnas() {

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

    // ============================================================
    // DISTRIBUIR COLUMNAS EN LOS 1200 PX DE LA TABLA
    // ============================================================

    private void distribuirColumnas() {

        final double ANCHO_TABLA = 1200.0;

        TableColumn<ReporteFila, ?>[] columnas = new TableColumn[]{
                colId,
                colMaterial,
                colCategoria,
                colTipo,
                colCantidad,
                colStockMin,
                colEstado,
                colUbicacion,
                colFecha
        };

        // ========================================================
        // CONTAR COLUMNAS VISIBLES
        // ========================================================

        int visibles = 0;

        for (TableColumn<ReporteFila, ?> columna : columnas) {

            if (columna.isVisible()) {
                visibles++;
            }
        }

        if (visibles == 0) {
            return;
        }

        // ========================================================
        // ANCHOS ESPECIALES
        // ========================================================

        double anchoUbicacion = 0;
        double anchoMaterial = 0;
        

        /*
         * Ubicación tendrá mayor espacio solamente
         * cuando forme parte del reporte actual.
         */
        if (colUbicacion.isVisible()) {
            anchoUbicacion = 220;
        }

        /*
         * Material tendrá mayor espacio solamente
         * cuando forme parte del reporte actual.
         */
        if (colMaterial.isVisible()) {
            anchoMaterial = 220;
        }

       

        // ========================================================
        // CONTAR COLUMNAS ESPECIALES
        // ========================================================

        int columnasEspeciales = 0;

        if (colUbicacion.isVisible()) {
            columnasEspeciales++;
        }

        if (colMaterial.isVisible()) {
            columnasEspeciales++;
        }

        // ========================================================
        // ESPACIO PARA LAS DEMÁS COLUMNAS
        // ========================================================

        double espacioRestante =
                ANCHO_TABLA
                        - anchoUbicacion
                        - anchoMaterial;

        int otrasColumnas =
                visibles - columnasEspeciales;

        double anchoNormal = 0;

        if (otrasColumnas > 0) {

            anchoNormal =
                    espacioRestante / otrasColumnas;
        }

        // ========================================================
        // ASIGNAR ANCHOS
        // ========================================================

        for (TableColumn<ReporteFila, ?> columna : columnas) {

            if (!columna.isVisible()) {
                continue;
            }

            double ancho;

            // ----------------------------------------------------
            // MATERIAL
            // ----------------------------------------------------

            if (columna == colMaterial) {

                ancho = anchoMaterial;
            }

            // ----------------------------------------------------
            // UBICACIÓN
            // ----------------------------------------------------

            else if (columna == colUbicacion) {

                ancho = anchoUbicacion;
            }

            // ----------------------------------------------------
            // DEMÁS COLUMNAS
            // ----------------------------------------------------

            else {

                ancho = anchoNormal;
            }

            columna.setPrefWidth(ancho);
            columna.setMinWidth(ancho);
            columna.setMaxWidth(ancho);
            columna.setResizable(false);
        }

        // ========================================================
        // TABLA SIEMPRE DE 1200 PX
        // ========================================================

        tblVistaPrevia.setPrefWidth(ANCHO_TABLA);
        tblVistaPrevia.setMinWidth(ANCHO_TABLA);
        tblVistaPrevia.setMaxWidth(ANCHO_TABLA);

        tblVistaPrevia.refresh();
    }

    // ============================================================
    // COLUMNAS SEGÚN EL REPORTE
    // ============================================================

    private void configurarColumnasPorReporte(
            String tipoReporte) {

         // Encabezado original para todos los reportes
        colUbicacion.setText("Ubicación");
        colFecha.setText("Fecha de Entrada"); 

        // ========================================================
        // OCULTAR TODAS LAS COLUMNAS
        // ========================================================

        colId.setVisible(false);
        colMaterial.setVisible(false);
        colCategoria.setVisible(false);
        colTipo.setVisible(false);
        colCantidad.setVisible(false);
        colStockMin.setVisible(false);
        colEstado.setVisible(false);
        colUbicacion.setVisible(false);
        colFecha.setVisible(false);

        // ========================================================
        // MATERIALES REGISTRADOS
        // ========================================================

        if (tipoReporte.equals("Materiales registrados")) {

            colId.setVisible(true);
            colMaterial.setVisible(true);
            colCategoria.setVisible(true);
            colTipo.setVisible(true);
            colCantidad.setVisible(true);
            colStockMin.setVisible(true);
            colEstado.setVisible(true);
            colUbicacion.setVisible(true);
            colFecha.setVisible(true);
        }

        // ========================================================
        // PRÉSTAMOS
        // ========================================================

        else if (tipoReporte.equals("Préstamos")) {

            colId.setVisible(true);
            colMaterial.setVisible(true);
            colTipo.setVisible(true);
            colCantidad.setVisible(true);
            colEstado.setVisible(true);
            colFecha.setVisible(true);
        }

        // ========================================================
        // MATERIALES DAÑADOS
        // ========================================================

        else if (tipoReporte.equals("Materiales dañados")) {

            colId.setVisible(true);
            colMaterial.setVisible(true);
            colCategoria.setVisible(true);
            colTipo.setVisible(true);
            colEstado.setVisible(true);
            colUbicacion.setVisible(true);
            colFecha.setVisible(true);
        }

        // ========================================================
        // USUARIOS
        // ========================================================

        else if (tipoReporte.equals("Usuarios")) {

            colId.setVisible(true);
            colMaterial.setVisible(true);
            colTipo.setVisible(true);
            colEstado.setVisible(true);
            colFecha.setVisible(true);

            colFecha.setText("Fecha de Creación");
        }

        // ========================================================
        // INVENTARIO GENERAL
        // ========================================================

        else if (tipoReporte.equals("Inventario general")) {

            colId.setVisible(true);
            colMaterial.setVisible(true);
            colCategoria.setVisible(true);
            colTipo.setVisible(true);
            colCantidad.setVisible(true);
            colStockMin.setVisible(true);
            colEstado.setVisible(true);
            colUbicacion.setVisible(true);
            colFecha.setVisible(true);
        }

        // ========================================================
        // BAJAS DEFINITIVAS
        // ========================================================

        else if (tipoReporte.equals("Bajas Definitivas")) {

                colId.setVisible(true);
                colMaterial.setVisible(true);
                colCategoria.setVisible(true);
                colTipo.setVisible(true);
                colEstado.setVisible(true);
                colUbicacion.setVisible(true);
                colFecha.setVisible(true);

                // En bajas definitivas, esta columna representa
                // el motivo/daño de la baja.
                colUbicacion.setText("Daños");
        }

        // ========================================================
        // MANTENER TABLA EN 1200 PX
        // ========================================================

        tblVistaPrevia.setPrefWidth(1200);
        tblVistaPrevia.setMinWidth(1200);
        tblVistaPrevia.setMaxWidth(1200);

        bloquearRedimensionColumnas();

        // ========================================================
        // DISTRIBUIR COLUMNAS
        // ========================================================

        distribuirColumnas();
    }

    // ============================================================
    // BUSCAR
    // ============================================================

    @FXML
    private void buscar(ActionEvent event) {

        String tipoReporte = cbTipoReporte.getValue();

        if (tipoReporte == null) {
            return;
        }

        LocalDate desde = dpDesde.getValue();
        LocalDate hasta = dpHasta.getValue();

        // ========================================================
        // VALIDAR FECHAS
        // ========================================================

        if (desde != null
                && hasta != null
                && desde.isAfter(hasta)) {

            lblTotalRegistros.setText(
                    "⚠ La fecha 'Desde' no puede ser mayor que 'Hasta'."
            );

            return;
        }

        // ========================================================
        // LISTA DE DATOS
        // ========================================================

        ObservableList<ReporteFila> datos =
                FXCollections.observableArrayList();

        // ========================================================
        // SELECCIONAR REPORTE
        // ========================================================

        switch (tipoReporte) {

            case "Materiales registrados":

                cargarMateriales(
                        datos,
                        desde,
                        hasta
                );

                break;

            case "Préstamos":

                cargarPrestamos(
                        datos,
                        desde,
                        hasta
                );

                break;

            case "Materiales dañados":

                cargarMaterialesDanados(
                        datos,
                        desde,
                        hasta
                );

                break;

            case "Usuarios":

                cargarUsuarios(
                        datos,
                        desde,
                        hasta
                );

                break;

            case "Inventario general":

                cargarInventario(
                        datos,
                        desde,
                        hasta
                );

                break;

            case "Bajas Definitivas":

                cargarBajas(
                        datos,
                        desde,
                        hasta
                );

                break;
        }

        // ========================================================
        // MOSTRAR DATOS
        // ========================================================

        tblVistaPrevia.setItems(datos);

        lblTotalRegistros.setText(
                "ℹ Se encontraron "
                        + datos.size()
                        + " registros para: "
                        + tipoReporte
        );
    }

    // ============================================================
    // MATERIALES REGISTRADOS
    // ============================================================

    private void cargarMateriales(
            ObservableList<ReporteFila> datos,
            LocalDate desde,
            LocalDate hasta) {

        String sql =
                "SELECT m.id_material, "
                        + "m.nom_material, "
                        + "c.nom_categoria, "
                        + "m.tipo, "
                        + "m.stock_actual, "
                        + "m.stock_minimo, "
                        + "m.estado, "
                        + "u.nom_ubicacion, "
                        + "m.fecha_registro "
                        + "FROM material m "
                        + "INNER JOIN categoria c "
                        + "ON m.id_categoria = c.id_categoria "
                        + "INNER JOIN ubicacion u "
                        + "ON m.id_ubicacion = u.id_ubicacion "
                        + "WHERE 1=1 ";

        if (desde != null) {

            sql +=
                    "AND m.fecha_registro >= ? ";
        }

        if (hasta != null) {

            sql +=
                    "AND m.fecha_registro <= ? ";
        }

        sql +=
                "ORDER BY m.id_material";

        try (Connection con =
                     Conexion.getConnection();

             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            int parametro = 1;

            if (desde != null) {

                ps.setObject(
                        parametro++,
                        desde
                );
            }

            if (hasta != null) {

                ps.setObject(
                        parametro++,
                        hasta
                );
            }

            try (ResultSet rs =
                         ps.executeQuery()) {

                while (rs.next()) {

                    datos.add(
                            new ReporteFila(

                                    rs.getInt(
                                            "id_material"),

                                    rs.getString(
                                            "nom_material"),

                                    rs.getString(
                                            "nom_categoria"),

                                    rs.getString(
                                            "tipo"),

                                    rs.getInt(
                                            "stock_actual"),

                                    rs.getInt(
                                            "stock_minimo"),

                                    rs.getString(
                                            "estado"),

                                    rs.getString(
                                            "nom_ubicacion"),

                                    rs.getObject(
                                            "fecha_registro",
                                            LocalDate.class)
                            )
                    );
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    // ============================================================
    // PRÉSTAMOS
    // ============================================================

    private void cargarPrestamos(
            ObservableList<ReporteFila> datos,
            LocalDate desde,
            LocalDate hasta) {

        String sql =
                "SELECT p.id_prestamo, "
                        + "m.nom_material, "
                        + "p.cantidad, "
                        + "p.estado, "
                        + "p.fecha_prestamo "
                        + "FROM prestamo p "
                        + "LEFT JOIN material m "
                        + "ON p.id_material = m.id_material "
                        + "WHERE 1=1 ";

        if (desde != null) {

            sql +=
                    "AND p.fecha_prestamo >= ? ";
        }

        if (hasta != null) {

            sql +=
                    "AND p.fecha_prestamo <= ? ";
        }

        sql +=
                "ORDER BY p.id_prestamo";

        try (Connection con =
                     Conexion.getConnection();

             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            int parametro = 1;

            if (desde != null) {

                ps.setObject(
                        parametro++,
                        desde
                );
            }

            if (hasta != null) {

                ps.setObject(
                        parametro++,
                        hasta
                );
            }

            try (ResultSet rs =
                         ps.executeQuery()) {

                while (rs.next()) {

                    datos.add(
                            new ReporteFila(

                                    rs.getInt(
                                            "id_prestamo"),

                                    rs.getString(
                                            "nom_material"),

                                    "N/A",

                                    "Préstamo",

                                    rs.getInt(
                                            "cantidad"),

                                    0,

                                    rs.getString(
                                            "estado"),

                                    "N/A",

                                    rs.getObject(
                                            "fecha_prestamo",
                                            LocalDate.class)
                            )
                    );
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    // ============================================================
    // MATERIALES DAÑADOS
    // ============================================================

    private void cargarMaterialesDanados(
            ObservableList<ReporteFila> datos,
            LocalDate desde,
            LocalDate hasta) {

        String sql =
                "SELECT md.id_material_danado, "
                        + "m.nom_material, "
                        + "c.nom_categoria, "
                        + "md.estado, "
                        + "md.fecha_reporte, "
                        + "u.nombre, "
                        + "u.apellido_p "
                        + "FROM material_danado md "
                        + "INNER JOIN material m "
                        + "ON md.id_material = m.id_material "
                        + "INNER JOIN categoria c "
                        + "ON m.id_categoria = c.id_categoria "
                        + "INNER JOIN usuario u "
                        + "ON md.id_usuario = u.id_usuario "
                        + "WHERE 1=1 ";

        if (desde != null) {

            sql +=
                    "AND md.fecha_reporte >= ? ";
        }

        if (hasta != null) {

            sql +=
                    "AND md.fecha_reporte <= ? ";
        }

        sql +=
                "ORDER BY md.id_material_danado";

        try (Connection con =
                     Conexion.getConnection();

             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            int parametro = 1;

            if (desde != null) {

                ps.setObject(
                        parametro++,
                        desde
                );
            }

            if (hasta != null) {

                ps.setObject(
                        parametro++,
                        hasta
                );
            }

            try (ResultSet rs =
                         ps.executeQuery()) {

                while (rs.next()) {

                    datos.add(
                            new ReporteFila(

                                    rs.getInt(
                                            "id_material_danado"),

                                    rs.getString(
                                            "nom_material"),

                                    rs.getString(
                                            "nom_categoria"),

                                    "Daño",

                                    0,

                                    0,

                                    rs.getString(
                                            "estado"),

                                    rs.getString(
                                            "nombre")
                                            + " "
                                            + rs.getString(
                                            "apellido_p"),

                                    rs.getObject(
                                            "fecha_reporte",
                                            LocalDate.class)
                            )
                    );
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    // ============================================================
    // USUARIOS
    // ============================================================

    private void cargarUsuarios(
            ObservableList<ReporteFila> datos,
            LocalDate desde,
            LocalDate hasta) {

        String sql =
                "SELECT id_usuario, "
                        + "nombre, "
                        + "apellido_p, "
                        + "rol, "
                        + "estado, "
                        + "fecha_creacion "
                        + "FROM usuario "
                        + "WHERE 1=1 ";

        if (desde != null) {

            sql +=
                    "AND fecha_creacion >= ? ";
        }

        if (hasta != null) {

            sql +=
                    "AND fecha_creacion <= ? ";
        }

        sql +=
                "ORDER BY id_usuario";

        try (Connection con =
                     Conexion.getConnection();

             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            int parametro = 1;

            if (desde != null) {

                ps.setObject(
                        parametro++,
                        desde
                );
            }

            if (hasta != null) {

                ps.setObject(
                        parametro++,
                        hasta
                );
            }

            try (ResultSet rs =
                         ps.executeQuery()) {

                while (rs.next()) {

                    datos.add(
                            new ReporteFila(

                                    rs.getInt(
                                            "id_usuario"),

                                    rs.getString(
                                            "nombre")
                                            + " "
                                            + rs.getString(
                                            "apellido_p"),

                                    "N/A",

                                    rs.getString(
                                            "rol"),

                                    0,

                                    0,

                                    rs.getString(
                                            "estado"),

                                    "N/A",

                                    rs.getObject(
                                            "fecha_creacion",
                                            LocalDate.class)
                            )
                    );
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    // ============================================================
    // INVENTARIO GENERAL
    // ============================================================

    private void cargarInventario(
            ObservableList<ReporteFila> datos,
            LocalDate desde,
            LocalDate hasta) {

        cargarMateriales(
                datos,
                desde,
                hasta
        );
    }

    // ============================================================
    // BAJAS DEFINITIVAS
    // ============================================================

    private void cargarBajas(
            ObservableList<ReporteFila> datos,
            LocalDate desde,
            LocalDate hasta) {

        String sql =
                "SELECT md.id_material_danado, "
                        + "m.nom_material, "
                        + "c.nom_categoria, "
                        + "md.estado, "
                        + "md.fecha_reporte, "
                        + "md.motivo_baja "
                        + "FROM material_danado md "
                        + "INNER JOIN material m "
                        + "ON md.id_material = m.id_material "
                        + "INNER JOIN categoria c "
                        + "ON m.id_categoria = c.id_categoria "
                        + "WHERE md.estado = 'Dado de baja' ";

        if (desde != null) {

            sql +=
                    "AND md.fecha_reporte >= ? ";
        }

        if (hasta != null) {

            sql +=
                    "AND md.fecha_reporte <= ? ";
        }

        sql +=
                "ORDER BY md.id_material_danado";

        try (Connection con =
                     Conexion.getConnection();

             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            int parametro = 1;

            if (desde != null) {

                ps.setObject(
                        parametro++,
                        desde
                );
            }

            if (hasta != null) {

                ps.setObject(
                        parametro++,
                        hasta
                );
            }

            try (ResultSet rs =
                         ps.executeQuery()) {

                while (rs.next()) {

                    datos.add(
                            new ReporteFila(

                                    rs.getInt(
                                            "id_material_danado"),

                                    rs.getString(
                                            "nom_material"),

                                    rs.getString(
                                            "nom_categoria"),

                                    "Baja",

                                    0,

                                    0,

                                    rs.getString(
                                            "estado"),

                                    rs.getString(
                                            "motivo_baja"),

                                    rs.getObject(
                                            "fecha_reporte",
                                            LocalDate.class)
                            )
                    );
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

         // ============================================================
        // LIMPIAR
        // ============================================================

        @FXML
        private void limpiar(ActionEvent event) {

        dpDesde.setValue(null);

        dpHasta.setValue(null);

        cbTipoReporte
                .getSelectionModel()
                .selectFirst();

        buscar(null);
        }
    // ============================================================
    // GENERAR PDF
    // ============================================================

       @FXML
        private void generarPDF(ActionEvent event) {

        String tipoReporte = cbTipoReporte.getValue();

        if (tipoReporte == null) {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Generar reporte",
                        "Seleccione un tipo de reporte."
                );
                return;
        }

        if (tblVistaPrevia.getItems().isEmpty()) {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Generar reporte",
                        "No hay datos para generar el reporte."
                );
                return;
        }

        // ========================================================
        // ELEGIR UBICACIÓN Y NOMBRE DEL PDF
        // ========================================================

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Guardar reporte PDF");

        fileChooser.setInitialFileName(
                "Reporte_" +
                tipoReporte.replace(" ", "_") +
                ".pdf"
        );

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Archivo PDF (*.pdf)",
                        "*.pdf"
                )
        );

        File archivo = fileChooser.showSaveDialog(
                tblVistaPrevia.getScene().getWindow()
        );

        // Si el usuario cancela
        if (archivo == null) {
                return;
        }

        try {

                // ========================================================
                // DOCUMENTO
                // ========================================================

                Document documento =
                        new Document(
                                PageSize.A4.rotate(),
                                35,
                                35,
                                40,
                                40
                        );

                PdfWriter writer =
                        PdfWriter.getInstance(
                                documento,
                                new FileOutputStream(archivo)
                        );

                documento.open();

                // ========================================================
                // FUENTES
                // ========================================================

                com.lowagie.text.Font fuenteInstitucion =
                        new com.lowagie.text.Font(
                                com.lowagie.text.Font.HELVETICA,
                                11,
                                com.lowagie.text.Font.BOLD,
                                new java.awt.Color(255, 255, 255)
                        );

                com.lowagie.text.Font fuenteTitulo =
                        new com.lowagie.text.Font(
                                com.lowagie.text.Font.HELVETICA,
                                18,
                                com.lowagie.text.Font.BOLD,
                                new java.awt.Color(31, 78, 121)
                        );

                com.lowagie.text.Font fuenteSubtitulo =
                        new com.lowagie.text.Font(
                                com.lowagie.text.Font.HELVETICA,
                                11,
                                com.lowagie.text.Font.NORMAL,
                                new java.awt.Color(80, 80, 80)
                        );

                com.lowagie.text.Font fuenteEncabezado =
                        new com.lowagie.text.Font(
                                com.lowagie.text.Font.HELVETICA,
                                9,
                                com.lowagie.text.Font.BOLD,
                                new java.awt.Color(255, 255, 255)
                        );

                com.lowagie.text.Font fuenteDatos =
                        new com.lowagie.text.Font(
                                com.lowagie.text.Font.HELVETICA,
                                8,
                                com.lowagie.text.Font.NORMAL,
                                new java.awt.Color(45, 45, 45)
                        );

                com.lowagie.text.Font fuenteResumen =
                        new com.lowagie.text.Font(
                                com.lowagie.text.Font.HELVETICA,
                                10,
                                com.lowagie.text.Font.BOLD,
                                new java.awt.Color(31, 78, 121)
                        );

                // ========================================================
                // COLORES INSTITUCIONALES
                // ========================================================

                java.awt.Color azulInstitucional =
                        new java.awt.Color(31, 78, 121);

                java.awt.Color azulClaro =
                        new java.awt.Color(221, 235, 247);

                java.awt.Color grisClaro =
                        new java.awt.Color(245, 245, 245);

                java.awt.Color grisBorde =
                        new java.awt.Color(210, 210, 210);

                // ========================================================
                // ENCABEZADO INSTITUCIONAL
                // ========================================================

                PdfPTable encabezado =
                        new PdfPTable(1);

                encabezado.setWidthPercentage(100);

                PdfPCell celdaInstitucion =
                        new PdfPCell();

                celdaInstitucion.setBackgroundColor(
                        azulInstitucional
                );

                celdaInstitucion.setBorder(
                        PdfPCell.NO_BORDER
                );

                celdaInstitucion.setPaddingTop(10);
                celdaInstitucion.setPaddingBottom(10);
                celdaInstitucion.setPaddingLeft(15);

                Paragraph institucion =
                        new Paragraph(
                                "UNIVERSIDAD TECNOLÓGICA DEL NORTE DE GUANAJUATO",
                                fuenteInstitucion
                        );

                Paragraph cgti =
                        new Paragraph(
                                "CENTRO DE GESTIÓN DE TECNOLOGÍAS DE LA INFORMACIÓN (CGTI)",
                                fuenteInstitucion
                        );

                celdaInstitucion.addElement(institucion);
                celdaInstitucion.addElement(cgti);

                encabezado.addCell(celdaInstitucion);

                documento.add(encabezado);

                documento.add(
                        new Paragraph(" ")
                );

                // ========================================================
                // TÍTULO
                // ========================================================

                Paragraph titulo =
                        new Paragraph(
                                "REPORTE DE GESTIÓN DE MATERIALES",
                                fuenteTitulo
                        );

                titulo.setAlignment(
                        Paragraph.ALIGN_CENTER
                );

                documento.add(titulo);

                Paragraph subtitulo =
                        new Paragraph(
                                "Tipo de reporte: " + tipoReporte,
                                fuenteSubtitulo
                        );

                subtitulo.setAlignment(
                        Paragraph.ALIGN_CENTER
                );

                documento.add(subtitulo);

                documento.add(
                        new Paragraph(" ")
                );

                // ========================================================
                // INFORMACIÓN GENERAL
                // ========================================================

                PdfPTable informacion =
                        new PdfPTable(2);

                informacion.setWidthPercentage(100);

                informacion.setWidths(
                        new float[]{1, 1}
                );

                PdfPCell celdaFecha =
                        new PdfPCell(
                                new Paragraph(
                                        "Fecha de generación: "
                                                + java.time.LocalDate.now(),
                                        fuenteDatos
                                )
                        );

                celdaFecha.setBackgroundColor(
                        grisClaro
                );

                celdaFecha.setBorderColor(
                        grisBorde
                );

                celdaFecha.setPadding(8);

                PdfPCell celdaTotal =
                        new PdfPCell(
                                new Paragraph(
                                        "Total de registros: "
                                                + tblVistaPrevia.getItems().size(),
                                        fuenteResumen
                                )
                        );

                celdaTotal.setBackgroundColor(
                        azulClaro
                );

                celdaTotal.setBorderColor(
                        grisBorde
                );

                celdaTotal.setPadding(8);

                informacion.addCell(celdaFecha);
                informacion.addCell(celdaTotal);

                documento.add(informacion);

                documento.add(
                        new Paragraph(" ")
                );

                // ========================================================
                // TÍTULO DE LA TABLA
                // ========================================================

                Paragraph detalle =
                        new Paragraph(
                                "DETALLE DEL REPORTE",
                                fuenteResumen
                        );

                detalle.setSpacingAfter(6);

                documento.add(detalle);

                // ========================================================
                // TABLA
                // ========================================================

                int numeroColumnas =
                        tblVistaPrevia
                                .getVisibleLeafColumns()
                                .size();

                PdfPTable tabla =
                        new PdfPTable(numeroColumnas);

                tabla.setWidthPercentage(100);

                tabla.setHeaderRows(1);

                // ========================================================
                // ENCABEZADOS
                // ========================================================

                for (TableColumn<ReporteFila, ?> columna
                        : tblVistaPrevia.getVisibleLeafColumns()) {

                PdfPCell celda =
                        new PdfPCell(
                                new Paragraph(
                                        columna.getText(),
                                        fuenteEncabezado
                                )
                        );

                celda.setBackgroundColor(
                        azulInstitucional
                );

                celda.setHorizontalAlignment(
                        PdfPCell.ALIGN_CENTER
                );

                celda.setVerticalAlignment(
                        PdfPCell.ALIGN_MIDDLE
                );

                celda.setPadding(7);

                celda.setBorderColor(
                        java.awt.Color.WHITE
                );

                tabla.addCell(celda);
                }

                // ========================================================
                // DATOS
                // ========================================================

                int filaNumero = 0;

                for (ReporteFila fila
                        : tblVistaPrevia.getItems()) {

                filaNumero++;

                for (TableColumn<ReporteFila, ?> columna
                        : tblVistaPrevia.getVisibleLeafColumns()) {

                        Object valor = null;

                        if (columna == colId) {

                        valor = fila.getId();

                        } else if (columna == colMaterial) {

                        valor = fila.getMaterial();

                        } else if (columna == colCategoria) {

                        valor = fila.getCategoria();

                        } else if (columna == colTipo) {

                        valor = fila.getTipo();

                        } else if (columna == colCantidad) {

                        valor = fila.getCantidad();

                        } else if (columna == colStockMin) {

                        valor = fila.getStockMin();

                        } else if (columna == colEstado) {

                        valor = fila.getEstado();

                        } else if (columna == colUbicacion) {

                        valor = fila.getUbicacion();

                        } else if (columna == colFecha) {

                        valor = fila.getFecha();
                        }

                        PdfPCell celda =
                                new PdfPCell(
                                        new Paragraph(
                                                valor != null
                                                        ? valor.toString()
                                                        : "N/A",
                                                fuenteDatos
                                        )
                                );

                        // Filas alternadas
                        if (filaNumero % 2 == 0) {

                        celda.setBackgroundColor(
                                grisClaro
                        );

                        } else {

                        celda.setBackgroundColor(
                                java.awt.Color.WHITE
                        );
                        }

                        celda.setPadding(6);

                        celda.setBorderColor(
                                grisBorde
                        );

                        celda.setVerticalAlignment(
                                PdfPCell.ALIGN_MIDDLE
                        );

                        tabla.addCell(celda);
                }
                }

                documento.add(tabla);

                // ========================================================
                // PIE DE REPORTE
                // ========================================================

                documento.add(
                        new Paragraph(" ")
                );

                Paragraph pie =
                        new Paragraph(
                                "Sistema de Gestión y Control de Materiales — CGTI",
                                new com.lowagie.text.Font(
                                        com.lowagie.text.Font.HELVETICA,
                                        8,
                                        com.lowagie.text.Font.NORMAL,
                                        new java.awt.Color(100, 100, 100)
                                )
                        );

                pie.setAlignment(
                        Paragraph.ALIGN_CENTER
                );

                documento.add(pie);

                // ========================================================
                // CERRAR DOCUMENTO
                // ========================================================

                documento.close();

                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Reporte generado",
                        "El reporte se guardó correctamente en:\n\n"
                                + archivo.getAbsolutePath()
                );

        } catch (Exception e) {

                e.printStackTrace();

                mostrarAlerta(
                        Alert.AlertType.ERROR,
                        "Error",
                        "No se pudo generar el reporte PDF.\n\n"
                                + e.getMessage()
                );
        }
        }

    // ============================================================
    // ALERTA
    // ============================================================

    private void mostrarAlerta(
            Alert.AlertType tipo,
            String titulo,
            String mensaje) {

        Alert alerta =
                new Alert(tipo);

        alerta.setTitle(titulo);

        alerta.setHeaderText(null);

        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }

    // ============================================================
    // NAVEGACIÓN
    // ============================================================

    @FXML
    private void inicio(ActionEvent event)
            throws IOException {

        App.setRoot(
                "inicio/Inicio"
        );
    }

    @FXML
    private void materialesRegistrados(
            ActionEvent event)
            throws IOException {

        App.setRoot(
                "materiales/MaterialesRegistrados"
        );
    }

    @FXML
    private void prestamosActivos(
            ActionEvent event)
            throws IOException {

        App.setRoot(
                "prestamos/PrestamosActivos"
        );
    }

    @FXML
    private void materialesDanados(
            ActionEvent event)
            throws IOException {

        App.setRoot(
                "danos/MaterialesDanados"
        );
    }

    @FXML
    private void reportes(
            ActionEvent event) {

        // Ya estamos en Reportes.
    }

    @FXML
    private void usuarios(
            ActionEvent event)
            throws IOException {

        App.setRoot(
                "usuarios/Usuarios"
        );
    }

    @FXML
    private void cuenta(
            ActionEvent event)
            throws IOException {

        App.setRoot(
                "cuenta/Cuenta"
        );
    }
}