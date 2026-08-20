package utng.gtid2.dab.controllers.materiales;

import java.io.IOException;
import java.net.URL;
import java.util.List;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.TableCell;
import javafx.util.Callback;

import utng.gtid2.dab.App;
import utng.gtid2.dab.dao.MaterialDAO;
import utng.gtid2.dab.modelo.Material;
import utng.gtid2.dab.util.RelojSistema;

public class MaterialesRegistradosController implements Initializable {

    // =========================================================
    // TABLA
    // =========================================================

    @FXML
    private TableView<Material> tblMateriales;

    @FXML
    private TableColumn<Material, Integer> colId;

    @FXML
    private TableColumn<Material, String> colNombre;

    @FXML
    private TableColumn<Material, String> colCategoria;

    @FXML
    private TableColumn<Material, String> colTipo;

    @FXML
    private TableColumn<Material, Integer> colCantidad;

    @FXML
    private TableColumn<Material, Integer> colStockMinimo;

    @FXML
    private TableColumn<Material, String> colEstado;

    @FXML
    private TableColumn<Material, String> colUbicacion;


    // =========================================================
    // BÚSQUEDA Y FILTROS
    // =========================================================

    @FXML
    private TextField txtBuscarMaterial;

    @FXML
    private Button btnBuscarMaterial;

    @FXML
    private Button btnLimpiar;

    @FXML
    private ComboBox<String> cmbCategoria;

    @FXML
    private ComboBox<String> cmbTipo;

    @FXML
    private ComboBox<String> cmbStock;


    // =========================================================
    // CONTADORES
    // =========================================================

    @FXML
    private Label lblMaterialesRegistrados;

    @FXML
    private Label lblActivos;

    @FXML
    private Label lblConsumibles;

    @FXML
    private Label lblStockBajo;


    // =========================================================
    // BOTONES
    // =========================================================

    @FXML
    private Button btnAgregar;

    @FXML
    private Button btnEditar;


    // =========================================================
    // FECHA Y HORA
    // =========================================================

    @FXML
    private HBox lblHoraFecha;

    @FXML
    private Label lblHora;

    @FXML
    private Label lblFecha;


    // =========================================================
    // NAVEGACIÓN
    // =========================================================

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


    // =========================================================
    // DATOS
    // =========================================================

    private final MaterialDAO materialDAO = new MaterialDAO();

    private final ObservableList<Material> todosLosMateriales =
            FXCollections.observableArrayList();

    private final ObservableList<Material> listaMateriales =
            FXCollections.observableArrayList();


    // =========================================================
    // INICIALIZACIÓN
    // =========================================================

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        btnMaterialesRegistrados.getStyleClass().add("active");

        // Fecha y hora
        RelojSistema.iniciar(lblHora, lblFecha);

        // Configurar columnas
        configurarColumnas();

        // Tabla no editable
        tblMateriales.setEditable(false);

        // =========================================================
        // HACER QUE LAS COLUMNAS OCUPEN TODO EL ANCHO DISPONIBLE
        // =========================================================

        tblMateriales.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        // =========================================================
        // BLOQUEAR REDIMENSIONAMIENTO MANUAL
        // =========================================================

        colId.setResizable(false);
        colNombre.setResizable(false);
        colCategoria.setResizable(false);
        colTipo.setResizable(false);
        colCantidad.setResizable(false);
        colStockMinimo.setResizable(false);
        colEstado.setResizable(false);
        colUbicacion.setResizable(false);

        // Cargar materiales
        cargarDatosTabla();

        // Configurar filtros
        configurarFiltros();

        // Verificar si la pantalla fue abierta
        // desde el acceso rápido de Stock Bajo.
        if (App.consumirFiltroStockBajo()) {

            cmbStock.setValue("Bajo");

            aplicarFiltros();
        }

