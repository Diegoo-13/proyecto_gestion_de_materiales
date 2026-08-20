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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import utng.gtid2.dab.App;
import utng.gtid2.dab.dao.MaterialDanadoDAO;
import utng.gtid2.dab.modelo.MaterialDanado;
import utng.gtid2.dab.util.Navegador;
import utng.gtid2.dab.util.RelojSistema;

public class MaterialesDanadosController implements Initializable {

    // ============================================================
    // DAO
    // ============================================================

    private final MaterialDanadoDAO materialDanadoDAO =
            new MaterialDanadoDAO();

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
    private TextField txtBuscar;

    @FXML
    private ComboBox<String> cbEstado;

    @FXML
    private ComboBox<String> cbCategoria;

    @FXML
    private Button btnBuscar;

    @FXML
    private Button btnLimpiar;

    // ============================================================
    // TARJETAS
    // ============================================================

    @FXML
    private Label lblHora;

    @FXML
    private Label lblFecha;

    @FXML
    private Label lblTotalReportados;

    @FXML
    private Label lblEnReparacion;

    @FXML
    private Label lblBajaDefinitiva;

    // ============================================================
    // TABLA
    // ============================================================

    @FXML
    private TableView<MaterialDanado> tblMateriales;

    @FXML
    private TableColumn<MaterialDanado, Integer> colId;

    @FXML
    private TableColumn<MaterialDanado, String> colMaterial;

    @FXML
    private TableColumn<MaterialDanado, String> colCategoria;

    @FXML
    private TableColumn<MaterialDanado, LocalDate> colFecha;

    @FXML
    private TableColumn<MaterialDanado, String> colReporto;

    @FXML
    private TableColumn<MaterialDanado, String> colEstado;

    @FXML
    private TableColumn<MaterialDanado, String> colObservaciones;

    // ============================================================
    // BOTONES
    // ============================================================

    @FXML
    private Button btnReportarDano;

    @FXML
    private Button btnEnviarReparacion;

    @FXML
    private Button btnRestaurar;

    @FXML
    private Button btnBaja;

    // ============================================================
    // INICIALIZAR
    // ============================================================

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        btnMaterialesDanados.getStyleClass().add("active");

        // ========================================================
        // FECHA Y HORA
        // ========================================================

        RelojSistema.iniciar(lblHora, lblFecha);

        // ========================================================
        // ESTADOS
        // ========================================================

        cbEstado.getItems().clear();

        cbEstado.getItems().addAll(
                "Todos",
                "Reportado",
                "En reparación",
                "Reparado",
                "Dado de baja"
        );

        cbEstado.getSelectionModel().selectFirst();

        // ========================================================
        // CATEGORÍAS
        // ========================================================

        cbCategoria.getItems().clear();

        cbCategoria.getItems().addAll(
                "Todas",
                "Computadoras",
                "Herramientas",
                "Periféricos",
                "Redes",
                "Electrónica",
                "Cableado",
                "Audio y Video"
        );

        cbCategoria.getSelectionModel().selectFirst();

        // ========================================================
        // BUSCAR CON ENTER
        // ========================================================

        txtBuscar.setOnKeyPressed(event -> {

            if (event.getCode() == KeyCode.ENTER) {
                buscar(null);
            }
        });

        // ========================================================
        // CAMBIO DE ESTADO
        // ========================================================

        cbEstado.setOnAction(event -> buscar(null));

        // ========================================================
        // CAMBIO DE CATEGORÍA
        // ========================================================

        cbCategoria.setOnAction(event -> buscar(null));

        // ========================================================
        // CONFIGURACIÓN DE TABLA
        // ========================================================

        tblMateriales.setColumnResizePolicy(
                TableView.UNCONSTRAINED_RESIZE_POLICY
        );

        colId.setResizable(false);
        colMaterial.setResizable(false);
        colCategoria.setResizable(false);
        colFecha.setResizable(false);
        colReporto.setResizable(false);
        colEstado.setResizable(false);
        colObservaciones.setResizable(false);

        // ========================================================
        // COLUMNAS
        // ========================================================

        colId.setCellValueFactory(
                new PropertyValueFactory<>("idMaterialDanado")
        );

        colMaterial.setCellValueFactory(
                new PropertyValueFactory<>("nombreMaterial")
        );

        colCategoria.setCellValueFactory(
                new PropertyValueFactory<>("categoria")
        );

        colFecha.setCellValueFactory(
                new PropertyValueFactory<>("fechaReporte")
        );

        colReporto.setCellValueFactory(
                new PropertyValueFactory<>("nombreUsuario")
        );

        colEstado.setCellValueFactory(
                new PropertyValueFactory<>("estado")
        );

        colObservaciones.setCellValueFactory(
                new PropertyValueFactory<>("descripcionDanio")
        );

        // ========================================================
        // COLOR DEL ESTADO
        // ========================================================

