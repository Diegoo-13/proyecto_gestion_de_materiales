package utng.gtid2.dab.controllers.inicio;
 
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
 
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.transform.Scale;
 
import utng.gtid2.dab.App;
import utng.gtid2.dab.dao.MaterialDAO;
import utng.gtid2.dab.dao.MaterialDanadoDAO;
import utng.gtid2.dab.dao.PrestamosDAO;
import utng.gtid2.dab.modelo.Prestamo;
import utng.gtid2.dab.modelo.Usuario;
import utng.gtid2.dab.util.Navegador;
import utng.gtid2.dab.util.RelojSistema;
import utng.gtid2.dab.util.Sesion;
 
/**
 * Controlador de la pantalla principal del sistema.
 *
 * <p>
 * Esta clase se encarga de controlar la pantalla de Inicio,
 * mostrando información general del sistema como:
 * </p>
 *
 * <ul>
 *     <li>Usuario que inició sesión.</li>
 *     <li>Fecha y hora actual.</li>
 *     <li>Total de materiales registrados.</li>
 *     <li>Total de préstamos activos.</li>
 *     <li>Total de materiales dañados pendientes.</li>
 *     <li>Total de materiales con stock bajo.</li>
 *     <li>Préstamos recientes.</li>
 * </ul>
 *
 * <p>
 * También controla los accesos rápidos de las tarjetas
 * del dashboard, la navegación hacia los diferentes módulos,
 * y el reescalado responsivo del contenido cuando la ventana
 * se maximiza, restaura o redimensiona.
 * </p>
 *
 * @author Juan Diego
 * @version 1.0
 */
public class InicioController implements Initializable {
 
    // ============================================================
    // COMPONENTES DE LA INTERFAZ
    // ============================================================
 
    /**
     * Panel principal que contiene el contenido de la pantalla.
     */
    @FXML
    private AnchorPane apContenido;
 
    /**
     * Escala aplicada al panel de contenido para que se
     * reajuste automáticamente al tamaño real de la ventana.
     */
    @FXML
    private Scale scaleContenido;
 
    /**
     * Tarjeta de materiales registrados.
     */
    @FXML
    private AnchorPane apMaterialesRegistrados;
 
    /**
     * Tarjeta de préstamos activos.
     */
    @FXML
    private AnchorPane apPrestamosActivos;
 
    /**
     * Tarjeta de materiales dañados.
     */
    @FXML
    private AnchorPane apMaterialesDanados;
 
    /**
     * Tarjeta de materiales con stock bajo.
     */
    @FXML
    private AnchorPane apMaterialesStockBajo;
 
    /**
     * Botón del módulo Inicio.
     */
    @FXML
    private Button btnInicio;
 
    /**
     * Botón del módulo Materiales Registrados.
     */
    @FXML
    private Button btnMaterialesRegistrados;
 
    /**
     * Botón del módulo Préstamos Activos.
     */
    @FXML
    private Button btnPrestamosActivos;
 
    /**
     * Botón del módulo Materiales Dañados.
     */
    @FXML
    private Button btnMaterialesDanados;
 
    /**
     * Botón del módulo Reportes.
     */
    @FXML
    private Button btnReportes;
 
    /**
     * Botón del módulo Usuarios.
     */
    @FXML
    private Button btnUsuarios;
 
    /**
     * Botón del módulo Cuenta.
     */
    @FXML
    private Button btnCuenta;
 
    /**
     * Botón para registrar un nuevo préstamo.
     */
    @FXML
    private Button btnNuevoPrestamoo;
 
    /**
     * Label utilizado para mostrar el saludo.
     */
    @FXML
    private Label lblSaludo;
 
    /**
     * Label utilizado para mostrar el nombre
     * del usuario que inició sesión.
     */
    @FXML
    private Label lblNombreUsuario;
 
    /**
     * Label que muestra la hora actual.
     */
    @FXML
    private Label lblHora;
 
