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
    // VALOR SEGURO
    // ============================================================

    private String valorSeguro(String valor) {

        if (valor == null
                || valor.trim().isEmpty()) {

            return "N/A";
        }

        return valor;
    }

    // ============================================================
    // GENERAR PDF
    // ============================================================

    @FXML
    private void generarPDF(ActionEvent event) {

        /*
         * Este método conserva el espacio para la generación
         * de PDF actual.
         */
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