        colEstado.setCellFactory(column ->
                new TableCell<MaterialDanado, String>() {

            @Override
            protected void updateItem(
                    String estado,
                    boolean empty) {

                super.updateItem(estado, empty);

                if (empty || estado == null) {

                    setText(null);
                    setStyle("");
                    setTextFill(Color.BLACK);

                    return;
                }

                setText(estado);
                setStyle("-fx-font-weight: bold;");

                switch (estado.toLowerCase()) {

                    case "dado de baja":
                        setTextFill(Color.RED);
                        break;

                    case "reportado":
                        setTextFill(Color.BLUE);
                        break;

                    case "en reparación":
                    case "en reparacion":
                        setTextFill(Color.ORANGE);
                        break;

                    case "reparado":
                        setTextFill(Color.GREEN);
                        break;

                    default:
                        setTextFill(Color.BLACK);
                        break;
                }
            }
        });

        // ========================================================
        // CARGAR DATOS
        // ========================================================

        cargarTabla();
    }

    // ============================================================
    // CARGAR TABLA
    // ============================================================

    private void cargarTabla() {

        try {

            List<MaterialDanado> lista =
                    materialDanadoDAO.listar();

            ObservableList<MaterialDanado> datos =
                    FXCollections.observableArrayList(lista);

            tblMateriales.setItems(datos);

            actualizarTarjetas(lista);

        } catch (Exception e) {

            e.printStackTrace();

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudieron cargar los materiales dañados."
            );
        }
    }

    // ============================================================
    // ACTUALIZAR TARJETAS
    // ============================================================

    private void actualizarTarjetas(
            List<MaterialDanado> lista) {

        long reportados = lista.stream()
                .filter(m ->
                        m.getEstado() != null
                        &&
                        "Reportado".equalsIgnoreCase(
                                m.getEstado()
                        ))
                .count();

        long enReparacion = lista.stream()
                .filter(m ->
                        m.getEstado() != null
                        &&
                        "En reparación".equalsIgnoreCase(
                                m.getEstado()
                        ))
                .count();

        long bajas = lista.stream()
                .filter(m ->
                        m.getEstado() != null
                        &&
                        "Dado de baja".equalsIgnoreCase(
                                m.getEstado()
                        ))
                .count();

        lblTotalReportados.setText(
                String.valueOf(reportados)
        );

        lblEnReparacion.setText(
                String.valueOf(enReparacion)
        );

        lblBajaDefinitiva.setText(
                String.valueOf(bajas)
        );
    }

    // ============================================================
    // BUSCAR / FILTRAR
    // ============================================================

  @FXML