    /**
     * Label que muestra la fecha actual.
     */
    @FXML
    private Label lblFecha;
 
    /**
     * Label que muestra el total de materiales registrados.
     */
    @FXML
    private Label lblTotalMaterialesRegistrados;
 
    /**
     * Label que muestra el total de préstamos activos.
     */
    @FXML
    private Label lblTotalPrestamosActivos;
 
    /**
     * Label que muestra el total de materiales dañados pendientes.
     */
    @FXML
    private Label lblTotalMaterialesDanados;
 
    /**
     * Label que muestra el total de materiales con stock bajo.
     */
    @FXML
    private Label lblTotalMaterialesStockBajo;
 
    // ============================================================
    // TABLA DE PRÉSTAMOS RECIENTES
    // ============================================================
 
    /**
     * Tabla donde se muestran los préstamos más recientes.
     */
    @FXML
    private TableView<Prestamo> tblPrestamosRecientes;
 
    /**
     * Columna que muestra el folio del préstamo.
     */
    @FXML
    private TableColumn<Prestamo, Integer> colFolio;
 
    /**
     * Columna que muestra el responsable del préstamo.
     */
    @FXML
    private TableColumn<Prestamo, String> colResponsable;
 
    /**
     * Columna que muestra el nombre del material.
     */
    @FXML
    private TableColumn<Prestamo, String> colMaterial;
 
    /**
     * Columna que muestra la cantidad prestada.
     */
    @FXML
    private TableColumn<Prestamo, Integer> colCantidad;
 
    /**
     * Columna que muestra la fecha del préstamo.
     */
    @FXML
    private TableColumn<Prestamo, String> colFechaPrestamo;
 
    /**
     * Columna que muestra la fecha esperada de devolución.
     */
    @FXML
    private TableColumn<Prestamo, String> colFechaDevolucion;
 
    /**
     * Columna que muestra el estado calculado del préstamo.
     */
    @FXML
    private TableColumn<Prestamo, String> colEstado;
 
    // ============================================================
    // DAO
    // ============================================================
 
    /**
     * DAO utilizado para consultar información de materiales.
     */
    private final MaterialDAO materialDAO = new MaterialDAO();
 
    /**
     * DAO utilizado para consultar información de préstamos.
     */
    private final PrestamosDAO prestamosDAO = new PrestamosDAO();
 
    /**
     * DAO utilizado para consultar información de materiales dañados.
     */
    private final MaterialDanadoDAO materialDanadoDAO =
            new MaterialDanadoDAO();
 
    // ============================================================
    // ESCALADO RESPONSIVO
    // ============================================================
 
    /**
     * Ancho de diseño original de la pantalla (tal como
     * fue construida en Scene Builder).
     */
    private static final double DISENO_ANCHO = 1366.0;
 
    /**
     * Alto de diseño original de la pantalla (tal como
     * fue construida en Scene Builder).
     */
    private static final double DISENO_ALTO = 768.0;
 
    // ============================================================
    // LISTA DE PRÉSTAMOS
    // ============================================================
 
    /**
     * Lista observable utilizada para alimentar la tabla
     * de préstamos recientes.
     */
    private final ObservableList<Prestamo> prestamosRecientes =
            FXCollections.observableArrayList();
 
    // ============================================================
    // INICIALIZACIÓN
    // ============================================================
 
