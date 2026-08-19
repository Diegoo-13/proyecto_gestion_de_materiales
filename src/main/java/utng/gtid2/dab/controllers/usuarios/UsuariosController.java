package utng.gtid2.dab.controllers.usuarios;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

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
import utng.gtid2.dab.App;
import utng.gtid2.dab.util.Navegador;
import utng.gtid2.dab.util.RelojSistema;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import utng.gtid2.dab.dao.UsuarioDAO;
import utng.gtid2.dab.modelo.Usuario;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.control.TableCell;

public class UsuariosController implements Initializable {

    //================ MENÚ =================

    @FXML private Button btnInicio;
    @FXML private Button btnMaterialesRegistrados;
    @FXML private Button btnPrestamosActivos;
    @FXML private Button btnMaterialesDanados;
    @FXML private Button btnReportes;
    @FXML private Button btnUsuarios;
    @FXML private Button btnCuenta;

    //================ FILTROS =================

    @FXML private TextField txtBuscarUsuario;
    @FXML private ComboBox<String> cbFiltroRol;
    @FXML private ComboBox<String> cbFiltroEstado;

    @FXML private Button btnBuscar;
    @FXML private Button btnLimpiar;

    //================ INDICADORES =================

    @FXML private Label lblHora;
    @FXML private Label lblFecha;

    @FXML private Label lblTotalUsuarios;
    @FXML private Label lblTotalAdmins;
    @FXML private Label lblTotalActivos;
    @FXML
    private Label lblTotalInactivos;

    //================ TABLA =================

    @FXML
    private TableView<Usuario> tblUsuarios;

    @FXML
    private TableColumn<Usuario, Integer> colId;

    @FXML
    private TableColumn<Usuario, String> colNombres;

    @FXML
    private TableColumn<Usuario, String> colApellidoPaterno;

    @FXML
    private TableColumn<Usuario, String> colApellidoMaterno;

    @FXML
    private TableColumn<Usuario, String> colUsuario;

    @FXML
    private TableColumn<Usuario, String> colCorreo;

    @FXML
    private TableColumn<Usuario, String> colRol;

    @FXML
    private TableColumn<Usuario, String> colEstado;

    @FXML
    private TableColumn<Usuario, LocalDateTime> colFechaCreacion;

    //================ BOTONES =================

    @FXML 
    private Button btnAgregarUsuario;
    @FXML 
    private Button btnEditarUsuario;
    @FXML
    private Button btnDesactivarUsuario;


    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    private ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        btnUsuarios.getStyleClass().add("active");

        //==============FECHA Y HORA ====================
        RelojSistema.iniciar(lblHora, lblFecha);

        lblTotalUsuarios.setText("0");
        lblTotalAdmins.setText("0");
        lblTotalActivos.setText("0");
        lblTotalInactivos.setText("0");

        cbFiltroRol.getItems().addAll(
                "Todos",
                "Administrador",
                "Usuario");

        cbFiltroRol.getSelectionModel().selectFirst();

        cbFiltroEstado.getItems().addAll(
                "Todos",
                "Activo",
                "Inactivo");

        cbFiltroEstado.getSelectionModel().selectFirst();

        //================ BLOQUEAR TABLA =================

        tblUsuarios.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        colId.setResizable(false);
        colNombres.setResizable(false);
        colApellidoPaterno.setResizable(false);
        colApellidoMaterno.setResizable(false);
        colUsuario.setResizable(false);
        colCorreo.setResizable(false);
        colRol.setResizable(false);
        colEstado.setResizable(false);
        colFechaCreacion.setResizable(false);