private void buscar(ActionEvent event) {

    String texto = txtBuscar.getText()
            .trim()
            .toLowerCase();

    String estadoSeleccionado = cbEstado.getValue();
    String categoriaSeleccionada = cbCategoria.getValue();

    List<MaterialDanado> todos =
            materialDanadoDAO.listar();

    ObservableList<MaterialDanado> filtrados =
            FXCollections.observableArrayList();

    for (MaterialDanado md : todos) {

        boolean coincideBusqueda = false;

        // ====================================================
        // SIN TEXTO: MOSTRAR TODOS
        // ====================================================

        if (texto.isEmpty()) {

            coincideBusqueda = true;

        }

        // ====================================================
        // SI ES UN NÚMERO: BUSCAR ÚNICAMENTE POR ID EXACTO
        // ====================================================

        else if (texto.matches("\\d+")) {

            try {

                int idBuscado = Integer.parseInt(texto);

                coincideBusqueda =
                        md.getIdMaterialDanado() == idBuscado;

            } catch (NumberFormatException e) {

                coincideBusqueda = false;
            }
        }

        // ====================================================
        // SI NO ES NÚMERO: BUSCAR POR NOMBRE O DESCRIPCIÓN
        // ====================================================

        else {

            boolean coincideNombre =
                    md.getNombreMaterial() != null
                    &&
                    md.getNombreMaterial()
                            .toLowerCase()
                            .contains(texto);

            boolean coincideDescripcion =
                    md.getDescripcionDanio() != null
                    &&
                    md.getDescripcionDanio()
                            .toLowerCase()
                            .contains(texto);

            coincideBusqueda =
                    coincideNombre
                    || coincideDescripcion;
        }

        // ====================================================
        // FILTRO DE ESTADO
        // ====================================================

        boolean coincideEstado =
                estadoSeleccionado == null
                ||
                estadoSeleccionado.equals("Todos")
                ||
                (
                    md.getEstado() != null
                    &&
                    estadoSeleccionado.equalsIgnoreCase(
                            md.getEstado()
                    )
                );

        // ====================================================
        // FILTRO DE CATEGORÍA
        // ====================================================

        boolean coincideCategoria =
                categoriaSeleccionada == null
                ||
                categoriaSeleccionada.equals("Todas")
                ||
                (
                    md.getCategoria() != null
                    &&
                    categoriaSeleccionada.equalsIgnoreCase(
                            md.getCategoria()
                    )
                );

        // ====================================================
        // AGREGAR RESULTADO
        // ====================================================

        if (coincideBusqueda
                && coincideEstado
                && coincideCategoria) {

            filtrados.add(md);
        }
    }

    tblMateriales.setItems(filtrados);
}
    // ============================================================
    // LIMPIAR
    // ============================================================

    @FXML
    private void limpiar(ActionEvent event) {

        txtBuscar.clear();

        cbEstado.getSelectionModel()
                .selectFirst();

        cbCategoria.getSelectionModel()
                .selectFirst();

        cargarTabla();
    }

    // ============================================================
    // REPORTAR DAÑO
    // ============================================================

    @FXML
    private void reportarDano(ActionEvent event)
            throws IOException {

        Navegador.abrirModal(
                "danos/ReportarDano",
                "Reportar daño"
        );

        cargarTabla();
    }

    // ============================================================
    // ENVIAR A REPARACIÓN
    // ============================================================

    @FXML
    private void enviarReparacion(ActionEvent event) {

        MaterialDanado seleccionado =
                tblMateriales.getSelectionModel()
                        .getSelectedItem();

        // ========================================================
        // VERIFICAR SELECCIÓN
        // ========================================================

        if (seleccionado == null) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Seleccionar material",
                    "Seleccione un material de la tabla antes de enviarlo a reparación."
            );

            return;
        }

        String estadoActual =
                seleccionado.getEstado();

        // ========================================================
        // VERIFICAR ESTADO
        // ========================================================

        if (!"Reportado".equalsIgnoreCase(estadoActual)) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "No se puede enviar",
                    "Solo los materiales con estado 'Reportado' pueden enviarse a reparación."
            );

            return;
        }

        // ========================================================
        // ACTUALIZAR ESTADO
        // ========================================================

        boolean actualizado =
                materialDanadoDAO.actualizarEstado(
                        seleccionado.getIdMaterialDanado(),
                        "En reparación"
                );

        if (actualizado) {

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Material enviado",
                    "El material fue enviado a reparación correctamente."
            );

            cargarTabla();

        } else {

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudo enviar el material a reparación."
            );
        }
    }

    // ============================================================
    // RESTAURAR
    // ============================================================

    @FXML
    private void restaurar(ActionEvent event) {

        MaterialDanado seleccionado =
                tblMateriales.getSelectionModel()
                        .getSelectedItem();

        // ========================================================
        // VERIFICAR SELECCIÓN
        // ========================================================

        if (seleccionado == null) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Seleccionar material",
                    "Seleccione un material de la tabla antes de restaurarlo."
            );

            return;
        }

        String estadoActual =
                seleccionado.getEstado();

        // ========================================================
        // SOLO SE PUEDEN RESTAURAR ESTOS ESTADOS
        // ========================================================

        if (!"En reparación".equalsIgnoreCase(estadoActual)) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "No se puede restaurar",
                    "Solo los materiales que están 'En reparación' pueden marcarse como reparados."
            );

            return;
        }

        // ========================================================
        // CAMBIAR A REPARADO
        // ========================================================

        boolean actualizado =
                materialDanadoDAO.actualizarEstado(
                        seleccionado.getIdMaterialDanado(),
                        "Reparado"
                );

        if (actualizado) {

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Material reparado",
                    "El material fue marcado como reparado correctamente."
            );

            cargarTabla();

        } else {

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudo actualizar el estado del material."
            );
        }
    }

    // ============================================================
    // DAR DE BAJA
    // ============================================================

    @FXML
    private void baja(ActionEvent event)
            throws IOException {

        MaterialDanado seleccionado =
                tblMateriales.getSelectionModel()
                        .getSelectedItem();

        // ========================================================
        // VERIFICAR SELECCIÓN
        // ========================================================

        if (seleccionado == null) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Seleccionar material",
                    "Seleccione un material de la tabla antes de darlo de baja."
            );

            return;
        }

        // ========================================================
        // ABRIR MODAL
        // ========================================================

        javafx.fxml.FXMLLoader loader =
                new javafx.fxml.FXMLLoader(
                        App.class.getResource(
                                "danos/DarBaja.fxml"
                        )
                );

        javafx.scene.Parent root =
                loader.load();

        DarBajaController controller =
                loader.getController();

        controller.cargarDatos(seleccionado);

        javafx.stage.Stage stage =
                new javafx.stage.Stage();

        stage.setTitle("Dar de baja");

        stage.setScene(
                new javafx.scene.Scene(root)
        );

        stage.initModality(
                javafx.stage.Modality.APPLICATION_MODAL
        );

        stage.setResizable(false);

        stage.showAndWait();

        cargarTabla();
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

        App.setRoot("inicio/Inicio");
    }

    @FXML
    private void materialesRegistrados(ActionEvent event)
            throws IOException {

        App.setRoot(
                "materiales/MaterialesRegistrados"
        );
    }

    @FXML
    private void prestamosActivos(ActionEvent event)
            throws IOException {

        App.setRoot(
                "prestamos/PrestamosActivos"
        );
    }

    @FXML
    private void materialesDanados(ActionEvent event) {

        // Ya estamos en esta pantalla.
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

        App.setRoot("cuenta/Cuenta");
    }
}