    /**
     * Inicializa los componentes de la pantalla principal.
     *
     * <p>
     * En este método se configura el reloj del sistema,
     * se obtiene el usuario actual, se cargan los contadores,
     * se configura la tabla de préstamos recientes,
     * se establecen los eventos de navegación y se activa
     * el reescalado responsivo del contenido.
     * </p>
     *
     * @param url URL utilizada para localizar el recurso FXML.
     * @param rb recursos utilizados para la internacionalización.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
 
        // ========================================================
        // BOTÓN INICIO ACTIVO
        // ========================================================
 
        btnInicio.getStyleClass().add("active");
 
        // ========================================================
        // FECHA Y HORA
        // ========================================================
 
        RelojSistema.iniciar(lblHora, lblFecha);
 
        // ========================================================
        // USUARIO ACTUAL
        // ========================================================
 
        Usuario usuarioActual = Sesion.getUsuarioActual();
 
        if (usuarioActual != null) {
 
            lblNombreUsuario.setText(
                    usuarioActual.getNombre()
            );
 
        } else {
 
            lblNombreUsuario.setText("Usuario");
        }
 
        // ========================================================
        // CONTADORES
        // ========================================================
 
        cargarContadores();
 
        // ========================================================
        // TABLA DE PRÉSTAMOS RECIENTES
        // ========================================================
 
        configurarTablaPrestamos();
        cargarPrestamosRecientes();
 
        // ========================================================
        // ACCESOS RÁPIDOS
        // ========================================================
 
        apMaterialesRegistrados.setOnMouseClicked(
                e -> abrirMateriales()
        );
 
        apPrestamosActivos.setOnMouseClicked(
                e -> abrirPrestamos()
        );
 
        apMaterialesDanados.setOnMouseClicked(
                e -> abrirMaterialesDanados()
        );
 
        apMaterialesStockBajo.setOnMouseClicked(
                e -> abrirStockBajo()
        );
 
        tblPrestamosRecientes.setColumnResizePolicy(
        TableView.UNCONSTRAINED_RESIZE_POLICY);
 
        // ========================================================
        // REESCALADO RESPONSIVO DEL CONTENIDO
        // ========================================================
        //
        // Evita que el layout se rompa (sidebar montado sobre
        // el contenido) cuando la ventana se minimiza, restaura
        // o redimensiona. Recalcula el factor de escala cada vez
        // que cambia el ancho o el alto real de la ventana, y lo
        // vuelve a aplicar también cuando la ventana recupera el
        // foco/tamaño tras minimizarse.
        // ========================================================
 
        apContenido.sceneProperty().addListener((obs, oldScene, newScene) -> {
 
            if (newScene != null) {
 
                newScene.widthProperty().addListener(
                        (o, ov, nv) -> ajustarEscala(newScene)
                );
 
                newScene.heightProperty().addListener(
                        (o, ov, nv) -> ajustarEscala(newScene)
                );
 
                // Aplica el ajuste inicial en cuanto la escena
                // ya está disponible.
                ajustarEscala(newScene);
            }
        });
    }
 
    // ============================================================
    // NAVEGACIÓN
    // ============================================================
 
    /**
     * Mantiene al usuario en la pantalla de Inicio.
     *
     * @param event evento generado por el botón Inicio.
     */
    @FXML
    private void inicio(ActionEvent event) {
 
        // Ya estamos en Inicio.
    }
 
    /**
     * Abre la ventana modal para registrar un nuevo préstamo.
     *
     * @param event evento generado por el botón Nuevo Préstamo.
     * @throws IOException si no es posible cargar la ventana.
     */
    @FXML
    private void nuevoPrestamo(ActionEvent event) throws IOException {
 
        Navegador.abrirModal(
                "prestamos/NuevoPrestamo",
                "Nuevo préstamo"
        );
    }
 
    /**
     * Abre el módulo de materiales registrados.
     *
     * @param event evento generado por el botón correspondiente.
     * @throws IOException si no es posible cargar la pantalla.
     */
    @FXML
    private void materialesRegistrados(ActionEvent event)
            throws IOException {
 
        App.setRoot(
                "materiales/MaterialesRegistrados"
        );
    }
 
    /**
     * Abre el módulo de préstamos activos.
     *
     * @param event evento generado por el botón correspondiente.
     * @throws IOException si no es posible cargar la pantalla.
     */
    @FXML
    private void prestamosActivos(ActionEvent event)
            throws IOException {
 
        App.setRoot(
                "prestamos/PrestamosActivos"
        );
    }
 