        txtBuscarMaterial.setOnAction(event -> aplicarFiltros());
    }


    // =========================================================
    // CONFIGURAR COLUMNAS
    // =========================================================

    private void configurarColumnas() {

        colId.setCellValueFactory(
                new PropertyValueFactory<>("idMaterial")
        );

        colNombre.setCellValueFactory(
                new PropertyValueFactory<>("nomMaterial")
        );

        colCategoria.setCellValueFactory(
                new PropertyValueFactory<>("nomCategoria")
        );

        colTipo.setCellValueFactory(
                new PropertyValueFactory<>("tipo")
        );

        // NUEVO:
        // La columna Cantidad muestra el stock actual disponible
        colCantidad.setCellValueFactory(
                new PropertyValueFactory<>("stockActual")
        );

        colStockMinimo.setCellValueFactory(
                new PropertyValueFactory<>("stockMinimo")
        );

        colEstado.setCellValueFactory(
                new PropertyValueFactory<>("estado")
        );

        colUbicacion.setCellValueFactory(
                new PropertyValueFactory<>("nomUbicacion")
        );


        // ================= ALINEACIÓN =================

        colId.setStyle("-fx-alignment: CENTER;");

        colNombre.setStyle("-fx-alignment: CENTER-LEFT;");

        colCategoria.setStyle("-fx-alignment: CENTER-LEFT;");

        colTipo.setStyle("-fx-alignment: CENTER;");

        colCantidad.setStyle("-fx-alignment: CENTER;");

        colStockMinimo.setStyle("-fx-alignment: CENTER;");

        colEstado.setStyle("-fx-alignment: CENTER;");

        colUbicacion.setStyle("-fx-alignment: CENTER-LEFT;");


        // =========================================================
        // COLORES DE LA COLUMNA TIPO
        // =========================================================

        colTipo.setCellFactory(
            new Callback<TableColumn<Material, String>, TableCell<Material, String>>() {

                @Override
                public TableCell<Material, String> call(
                        TableColumn<Material, String> column) {

                    return new TableCell<Material, String>() {

                        @Override
                        protected void updateItem(
                                String tipo,
                                boolean empty) {

                            super.updateItem(tipo, empty);

                            if (empty || tipo == null) {

                                setText(null);
                                setTextFill(Color.BLACK);
                                setStyle("-fx-alignment: CENTER;");

                                return;
                            }

                            setText(tipo);

                            setStyle(
                                "-fx-alignment: CENTER; " +
                                "-fx-font-weight: bold;"
                            );

                            // ACTIVO → VERDE
                            if (tipo.equalsIgnoreCase("Activo")) {

                                setTextFill(
                                        Color.web("#16A34A")
                                );

                            // CONSUMIBLE → AMARILLO / ÁMBAR
                            } else if (
                                    tipo.equalsIgnoreCase("Consumible")) {

                                setTextFill(
                                        Color.web("#D97706")
                                );

                            } else {

                                setTextFill(Color.BLACK);
                            }
                        }
                    };
                }
            }
        );


        // =========================================================
        // COLORES Y LÓGICA DE LA COLUMNA ESTADO
        // =========================================================

        colEstado.setCellFactory(
            new Callback<TableColumn<Material, String>, TableCell<Material, String>>() {

                @Override
                public TableCell<Material, String> call(
                        TableColumn<Material, String> column) {

                    return new TableCell<Material, String>() {

                        @Override
                        protected void updateItem(
                                String estado,
                                boolean empty) {

                            super.updateItem(estado, empty);

                            if (empty) {

                                setText(null);
                                setTextFill(Color.BLACK);
                                setStyle("-fx-alignment: CENTER;");

                                return;
                            }

                            // Obtener el material de la fila
                            Material material = getTableView()
                                    .getItems()
                                    .get(getIndex());

                            if (material == null) {

                                setText(null);
                                setTextFill(Color.BLACK);

                                return;
                            }

                            int cantidad =
                                    material.getStockActual();

                            int minimo =
                                    material.getStockMinimo();

                            setStyle(
                                "-fx-alignment: CENTER; " +
                                "-fx-font-weight: bold;"
                            );


                            // =================================================
                            // STOCK BAJO
                            // =================================================

                            if (cantidad <= minimo) {

                                setText("Stock Bajo");

                                setTextFill(
                                        Color.web("#DC2626")
                                );


                            // =================================================
                            // DISPONIBLE
                            // =================================================

                            } else {

                                setText("Disponible");

                                setTextFill(
                                        Color.web("#2563EB")
                                );
                            }
                        }
                    };
                }
            }
        );
    }


    // =========================================================
    // CARGAR DATOS
    // =========================================================

    public void cargarDatosTabla() {

        List<Material> resultado =
                materialDAO.obtenerTodosLosMateriales();

        todosLosMateriales.setAll(resultado);

        listaMateriales.setAll(resultado);

        tblMateriales.setItems(listaMateriales);

        actualizarContadores();

        cargarOpcionesFiltros();

        aplicarFiltros();
    }


    // =========================================================
    // CONTADORES
    // =========================================================

    private void actualizarContadores() {

        long total =
                todosLosMateriales.size();

        long activos =
                todosLosMateriales.stream()
                        .filter(m ->
                                m.getTipo() != null &&
                                m.getTipo()
                                        .equalsIgnoreCase("Activo")
                        )
                        .count();

        long consumibles =
                todosLosMateriales.stream()
                        .filter(m ->
                                m.getTipo() != null &&
                                m.getTipo()
                                        .equalsIgnoreCase("Consumible")
                        )
                        .count();

        long stockBajo =
                todosLosMateriales.stream()
                        .filter(this::esStockBajo)
                        .count();


        lblMaterialesRegistrados.setText(
                String.valueOf(total)
        );

        lblActivos.setText(
                String.valueOf(activos)
        );

        lblConsumibles.setText(
                String.valueOf(consumibles)
        );

        lblStockBajo.setText(
                String.valueOf(stockBajo)
        );
    }


    // =========================================================
    // STOCK BAJO
    // =========================================================

    private boolean esStockBajo(Material material) {

        if (material == null) {
            return false;
        }

        return material.getStockActual() <=
                material.getStockMinimo();
    }


    // =========================================================
    // CONFIGURAR FILTROS
    // =========================================================

    private void configurarFiltros() {

        cmbCategoria.setOnAction(event ->
                aplicarFiltros()
        );

        cmbTipo.setOnAction(event ->
                aplicarFiltros()
        );

        cmbStock.setOnAction(event ->
                aplicarFiltros()
        );
    }


    // =========================================================
    // CARGAR OPCIONES DE FILTROS
    // =========================================================

    private void cargarOpcionesFiltros() {

        // -----------------------------------------------------
        // CATEGORÍAS
        // -----------------------------------------------------

        ObservableList<String> categorias =
                FXCollections.observableArrayList();

        categorias.add("Todas");

        todosLosMateriales.stream()
                .map(Material::getNomCategoria)
                .filter(c ->
                        c != null &&
                        !c.isBlank()
                )
                .distinct()
                .sorted()
                .forEach(categorias::add);

        cmbCategoria.setItems(categorias);

        cmbCategoria.setValue("Todas");


        // -----------------------------------------------------
        // TIPOS
        // -----------------------------------------------------

        ObservableList<String> tipos =
                FXCollections.observableArrayList(
                        "Todos",
                        "Activo",
                        "Consumible"
                );

        cmbTipo.setItems(tipos);

        cmbTipo.setValue("Todos");


        // -----------------------------------------------------
        // STOCK
        // -----------------------------------------------------

        ObservableList<String> stock =
                FXCollections.observableArrayList(
                        "Todos",
                        "Normal",
                        "Bajo"
                );

        cmbStock.setItems(stock);

        cmbStock.setValue("Todos");
    }


    // =========================================================
    // APLICAR FILTROS
    // =========================================================

    private void aplicarFiltros() {

        String texto =
                txtBuscarMaterial.getText() == null
                        ? ""
                        : txtBuscarMaterial.getText()
                                .trim()
                                .toLowerCase();


        String categoria =
                cmbCategoria.getValue();

        String tipo =
                cmbTipo.getValue();

        String stock =
                cmbStock.getValue();


        ObservableList<Material> filtrados =
                todosLosMateriales.filtered(material -> {

            // -------------------------------------------------
            // BÚSQUEDA POR ID O NOMBRE
            // -------------------------------------------------

            boolean coincideBusqueda = true;

            if (!texto.isEmpty()) {

                // Si el usuario escribe solamente números,
                // se interpreta como una búsqueda exacta por ID.
                if (texto.matches("\\d+")) {

                    int idBuscado =
                            Integer.parseInt(texto);

                    coincideBusqueda =
                            material.getIdMaterial() == idBuscado;

                } else {

                    // Si escribe texto, se busca por nombre.
                    String nombre =
                            material.getNomMaterial() == null
                                    ? ""
                                    : material.getNomMaterial()
                                            .toLowerCase();

                    coincideBusqueda =
                            nombre.contains(texto);
                }
            }


            // -------------------------------------------------
            // CATEGORÍA
            // -------------------------------------------------

            boolean coincideCategoria =
                    categoria == null ||
                    categoria.equals("Todas") ||
                    (
                        material.getNomCategoria() != null &&
                        material.getNomCategoria()
                                .equalsIgnoreCase(categoria)
                    );


            // -------------------------------------------------
            // TIPO
            // -------------------------------------------------

            boolean coincideTipo =
                    tipo == null ||
                    tipo.equals("Todos") ||
                    (
                        material.getTipo() != null &&
                        material.getTipo()
                                .equalsIgnoreCase(tipo)
                    );


            // -------------------------------------------------
            // STOCK
            // -------------------------------------------------

            boolean coincideStock = true;

            if ("Bajo".equals(stock)) {

                coincideStock =
                        esStockBajo(material);

            } else if ("Normal".equals(stock)) {

                coincideStock =
                        !esStockBajo(material);
            }


            // -------------------------------------------------
            // RESULTADO FINAL
            // -------------------------------------------------

            return coincideBusqueda &&
                    coincideCategoria &&
                    coincideTipo &&
                    coincideStock;
        });


        listaMateriales.setAll(filtrados);

        tblMateriales.setItems(listaMateriales);
    }


    // =========================================================
    // BUSCAR
    // =========================================================

    @FXML
    private void buscarMaterial(ActionEvent event) {

        aplicarFiltros();
    }


    // =========================================================
    // LIMPIAR
    // =========================================================

    @FXML
    private void limpiar(ActionEvent event) {

        txtBuscarMaterial.clear();

        cmbCategoria.setValue("Todas");
        cmbTipo.setValue("Todos");
        cmbStock.setValue("Todos");

        aplicarFiltros();
    }


    // =========================================================
    // AGREGAR MATERIAL
    // =========================================================

    @FXML
    private void agregarMaterial(ActionEvent event) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/utng/gtid2/dab/materiales/AgregarMaterial.fxml"
                            )
                    );

            Parent root = loader.load();

            Stage stage = new Stage();

            stage.setTitle("Agregar Material");

            stage.initModality(
                    Modality.APPLICATION_MODAL
            );

            stage.setScene(
                    new Scene(root)
            );

            stage.showAndWait();

            // Actualizar tabla, contadores y filtros
            cargarDatosTabla();

        } catch (IOException e) {

            e.printStackTrace();

            mostrarAlerta(
                    "Error",
                    "No se pudo cargar la vista AgregarMaterial.fxml"
            );
        }
    }


    // =========================================================
    // EDITAR MATERIAL
    // =========================================================

    @FXML
    private void editarMaterial(ActionEvent event) {

        Material seleccionado =
                tblMateriales
                        .getSelectionModel()
                        .getSelectedItem();


        if (seleccionado == null) {

            mostrarAlerta(
                    "Atención",
                    "Por favor selecciona un material de la tabla para editar."
            );

            return;
        }


        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/utng/gtid2/dab/materiales/EditarMaterial.fxml"
                            )
                    );

            Parent root = loader.load();


            EditarMaterialController controller =
                    loader.getController();

            controller.cargarMaterial(
                    seleccionado
            );


            Stage stage = new Stage();

            stage.setTitle("Editar Material");

            stage.initModality(
                    Modality.APPLICATION_MODAL
            );

            stage.setScene(
                    new Scene(root)
            );

            stage.showAndWait();


            // Actualizar después de editar
            cargarDatosTabla();

        } catch (IOException e) {

            e.printStackTrace();

            mostrarAlerta(
                    "Error",
                    "No se pudo cargar la vista EditarMaterial.fxml"
            );
        }
    }


    // =========================================================
    // NAVEGACIÓN
    // =========================================================

    @FXML
    private void inicio(ActionEvent event)
            throws IOException {

        App.setRoot("inicio/Inicio");
    }


    @FXML
    private void materialesRegistrados(ActionEvent event)
            throws IOException {

        cargarDatosTabla();
    }


    @FXML
    private void prestamosActivos(ActionEvent event)
            throws IOException {

        App.setRoot(
                "prestamos/PrestamosActivos"
        );
    }


    @FXML
    private void materialesDanados(ActionEvent event)
            throws IOException {

        App.setRoot(
                "danos/MaterialesDanados"
        );
    }


    @FXML
    private void reportes(ActionEvent event)
            throws IOException {

        App.setRoot(
                "reportes/Reportes"
        );
    }


    @FXML
    private void usuarios(ActionEvent event)
            throws IOException {

        App.setRoot(
                "usuarios/Usuarios"
        );
    }


    @FXML
    private void cuenta(ActionEvent event)
            throws IOException {

        App.setRoot(
                "cuenta/Cuenta"
        );
    }


    // =========================================================
    // ALERTAS
    // =========================================================

    private void mostrarAlerta(
            String titulo,
            String mensaje) {

        Alert alert =
                new Alert(
                        Alert.AlertType.WARNING
                );

        alert.setTitle(titulo);

        alert.setHeaderText(null);

        alert.setContentText(mensaje);

        alert.showAndWait();
    }
}