        configurarTabla();
        distribuirColumnas();
        cargarUsuarios();
        txtBuscarUsuario.setOnAction(this::buscar);

    }

    private void configurarTabla() {

        colId.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));
        colNombres.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellidoPaterno.setCellValueFactory(new PropertyValueFactory<>("apellidoP"));
        colApellidoMaterno.setCellValueFactory(new PropertyValueFactory<>("apellidoM"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("nomUsuario"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));

        colRol.setCellFactory(column -> new TableCell<Usuario, String>() {

            @Override
            protected void updateItem(String rol, boolean empty) {
                super.updateItem(rol, empty);

                if (empty || rol == null) {
                    setText(null);
                    setStyle("");
                    return;
                }

                setText(rol);
                setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");

                if (rol.equalsIgnoreCase("Administrador")) {
                    setTextFill(Color.web("#D97706")); // Ámbar
                } else if (rol.equalsIgnoreCase("Usuario")) {
                    setTextFill(Color.web("#2563EB")); // Gris oscuro
                } else {
                    setTextFill(Color.BLACK);
                }
            }
        });

        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        colEstado.setCellFactory(column -> new TableCell<Usuario, String>() {

            @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);

                if (empty || estado == null) {
                    setText(null);
                    setStyle("");
                    return;
                }

                setText(estado);
                setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");

                if (estado.equalsIgnoreCase("Activo")) {
                    setTextFill(Color.web("#16A34A"));
                } else if (estado.equalsIgnoreCase("Inactivo")) {
                    setTextFill(Color.web("#DC2626"));
                } else {
                    setTextFill(Color.BLACK);
                }
            }
        });

        colFechaCreacion.setCellValueFactory(new PropertyValueFactory<>("fechaCreacion"));

        //================ ALINEACIÓN DE COLUMNAS =================

        // Centro
        colId.setStyle("-fx-alignment: CENTER;");
        colUsuario.setStyle("-fx-alignment: CENTER;");
        colRol.setStyle("-fx-alignment: CENTER;");
        colEstado.setStyle("-fx-alignment: CENTER;");
        colFechaCreacion.setStyle("-fx-alignment: CENTER;");

        // Izquierda
        colNombres.setStyle("-fx-alignment: CENTER-LEFT;");
        colApellidoPaterno.setStyle("-fx-alignment: CENTER-LEFT;");
        colApellidoMaterno.setStyle("-fx-alignment: CENTER-LEFT;");
        colCorreo.setStyle("-fx-alignment: CENTER-LEFT;");

    }

    private void distribuirColumnas() {

        final double ANCHO_TABLA = 1120.0;

        colId.setPrefWidth(70);
        colNombres.setPrefWidth(130);
        colApellidoPaterno.setPrefWidth(140);
        colApellidoMaterno.setPrefWidth(140);
        colUsuario.setPrefWidth(130);
        colCorreo.setPrefWidth(250);
        colRol.setPrefWidth(140);
        colEstado.setPrefWidth(145);
        colFechaCreacion.setPrefWidth(130);

        double suma =
                colId.getPrefWidth()
                + colNombres.getPrefWidth()
                + colApellidoPaterno.getPrefWidth()
                + colApellidoMaterno.getPrefWidth()
                + colUsuario.getPrefWidth()
                + colCorreo.getPrefWidth()
                + colRol.getPrefWidth()
                + colEstado.getPrefWidth()
                + colFechaCreacion.getPrefWidth();

        double espacioExtra = ANCHO_TABLA - suma;

        if (espacioExtra > 0) {

            colCorreo.setPrefWidth(
                    colCorreo.getPrefWidth() + espacioExtra
            );
        }
    }

    private void cargarUsuarios() {

        listaUsuarios.clear();

        listaUsuarios.addAll(usuarioDAO.listarTodos());

        tblUsuarios.setItems(listaUsuarios);

        actualizarContadores();

        

    }

    private void actualizarContadores() {

        long totalUsuarios = listaUsuarios.stream()
            .filter(u -> u.getRol().equalsIgnoreCase("Usuario"))
            .count();

        lblTotalUsuarios.setText(
                String.valueOf(totalUsuarios));

        long totalAdmins = listaUsuarios.stream()
                .filter(u -> u.getRol().equalsIgnoreCase("Administrador"))
                .count();

        lblTotalAdmins.setText(
                String.valueOf(totalAdmins));

        long totalActivos = listaUsuarios.stream()
                .filter(u -> u.getEstado().equalsIgnoreCase("Activo"))
                .count();

        lblTotalActivos.setText(
                String.valueOf(totalActivos));

        long totalInactivos = listaUsuarios.stream()
        .filter(u -> u.getEstado().equalsIgnoreCase("Inactivo"))
        .count();

        lblTotalInactivos.setText(
                String.valueOf(totalInactivos));

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
    private void reportes(ActionEvent event) throws IOException {
        App.setRoot("reportes/Reportes");
    }

    @FXML
    private void usuarios(ActionEvent event) {
        // Ya estamos aquí.
    }

    @FXML
    private void cuenta(ActionEvent event) throws IOException {
        App.setRoot("cuenta/Cuenta");
    }

    //================ FILTROS =================

    @FXML
    private void buscarUsuario(ActionEvent event) {

    }

    @FXML
    private void buscar(ActionEvent event) {


        listaUsuarios.clear();

        listaUsuarios.addAll(

            usuarioDAO.buscar(

                txtBuscarUsuario.getText().trim(),
                cbFiltroRol.getValue(),
                cbFiltroEstado.getValue()

            )

        );

        tblUsuarios.setItems(listaUsuarios);

        actualizarContadores();

    }

    @FXML
    private void limpiar(ActionEvent event) {

        txtBuscarUsuario.clear();

        cbFiltroRol.getSelectionModel().selectFirst();
        cbFiltroEstado.getSelectionModel().selectFirst();

        cargarUsuarios();

    }

    //================ BOTONES =================

    @FXML
    private void agregarUsuario(ActionEvent event) throws IOException {

        Navegador.abrirModal(
                "usuarios/AgregarUsuario",
                "Agregar usuario");
                
        cargarUsuarios();

    }

    @FXML
    private void editarUsuario(ActionEvent event) throws IOException {

        Usuario usuarioSeleccionado =
                tblUsuarios.getSelectionModel().getSelectedItem();

        if (usuarioSeleccionado == null) {

            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Editar usuario");
            alerta.setHeaderText(null);
            alerta.setContentText("Seleccione un usuario para editar.");
            alerta.showAndWait();

            return;
        }

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/utng/gtid2/dab/usuarios/EditarUsuario.fxml"));

        Parent root = loader.load();

        EditarUsuarioController controller = loader.getController();

        controller.setUsuario(usuarioSeleccionado);

        Stage stage = new Stage();
        stage.setTitle("Editar usuario");
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        cargarUsuarios();

    }

    @FXML
    private void desactivarUsuario(ActionEvent event) throws IOException {

        Usuario usuarioSeleccionado =
                tblUsuarios.getSelectionModel().getSelectedItem();

        if (usuarioSeleccionado == null) {

            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Desactivar usuario");
            alerta.setHeaderText(null);
            alerta.setContentText("Seleccione un usuario para desactivar.");
            alerta.showAndWait();

            return;

        }

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/utng/gtid2/dab/usuarios/DesactivarUsuario.fxml"));

        Parent root = loader.load();

        DesactivarUsuarioController controller = loader.getController();

        controller.setUsuario(usuarioSeleccionado);

        Stage stage = new Stage();
        stage.setTitle("Desactivar usuario");
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        cargarUsuarios();

    }
}