    /**
     * Abre el módulo de materiales dañados.
     *
     * @param event evento generado por el botón correspondiente.
     * @throws IOException si no es posible cargar la pantalla.
     */
    @FXML
    private void materialesDanados(ActionEvent event)
            throws IOException {
 
        App.setRoot(
                "danos/MaterialesDanados"
        );
    }
 
    /**
     * Abre el módulo de reportes.
     *
     * @param event evento generado por el botón correspondiente.
     * @throws IOException si no es posible cargar la pantalla.
     */
        @FXML
        private void reportes(ActionEvent event) throws IOException {
                if (Navegador.verificarAdministrador()) {
                        App.setRoot("reportes/Reportes");
                }
        }
 
    /**
     * Abre el módulo de usuarios.
     *
     * @param event evento generado por el botón correspondiente.
     * @throws IOException si no es posible cargar la pantalla.
     */
        @FXML
        private void usuarios(ActionEvent event) throws IOException {
                if (Navegador.verificarAdministrador()) {
                        App.setRoot("usuarios/Usuarios");
                }
        }
 
    /**
     * Abre el módulo de cuenta del usuario.
     *
     * @param event evento generado por el botón correspondiente.
     * @throws IOException si no es posible cargar la pantalla.
     */
    @FXML
    private void cuenta(ActionEvent event)
            throws IOException {
 
        App.setRoot(
                "cuenta/Cuenta"
        );
    }
 
    // ============================================================
    // ACCESOS RÁPIDOS DEL DASHBOARD
    // ============================================================
 
    /**
     * Abre el módulo de materiales registrados
     * desde la tarjeta correspondiente del dashboard.
     */
    private void abrirMateriales() {
 
        try {
 
            App.setRoot(
                    "materiales/MaterialesRegistrados"
            );
 
        } catch (IOException e) {
 
            e.printStackTrace();
        }
    }
 
    /**
     * Abre el módulo de préstamos activos
     * desde la tarjeta correspondiente del dashboard.
     */
    private void abrirPrestamos() {
 
        try {
 
            App.setRoot(
                    "prestamos/PrestamosActivos"
            );
 
        } catch (IOException e) {
 
            e.printStackTrace();
        }
    }
 
    /**
     * Abre el módulo de materiales dañados
     * desde la tarjeta correspondiente del dashboard.
     */
    private void abrirMaterialesDanados() {
 
        try {
 
            App.setRoot(
                    "danos/MaterialesDanados"
            );
 
        } catch (IOException e) {
 
            e.printStackTrace();
        }
    }
 
    /**
     * Abre el módulo de materiales registrados
     * solicitando que se muestre únicamente el stock bajo.
     */
    private void abrirStockBajo() {
 
        try {
 
            // Solicitar el filtro de stock bajo.
            App.solicitarFiltroStockBajo();
 
            App.setRoot(
                    "materiales/MaterialesRegistrados"
            );
 
        } catch (IOException e) {
 
            e.printStackTrace();
        }
    }
 
    // ============================================================
    // CONTADORES
    // ============================================================
 
    /**
     * Carga los valores de los contadores mostrados
     * en la pantalla principal.
     *
     * <p>
     * Los datos se obtienen directamente desde la base
     * de datos mediante los DAO correspondientes.
     * </p>
     *
     * <p>
     * El contador de materiales dañados considera únicamente
     * los materiales que todavía se encuentran en proceso
     * de atención.
     * </p>
     */
    private void cargarContadores() {
 
        int totalMateriales =
                materialDAO.contarMaterialesRegistrados();
 
        int totalPrestamosActivos =
                prestamosDAO.contarPrestamosActivos();
 
        int totalMaterialesDanados =
                materialDanadoDAO.contarMaterialesDanadosPendientes();
 
        int totalStockBajo =
                materialDAO.contarMaterialesStockBajo();
 
        // Materiales registrados
        lblTotalMaterialesRegistrados.setText(
                String.valueOf(totalMateriales)
        );
 
        // Préstamos activos
        lblTotalPrestamosActivos.setText(
                String.valueOf(totalPrestamosActivos)
        );
 
        // Materiales dañados pendientes
        lblTotalMaterialesDanados.setText(
                String.valueOf(totalMaterialesDanados)
        );
 
        // Materiales con stock bajo
        lblTotalMaterialesStockBajo.setText(
                String.valueOf(totalStockBajo)
        );
    }
 
