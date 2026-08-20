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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import utng.gtid2.dab.App;
import utng.gtid2.dab.dao.MaterialDAO;
import utng.gtid2.dab.modelo.Material;
import utng.gtid2.dab.util.RelojSistema;
import utng.gtid2.dab.util.Navegador;

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

        /*
         * IMPORTANTE:
         * NO usamos CONSTRAINED_RESIZE_POLICY.
         *
         * Esa política hacía que al maximizar la ventana
         * todas las columnas crecieran demasiado.
         */

        // Configuración visual inicial
        ajustarAnchoColumnas();

        /*
         * Cuando cambia el tamaño de la ventana,
         * ajustamos las columnas nuevamente.
         */
        tblMateriales.widthProperty().addListener(
                (observable, oldValue, newValue) -> {

                    ajustarAnchoColumnas();
                }
        );

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

        txtBuscarMaterial.setOnAction(
                event -> aplicarFiltros()
        );
    }


    // =========================================================
    // AJUSTAR ANCHO DE COLUMNAS
    // =========================================================

    private void ajustarAnchoColumnas() {

        if (tblMateriales == null) {
            return;
        }

        double ancho =
                tblMateriales.getWidth();

        if (ancho <= 0) {
            return;
        }


        /*
         * Ancho aproximado de la tabla en una ventana
         * normal.
         *
         * Estos valores mantienen prácticamente
         * la apariencia que ya tienes.
         */

        double id = 55;

        double nombre = 190;

        double categoria = 145;

        double tipo = 100;

        double cantidad = 85;

        double minimo = 80;

        double estado = 125;

        double ubicacion = 180;


        double anchoBase =
                id
                + nombre
                + categoria
                + tipo
                + cantidad
                + minimo
                + estado
                + ubicacion;


        /*
         * Espacio disponible adicional.
         */

        double espacioExtra =
                ancho - anchoBase;


        /*
         * Si hay espacio adicional porque la ventana
         * está maximizada, no lo repartimos entre
         * todas las columnas.
         *
         * Lo damos principalmente a Nombre,
         * Categoría y Ubicación.
         */

        if (espacioExtra > 0) {

            double crecimientoNombre =
                    espacioExtra * 0.30;

            double crecimientoCategoria =
                    espacioExtra * 0.20;

            double crecimientoUbicacion =
                    espacioExtra * 0.50;


            nombre += crecimientoNombre;

            categoria += crecimientoCategoria;

            ubicacion += crecimientoUbicacion;
        }


        /*
         * Límites para evitar que una columna se
         * vuelva exageradamente grande.
         */

        nombre =
                Math.min(nombre, 300);

        categoria =
                Math.min(categoria, 240);

        ubicacion =
                Math.min(ubicacion, 320);


        /*
         * Aplicar anchos.
         */

        colId.setPrefWidth(id);

        colNombre.setPrefWidth(nombre);

        colCategoria.setPrefWidth(categoria);

        colTipo.setPrefWidth(tipo);

        colCantidad.setPrefWidth(cantidad);

        colStockMinimo.setPrefWidth(minimo);

        colEstado.setPrefWidth(estado);

        colUbicacion.setPrefWidth(ubicacion);


        /*
         * Evitar que el usuario arrastre manualmente
         * las columnas.
         */

        colId.setResizable(false);
        colNombre.setResizable(false);
        colCategoria.setResizable(false);
        colTipo.setResizable(false);
        colCantidad.setResizable(false);
        colStockMinimo.setResizable(false);
        colEstado.setResizable(false);
        colUbicacion.setResizable(false);
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


        // =====================================================
        // ALINEACIÓN
        // =====================================================

        colId.setStyle("-fx-alignment: CENTER;");

        colNombre.setStyle("-fx-alignment: CENTER-LEFT;");

        colCategoria.setStyle("-fx-alignment: CENTER-LEFT;");

        colTipo.setStyle("-fx-alignment: CENTER;");

        colCantidad.setStyle("-fx-alignment: CENTER;");

        colStockMinimo.setStyle("-fx-alignment: CENTER;");

        colEstado.setStyle("-fx-alignment: CENTER;");

        colUbicacion.setStyle("-fx-alignment: CENTER-LEFT;");


        // =====================================================
        // COLORES DE TIPO
        // =====================================================

        colTipo.setCellFactory(column ->
                new TableCell<Material, String>() {

                    @Override
                    protected void updateItem(
                            String tipo,
                            boolean empty) {

                        super.updateItem(tipo, empty);

                        if (empty || tipo == null) {

                            setText(null);

                            setTextFill(Color.BLACK);

                            setStyle(
                                    "-fx-alignment: CENTER;"
                            );

                            return;
                        }

                        setText(tipo);

                        setStyle(
                                "-fx-alignment: CENTER;" +
                                "-fx-font-weight: bold;"
                        );


                        if (tipo.equalsIgnoreCase("Activo")) {

                            setTextFill(
                                    Color.web("#16A34A")
                            );

                        } else if (
                                tipo.equalsIgnoreCase("Consumible")) {

                            setTextFill(
                                    Color.web("#D97706")
                            );

                        } else {

                            setTextFill(Color.BLACK);
                        }
                    }
                }
        );


        // =====================================================
        // COLORES DE ESTADO
        // =====================================================

        colEstado.setCellFactory(column ->
                new TableCell<Material, String>() {

                    @Override
                    protected void updateItem(
                            String estado,
                            boolean empty) {

                        super.updateItem(estado, empty);

                        if (empty) {

                            setText(null);

                            setTextFill(Color.BLACK);

                            setStyle(
                                    "-fx-alignment: CENTER;"
                            );

                            return;
                        }


                        Material material =
                                getTableView()
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
                                "-fx-alignment: CENTER;" +
                                "-fx-font-weight: bold;"
                        );


                        if (cantidad <= minimo) {

                            setText("Stock Bajo");

                            setTextFill(
                                    Color.web("#DC2626")
                            );

                        } else {

                            setText("Disponible");

                            setTextFill(
                                    Color.web("#2563EB")
                            );
                        }
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
    // ACTUALIZAR CONTADORES
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

        return material.getStockActual()
                <= material.getStockMinimo();
    }


    // =========================================================
    // CONFIGURAR FILTROS
    // =========================================================

    private void configurarFiltros() {

        cmbCategoria.setOnAction(
                event -> aplicarFiltros()
        );

        cmbTipo.setOnAction(
                event -> aplicarFiltros()
        );

        cmbStock.setOnAction(
                event -> aplicarFiltros()
        );
    }


    // =========================================================
    // CARGAR OPCIONES DE FILTROS
    // =========================================================

    private void cargarOpcionesFiltros() {

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


        ObservableList<String> tipos =
                FXCollections.observableArrayList(
                        "Todos",
                        "Activo",
                        "Consumible"
                );

        cmbTipo.setItems(tipos);

        cmbTipo.setValue("Todos");


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

                    boolean coincideBusqueda = true;


                    if (!texto.isEmpty()) {

                        if (texto.matches("\\d+")) {

                            int idBuscado =
                                    Integer.parseInt(texto);

                            coincideBusqueda =
                                    material.getIdMaterial()
                                            == idBuscado;

                        } else {

                            String nombre =
                                    material.getNomMaterial() == null
                                            ? ""
                                            : material.getNomMaterial()
                                                    .toLowerCase();

                            coincideBusqueda =
                                    nombre.contains(texto);
                        }
                    }


                    boolean coincideCategoria =
                            categoria == null ||
                            categoria.equals("Todas") ||
                            (
                                material.getNomCategoria() != null &&
                                material.getNomCategoria()
                                        .equalsIgnoreCase(categoria)
                            );


                    boolean coincideTipo =
                            tipo == null ||
                            tipo.equals("Todos") ||
                            (
                                material.getTipo() != null &&
                                material.getTipo()
                                        .equalsIgnoreCase(tipo)
                            );


                    boolean coincideStock = true;


                    if ("Bajo".equals(stock)) {

                        coincideStock =
                                esStockBajo(material);

                    } else if ("Normal".equals(stock)) {

                        coincideStock =
                                !esStockBajo(material);
                    }


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
        private void reportes(ActionEvent event) throws IOException {
                if (Navegador.verificarAdministrador()) {
                        App.setRoot("reportes/Reportes");
                }
        }

        @FXML
        private void usuarios(ActionEvent event) throws IOException {
                if (Navegador.verificarAdministrador()) {
                        App.setRoot("usuarios/Usuarios");
                }
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