package utng.gtid2.dab.controllers.reportes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
    // BOTÓN PDF
    // ============================================================

    @FXML
    private Button btnGenerarPDF;

    // ============================================================
    // INICIALIZAR
    // ============================================================

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        btnReportes.getStyleClass().add("active");

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
        // COLUMNAS DE LA TABLA
        // ========================================================

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

        // ========================================================
        // TABLA
        // ========================================================

        tblVistaPrevia.setColumnResizePolicy(
                TableView.UNCONSTRAINED_RESIZE_POLICY
        );

        colId.setResizable(false);
        colMaterial.setResizable(false);
        colCategoria.setResizable(false);
        colTipo.setResizable(false);
        colCantidad.setResizable(false);
        colStockMin.setResizable(false);
        colEstado.setResizable(false);
        colUbicacion.setResizable(false);
        colFecha.setResizable(false);

        // ========================================================
        // CAMBIO AUTOMÁTICO DE REPORTE
        // ========================================================

        cbTipoReporte.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, nuevo) -> {

                    if (nuevo != null) {

                        buscar(null);
                    }
                });

        // ========================================================
        // SELECCIONAR PRIMER REPORTE
        // ========================================================

        cbTipoReporte.getSelectionModel().selectFirst();
    }

    // ============================================================
    // TEXTO INFORMATIVO
    // ============================================================

    // ============================================================
    // BUSCAR
    // ============================================================

    @FXML
    private void buscar(ActionEvent event) {

        String tipoReporte =
                cbTipoReporte.getValue();

        if (tipoReporte == null) {
            return;
        }

        LocalDate desde =
                dpDesde.getValue();

        LocalDate hasta =
                dpHasta.getValue();

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
        // MOSTRAR RESULTADOS
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

                                    "",

                                    "Préstamo",

                                    rs.getInt(
                                            "cantidad"),

                                    0,

                                    rs.getString(
                                            "estado"),

                                    "",

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

                                    "",

                                    rs.getString(
                                            "rol"),

                                    0,

                                    0,

                                    rs.getString(
                                            "estado"),

                                    "",

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
    // INVENTARIO
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

        String tipoReporte =
                cbTipoReporte.getValue();

        // ========================================================
        // VALIDAR TIPO
        // ========================================================

        if (tipoReporte == null
                || tipoReporte.isEmpty()) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Generar PDF",
                    "Selecciona primero un tipo de reporte."
            );

            return;
        }

        // ========================================================
        // VALIDAR REGISTROS
        // ========================================================

        if (tblVistaPrevia.getItems().isEmpty()) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Generar PDF",
                    "No hay registros para generar el reporte."
            );

            return;
        }

        // ========================================================
        // FILE CHOOSER
        // ========================================================

        FileChooser fileChooser =
                new FileChooser();

        fileChooser.setTitle(
                "Guardar reporte PDF"
        );

        fileChooser.setInitialFileName(
                "Reporte_"
                        + tipoReporte
                        .replace(" ", "_")
                        .replace("á", "a")
                        .replace("é", "e")
                        .replace("í", "i")
                        .replace("ó", "o")
                        .replace("ú", "u")
                        + ".pdf"
        );

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Archivo PDF (*.pdf)",
                        "*.pdf"
                )
        );

        Stage stage =
                (Stage) btnGenerarPDF
                        .getScene()
                        .getWindow();

        File archivo =
                fileChooser.showSaveDialog(stage);

        if (archivo == null) {
            return;
        }

        // ========================================================
        // DOCUMENTO HORIZONTAL
        // ========================================================

        Document documento =
                new Document(
                        PageSize.A4.rotate(),
                        25,
                        25,
                        30,
                        30
                );

        try {

            // ====================================================
            // CORRECCIÓN DEL ERROR DE PdfWriter
            // ====================================================

            FileOutputStream salida =
                    new FileOutputStream(archivo);

            PdfWriter.getInstance(
                    documento,
                    salida
            );

            documento.open();

            // ====================================================
            // FUENTES
            // ====================================================

            Font fuenteTitulo =
                    new Font(
                            Font.HELVETICA,
                            18,
                            Font.BOLD
                    );

            Font fuenteSubtitulo =
                    new Font(
                            Font.HELVETICA,
                            12,
                            Font.BOLD
                    );

            Font fuenteNormal =
                    new Font(
                            Font.HELVETICA,
                            9,
                            Font.NORMAL
                    );

            Font fuenteEncabezado =
                    new Font(
                            Font.HELVETICA,
                            8,
                            Font.BOLD
                    );

            Font fuenteTabla =
                    new Font(
                            Font.HELVETICA,
                            7,
                            Font.NORMAL
                    );

            // ====================================================
            // ENCABEZADO
            // ====================================================

            Paragraph encabezado =
                    new Paragraph(
                            "SISTEMA DE GESTIÓN DE MATERIALES",
                            fuenteSubtitulo
                    );

            encabezado.setAlignment(
                    Element.ALIGN_CENTER
            );

            documento.add(encabezado);

            // ====================================================
            // TÍTULO
            // ====================================================

            Paragraph titulo =
                    new Paragraph(
                            "REPORTE DE "
                                    + tipoReporte.toUpperCase(),
                            fuenteTitulo
                    );

            titulo.setAlignment(
                    Element.ALIGN_CENTER
            );

            documento.add(titulo);

            // ====================================================
            // FECHA Y HORA
            // ====================================================

            DateTimeFormatter formatoFechaHora =
                    DateTimeFormatter.ofPattern(
                            "dd/MM/yyyy HH:mm:ss"
                    );

            Paragraph fechaGeneracion =
                    new Paragraph(
                            "Fecha de generación: "
                                    + LocalDateTime.now()
                                    .format(
                                            formatoFechaHora
                                    ),
                            fuenteNormal
                    );

            fechaGeneracion.setAlignment(
                    Element.ALIGN_CENTER
            );

            documento.add(
                    fechaGeneracion
            );

            documento.add(
                    new Paragraph(" ")
            );

            // ====================================================
            // INFORMACIÓN DEL FILTRO
            // ====================================================

            PdfPTable info =
                    new PdfPTable(2);

            info.setWidthPercentage(100);

            info.setWidths(
                    new float[]{1, 1}
            );

            PdfPCell celdaTipo =
                    new PdfPCell(
                            new Phrase(
                                    "Tipo de reporte:\n"
                                            + tipoReporte,
                                    fuenteNormal
                            )
                    );

            celdaTipo.setPadding(7);

            PdfPCell celdaFechas =
                    new PdfPCell(
                            new Phrase(
                                    obtenerRangoFechas(),
                                    fuenteNormal
                            )
                    );

            celdaFechas.setPadding(7);

            info.addCell(celdaTipo);
            info.addCell(celdaFechas);

            documento.add(info);

            documento.add(
                    new Paragraph(" ")
            );

            // ====================================================
            // TABLA
            // ====================================================

            PdfPTable tabla =
                    new PdfPTable(9);

            tabla.setWidthPercentage(100);

            tabla.setWidths(
                    new float[]{
                            0.6f,
                            2.5f,
                            1.7f,
                            1.2f,
                            0.9f,
                            0.9f,
                            1.3f,
                            2.0f,
                            1.2f
                    }
            );

            // ====================================================
            // ENCABEZADOS
            // ====================================================

            agregarEncabezado(
                    tabla,
                    "ID",
                    fuenteEncabezado
            );

            agregarEncabezado(
                    tabla,
                    "Material",
                    fuenteEncabezado
            );

            agregarEncabezado(
                    tabla,
                    "Categoría",
                    fuenteEncabezado
            );

            agregarEncabezado(
                    tabla,
                    "Tipo",
                    fuenteEncabezado
            );

            agregarEncabezado(
                    tabla,
                    "Cantidad",
                    fuenteEncabezado
            );

            agregarEncabezado(
                    tabla,
                    "Stock min.",
                    fuenteEncabezado
            );

            agregarEncabezado(
                    tabla,
                    "Estado",
                    fuenteEncabezado
            );

            agregarEncabezado(
                    tabla,
                    "Ubicación",
                    fuenteEncabezado
            );

            agregarEncabezado(
                    tabla,
                    "Fecha",
                    fuenteEncabezado
            );

            // ====================================================
            // DATOS
            // ====================================================

            int contadorFila = 0;

            for (ReporteFila fila :
                    tblVistaPrevia.getItems()) {

                agregarCelda(
                        tabla,
                        String.valueOf(
                                fila.getId()
                        ),
                        fuenteTabla,
                        contadorFila
                );

                agregarCelda(
                        tabla,
                        valorSeguro(
                                fila.getMaterial()
                        ),
                        fuenteTabla,
                        contadorFila
                );

                agregarCelda(
                        tabla,
                        valorSeguro(
                                fila.getCategoria()
                        ),
                        fuenteTabla,
                        contadorFila
                );

                agregarCelda(
                        tabla,
                        valorSeguro(
                                fila.getTipo()
                        ),
                        fuenteTabla,
                        contadorFila
                );

                agregarCelda(
                        tabla,
                        String.valueOf(
                                fila.getCantidad()
                        ),
                        fuenteTabla,
                        contadorFila
                );

                agregarCelda(
                        tabla,
                        String.valueOf(
                                fila.getStockMin()
                        ),
                        fuenteTabla,
                        contadorFila
                );

                agregarCelda(
                        tabla,
                        valorSeguro(
                                fila.getEstado()
                        ),
                        fuenteTabla,
                        contadorFila
                );

                agregarCelda(
                        tabla,
                        valorSeguro(
                                fila.getUbicacion()
                        ),
                        fuenteTabla,
                        contadorFila
                );

                agregarCelda(
                        tabla,
                        String.valueOf(
                                fila.getFecha()
                        ),
                        fuenteTabla,
                        contadorFila
                );

                contadorFila++;
            }

            documento.add(tabla);

            documento.add(
                    new Paragraph(" ")
            );

            // ====================================================
            // TOTAL
            // ====================================================

            Paragraph total =
                    new Paragraph(
                            "Total de registros: "
                                    + tblVistaPrevia
                                    .getItems()
                                    .size(),
                            fuenteSubtitulo
                    );

            total.setAlignment(
                    Element.ALIGN_RIGHT
            );

            documento.add(total);

            // ====================================================
            // PIE
            // ====================================================

            documento.add(
                    new Paragraph(" ")
            );

            Paragraph pie =
                    new Paragraph(
                            "Reporte generado por el Sistema de Gestión de Materiales.",
                            fuenteNormal
                    );

            pie.setAlignment(
                    Element.ALIGN_CENTER
            );

            documento.add(pie);

            // ====================================================
            // CERRAR
            // ====================================================

            documento.close();

            salida.close();

            // ====================================================
            // ÉXITO
            // ====================================================

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "PDF generado",
                    "El reporte PDF se generó correctamente."
                            + "\n\nArchivo:"
                            + "\n"
                            + archivo.getAbsolutePath()
            );

        } catch (IOException |
                 DocumentException e) {

            e.printStackTrace();

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error al generar PDF",
                    "No se pudo generar el archivo PDF."
                            + "\n\n"
                            + e.getMessage()
            );
        }
    }

    // ============================================================
    // ENCABEZADO DE TABLA PDF
    // ============================================================

    private void agregarEncabezado(
            PdfPTable tabla,
            String texto,
            Font fuente) {

        PdfPCell celda =
                new PdfPCell(
                        new Phrase(
                                texto,
                                fuente
                        )
                );

        celda.setHorizontalAlignment(
                Element.ALIGN_CENTER
        );

        celda.setVerticalAlignment(
                Element.ALIGN_MIDDLE
        );

        celda.setPadding(6);

        tabla.addCell(celda);
    }

    // ============================================================
    // CELDA DE DATOS PDF
    // ============================================================

    private void agregarCelda(
            PdfPTable tabla,
            String texto,
            Font fuente,
            int fila) {

        PdfPCell celda =
                new PdfPCell(
                        new Phrase(
                                texto,
                                fuente
                        )
                );

        celda.setPadding(4);

        celda.setVerticalAlignment(
                Element.ALIGN_MIDDLE
        );

        tabla.addCell(celda);
    }

    // ============================================================
    // RANGO DE FECHAS
    // ============================================================

    private String obtenerRangoFechas() {

        LocalDate desde =
                dpDesde.getValue();

        LocalDate hasta =
                dpHasta.getValue();

        if (desde == null
                && hasta == null) {

            return "Rango de fechas:\nTodas las fechas";
        }

        if (desde != null
                && hasta != null) {

            return "Rango de fechas:\n"
                    + desde
                    + " hasta "
                    + hasta;
        }

        if (desde != null) {

            return "Rango de fechas:\nDesde "
                    + desde;
        }

        return "Rango de fechas:\nHasta "
                + hasta;
    }

    // ============================================================
    // VALOR SEGURO
    // ============================================================

    private String valorSeguro(
            String valor) {

        if (valor == null
                || valor.trim().isEmpty()) {

            return "-";
        }

        return valor;
    }

    // ============================================================
    // ALERTAS
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