    // ============================================================
    // TABLA DE PRÉSTAMOS RECIENTES
    // ============================================================
 
    /**
     * Configura las columnas de la tabla de préstamos recientes.
     *
     * <p>
     * Se establecen anchos fijos para evitar que las columnas
     * cambien de tamaño durante la ejecución del sistema.
     * También se alinean al centro los valores numéricos.
     * </p>
     */
    private void configurarTablaPrestamos() {
 
        // ============================================================
        // CONFIGURACIÓN DE DATOS
        // ============================================================
 
        colFolio.setCellValueFactory(
                new PropertyValueFactory<>("idPrestamo")
        );
 
        colResponsable.setCellValueFactory(
                new PropertyValueFactory<>("responsable")
        );
 
        colMaterial.setCellValueFactory(
                new PropertyValueFactory<>("nombreMaterial")
        );
 
        colCantidad.setCellValueFactory(
                new PropertyValueFactory<>("cantidad")
        );
 
        colFechaPrestamo.setCellValueFactory(
                new PropertyValueFactory<>("fechaPrestamo")
        );
 
        colFechaDevolucion.setCellValueFactory(
                new PropertyValueFactory<>("fechaDevolucion")
        );
 
        colEstado.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getEstadoCalculado()
                )
        );
 
        // ============================================================
        // IMPEDIR QUE EL USUARIO CAMBIE EL TAMAÑO
        // ============================================================
 
        colFolio.setResizable(false);
        colResponsable.setResizable(false);
        colMaterial.setResizable(false);
        colCantidad.setResizable(false);
        colFechaPrestamo.setResizable(false);
        colFechaDevolucion.setResizable(false);
        colEstado.setResizable(false);
 
        // ============================================================
        // ALINEACIÓN
        // ============================================================
 
        // Folio centrado
        colFolio.setStyle("-fx-alignment: CENTER;");
 
        // Cantidad centrada
        colCantidad.setStyle("-fx-alignment: CENTER;");
 
        // Fechas centradas
        colFechaPrestamo.setStyle("-fx-alignment: CENTER;");
        colFechaDevolucion.setStyle("-fx-alignment: CENTER;");
 
        // Estado centrado
        colEstado.setStyle("-fx-alignment: CENTER;");
 
 
    }
 
    /**
     * Carga los cinco préstamos más recientes
     * desde la base de datos.
     *
     * <p>
     * Los préstamos son obtenidos mediante el método
     * {@code listarRecientes()} del {@link PrestamosDAO}.
     * </p>
     */
    private void cargarPrestamosRecientes() {
 
        List<Prestamo> lista =
                prestamosDAO.listarRecientes();
 
        prestamosRecientes.setAll(lista);
 
        tblPrestamosRecientes.setItems(
                prestamosRecientes
        );
    }
 
    // ============================================================
    // ESCALADO RESPONSIVO
    // ============================================================
 
    /**
     * Recalcula el factor de escala del contenido según
     * el tamaño real de la ventana en ese momento.
     *
     * <p>
     * Se toma el menor de los dos factores (ancho y alto)
     * para que el contenido nunca se desborde ni se monte
     * sobre la barra lateral, sin importar si la ventana
     * está maximizada, restaurada o fue redimensionada
     * manualmente.
     * </p>
     *
     * @param scene escena actual de la pantalla de Inicio.
     */
    private void ajustarEscala(Scene scene) {
 
        double factor = Math.min(
                scene.getWidth() / DISENO_ANCHO,
                scene.getHeight() / DISENO_ALTO
        );
 
        scaleContenido.setX(factor);
        scaleContenido.setY(factor);
    }
}