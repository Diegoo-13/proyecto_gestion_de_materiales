package utng.gtid2.dab.controllers.inicio;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import utng.gtid2.dab.App;
import utng.gtid2.dab.modelo.Usuario;
import utng.gtid2.dab.util.Navegador;
import utng.gtid2.dab.util.RelojSistema;
import utng.gtid2.dab.util.Sesion;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.TableColumn;
import javafx.scene.transform.Scale;


public class InicioController implements Initializable {

    @FXML
    private AnchorPane apContenido;

    @FXML
    private AnchorPane apMaterialesRegistrados;

    @FXML
    private AnchorPane apPrestamosActivos;

    @FXML
    private AnchorPane apMaterialesDanados;

    @FXML
    private AnchorPane apMaterialesStockBajo;

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

    @FXML
    private Button btnNuevoPrestamoo;

    @FXML
    private Label lblSaludo;

    @FXML
    private Label lblNombreUsuario;

    @FXML
    private Label lblHora;

    @FXML
    private Label lblFecha;

    @FXML
    private Label lblTotalMaterialesRegistrados;

    @FXML
    private Label lblTotalPrestamosActivos;

    @FXML
    private Label lblTotalMaterialesDanados;

    @FXML
    private Label lblTotalMaterialesStockBajo;

    @FXML
    private TableView<?> tblMovimientos;

    @FXML
    private TableColumn<?, ?> colFecha;

    @FXML
    private TableColumn<?, ?> colHora;

    @FXML
    private TableColumn<?, ?> colMaterial;

    @FXML
    private TableColumn<?, ?> colMovimiento;

    @FXML
    private TableColumn<?, ?> colResponsable;

    @FXML
    private TableColumn<?, ?> colCantidad;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        btnInicio.getStyleClass().add("active");
        
        //==============FECHA Y HORA ====================
        RelojSistema.iniciar(lblHora, lblFecha);

        //================ USUARIO ACTUAL ====================
        Usuario usuarioActual = Sesion.getUsuarioActual();

        if (usuarioActual != null) {
            lblNombreUsuario.setText(usuarioActual.getNombre());
        } else {
            lblNombreUsuario.setText("Usuario");
        }

        lblTotalMaterialesRegistrados.setText("0");
        lblTotalPrestamosActivos.setText("0");
        lblTotalMaterialesDanados.setText("0");
        lblTotalMaterialesStockBajo.setText("0");

        // Evita que el usuario cambie el tamaño de las columnas
        tblMovimientos.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        colFecha.setResizable(false);
        colHora.setResizable(false);
        colMaterial.setResizable(false);
        colMovimiento.setResizable(false);
        colResponsable.setResizable(false);
        colCantidad.setResizable(false);

        apMaterialesRegistrados.setOnMouseClicked(e -> abrirMateriales());

        apPrestamosActivos.setOnMouseClicked(e -> abrirPrestamos());

        apMaterialesDanados.setOnMouseClicked(e -> abrirMaterialesDanados());

        apMaterialesStockBajo.setOnMouseClicked(e -> abrirStockBajo());

        apContenido.sceneProperty().addListener((obs, oldScene, newScene) -> {

            if (newScene != null) {

                Scale scale = new Scale();

                apContenido.getTransforms().add(scale);

                scale.xProperty().bind(
                        newScene.widthProperty().divide(1366));

                scale.yProperty().bind(
                        newScene.heightProperty().divide(768));
            }
        });
    }

    @FXML
    private void inicio(ActionEvent event) {
        // Ya estamos en Inicio.
    }

    @FXML
    private void nuevoPrestamo(ActionEvent event) throws IOException {

        Navegador.abrirModal(
            "prestamos/NuevoPrestamo",
            "Nuevo préstamo");

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
    private void usuarios(ActionEvent event) throws IOException {

        App.setRoot("usuarios/Usuarios");

    }

    @FXML
    private void cuenta(ActionEvent event) throws IOException {

        App.setRoot("cuenta/Cuenta");

    }

    private void abrirMateriales() {

        try {

            App.setRoot("materiales/MaterialesRegistrados");

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    private void abrirPrestamos() {

        try {

            App.setRoot("prestamos/PrestamosActivos");

        } catch (IOException e) {

            e.printStackTrace();

        }

    }
        
    private void abrirMaterialesDanados() {

        try {

            App.setRoot("danos/MaterialesDanados");

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    private void abrirStockBajo() {

        try {

            App.setRoot("materiales/MaterialesRegistrados");

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

}