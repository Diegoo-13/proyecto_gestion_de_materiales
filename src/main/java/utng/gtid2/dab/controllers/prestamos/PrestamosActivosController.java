package utng.gtid2.dab.controllers.prestamos;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.List;
import java.util.Optional;

import javafx.scene.paint.Color;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import utng.gtid2.dab.App;
import utng.gtid2.dab.dao.PrestamosDAO;
import utng.gtid2.dab.modelo.Prestamo;
import utng.gtid2.dab.util.Navegador;
import utng.gtid2.dab.util.RelojSistema;

import javafx.scene.control.TableCell;

public class PrestamosActivosController implements Initializable {

    //================ MENÚ =================

    @FXML private Button btnInicio;
    @FXML private Button btnMaterialesRegistrados;
    @FXML private Button btnPrestamosActivos;
    @FXML private Button btnMaterialesDanados;
    @FXML private Button btnReportes;
    @FXML private Button btnUsuarios;
    @FXML private Button btnCuenta;

    //================ BOTONES =================

    @FXML private Button btnBuscar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnNuevoPrestamo;
    @FXML private Button btnMarcarDevuelto;

    //================ FILTROS =================

    @FXML private TextField txtBuscar;          // Folio
    @FXML private TextField txtResponsable;

    @FXML private ComboBox<String> cbEstado;

    @FXML private DatePicker dpDesde;
    @FXML private DatePicker dpHasta;

    //================ ENCABEZADO =================

    @FXML private Label lblHora;
    @FXML
    private Label lblFecha;

    @FXML private Label lblPrestamosActivos;
    @FXML private Label lblPrestamosDevueltos;
    @FXML private Label lblVenceHoy;
    @FXML private Label lblPrestamosVencidos;

    //================ TABLA =================

    @FXML
    private TableView<Prestamo> tblPrestamos;

    @FXML
    private TableColumn<Prestamo,Integer> colFolio;

    @FXML
    private TableColumn<Prestamo,String> colResponsable;

    @FXML
    private TableColumn<Prestamo,String> colTelefono;

    @FXML
    private TableColumn<Prestamo,String> colMaterial;

    @FXML
    private TableColumn<Prestamo,Integer> colCantidad;

    @FXML
    private TableColumn<Prestamo,LocalDate> colFechaPrestamo;

    @FXML
    private TableColumn<Prestamo,LocalDate> colFechaDevolucion;

    @FXML
    private TableColumn<Prestamo, LocalTime> colHoraDevolucion;

    @FXML
    private TableColumn<Prestamo,String> colEstado;

    //================ DAO =================

    private final PrestamosDAO prestamosDAO = new PrestamosDAO();

    private final ObservableList<Prestamo> listaPrestamos = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //================ MÓDULO ACTUAL =================
        btnPrestamosActivos.getStyleClass().add("active");
        
        //==============FECHA Y HORA ====================
        RelojSistema.iniciar(lblHora, lblFecha);

        //================ COMBO ESTADO =================

        cbEstado.getItems().addAll(
                                "Todos",
                                "Activo",
                                "Devuelto",
                                "Vence hoy",
                                "Vencido");

        cbEstado.getSelectionModel().selectFirst();

        //================ TABLA =================

        colFolio.setCellValueFactory(new PropertyValueFactory<>("idPrestamo"));
        colResponsable.setCellValueFactory(new PropertyValueFactory<>("responsable"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colMaterial.setCellValueFactory(new PropertyValueFactory<>("nombreMaterial"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colFechaPrestamo.setCellValueFactory(new PropertyValueFactory<>("fechaPrestamo"));
        colFechaDevolucion.setCellValueFactory(new PropertyValueFactory<>("fechaDevolucion"));
        colHoraDevolucion.setCellValueFactory(new PropertyValueFactory<>("horaDevolucion"));
        colHoraDevolucion.setCellFactory(column -> new TableCell<Prestamo, LocalTime>() {

            @Override
            protected void updateItem(LocalTime hora, boolean empty) {
                super.updateItem(hora, empty);

                if (empty || hora == null) {
                    setText(null);
                } else {
                    setText(hora.format(DateTimeFormatter.ofPattern("HH:mm")));
                }

                setStyle("-fx-alignment: CENTER;");
            }

        });

        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colEstado.setCellFactory(column -> new TableCell<Prestamo, String>() {

            @Override
            protected void updateItem(String item, boolean empty) {

                super.updateItem(item, empty);

                if (empty) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                Prestamo prestamo = getTableView().getItems().get(getIndex());

                LocalDate hoy = LocalDate.now();
                LocalTime horaActual = LocalTime.now();

                LocalDate fecha = prestamo.getFechaDevolucion();
                LocalTime hora = prestamo.getHoraDevolucion();

                setStyle("-fx-alignment:CENTER; -fx-font-weight:bold;");

                // Si ya fue devuelto
                if (prestamo.getEstado().equalsIgnoreCase("Devuelto")) {

                    setText("Devuelto");
                    setTextFill(Color.web("#16A34A"));

                }
                // Si la fecha ya pasó
                else if (fecha.isBefore(hoy)) {

                    setText("Vencido");
                    setTextFill(Color.web("#DC2626"));

                }
                // Si la devolución es hoy
                else if (fecha.isEqual(hoy)) {

                    if (hora != null && !horaActual.isBefore(hora)) {

                        setText("Vencido");
                        setTextFill(Color.web("#DC2626"));

                    } else {

                        setText("Vence hoy");
                        setTextFill(Color.web("#D97706"));

                    }

                }
                // Si todavía faltan días
                else {

                    setText("Activo");
                    setTextFill(Color.web("#2563EB"));

                }

            }

        });

        //================ BLOQUEAR REDIMENSIONAMIENTO =================

        colFolio.setResizable(false);
        colResponsable.setResizable(false);
        colTelefono.setResizable(false);
        colMaterial.setResizable(false);
        colCantidad.setResizable(false);
        colFechaPrestamo.setResizable(false);
        colFechaDevolucion.setResizable(false);
        colEstado.setResizable(false);

        //================ ALINEACIÓN =================

        // Centro
        colFolio.setStyle("-fx-alignment: CENTER;");
        colTelefono.setStyle("-fx-alignment: CENTER;");
        colCantidad.setStyle("-fx-alignment: CENTER;");
        colFechaPrestamo.setStyle("-fx-alignment: CENTER;");
        colFechaDevolucion.setStyle("-fx-alignment: CENTER;");
        colEstado.setStyle("-fx-alignment: CENTER;");

        // Izquierda
        colResponsable.setStyle("-fx-alignment: CENTER-LEFT;");
        colMaterial.setStyle("-fx-alignment: CENTER-LEFT;");

        // Al presionar Enter en Folio buscará automáticamente
        txtBuscar.setOnAction(e -> buscar(null));
        txtResponsable.setOnAction(e -> buscar(null));
 
        tblPrestamos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        cargarPrestamos();
    }

    //================ CARGAR TABLA =================

    private void cargarPrestamos() {

        listaPrestamos.clear();

        listaPrestamos.addAll(prestamosDAO.listarTodos());

        tblPrestamos.setItems(listaPrestamos);

        actualizarContadores();
    }

    //================ CONTADORES =================

    private void actualizarContadores() {

        int activos = 0;
        int devueltos = 0;
        int venceHoy = 0;
        int vencidos = 0;

        for (Prestamo prestamo : listaPrestamos) {

            switch (prestamo.getEstadoCalculado()) {

                case "Activo":
                    activos++;
                    break;

                case "Devuelto":
                    devueltos++;
                    break;

                case "Vence hoy":
                    venceHoy++;
                    break;

                case "Vencido":
                    vencidos++;
                    break;
            }
        }

        lblPrestamosActivos.setText(String.valueOf(activos));
        lblPrestamosDevueltos.setText(String.valueOf(devueltos));
        lblVenceHoy.setText(String.valueOf(venceHoy));
        lblPrestamosVencidos.setText(String.valueOf(vencidos));
    }

    //================ BUSCAR =================

   @FXML
    private void buscar(ActionEvent event) {

        String folio = txtBuscar.getText().trim();
        String responsable = txtResponsable.getText().trim();
        String estado = cbEstado.getValue();

        LocalDate desde = dpDesde.getValue();
        LocalDate hasta = dpHasta.getValue();

        // Validar que el folio sea numérico
        if (!folio.isEmpty()) {

            try {
                Integer.parseInt(folio);

            } catch (NumberFormatException e) {

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Búsqueda");
                alert.setHeaderText(null);
                alert.setContentText("El folio debe ser un número.");

                alert.showAndWait();

                txtBuscar.requestFocus();
                return;
            }
        }

        // Limpiar lista
        listaPrestamos.clear();

        // Buscar SIN filtrar por estado en SQL
        List<Prestamo> resultados = prestamosDAO.buscar(
                folio,
                responsable,
                "Todos",
                desde,
                hasta);

        // Filtrar por estado calculado en Java
        if (!estado.equalsIgnoreCase("Todos")) {

            resultados.removeIf(prestamo ->
                    !prestamo.getEstadoCalculado()
                            .equalsIgnoreCase(estado));
        }

        listaPrestamos.addAll(resultados);

        tblPrestamos.setItems(listaPrestamos);

        actualizarContadores();
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
    private void prestamosActivos(ActionEvent event) {
        // Ya estamos aquí
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

    //================ BOTONES =================

    @FXML
    private void nuevoPrestamo(ActionEvent event) throws IOException {

        Navegador.abrirModal(
                "prestamos/NuevoPrestamo",
                "Nuevo Préstamo");

        cargarPrestamos();
    }

    //================ LIMPIAR =================

    @FXML
    private void limpiar(ActionEvent event) {

        txtBuscar.clear();
        txtResponsable.clear();

        dpDesde.setValue(null);
        dpHasta.setValue(null);

        cbEstado.getSelectionModel().selectFirst();

        cargarPrestamos();
    }

    //================ MARCAR DEVUELTO =================
    @FXML
    private void devuelto(ActionEvent event) {

            Prestamo seleccionado =
                    tblPrestamos.getSelectionModel().getSelectedItem();

            if (seleccionado == null) {

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Préstamos");
        alert.setHeaderText(null);
        alert.setContentText("Seleccione un préstamo.");

        alert.showAndWait();

        return;
    }

    //==========================================
    // YA ESTÁ DEVUELTO
    //==========================================

    if (seleccionado.getEstado().equalsIgnoreCase("Devuelto")) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Préstamos");
        alert.setHeaderText(null);
        alert.setContentText("Este préstamo ya fue marcado como devuelto.");

        alert.showAndWait();

        return;
    }

        //================ CONFIRMAR DEVOLUCIÓN =================

        Alert confirmar = new Alert(Alert.AlertType.CONFIRMATION);

        confirmar.setTitle("Confirmar devolución");
        confirmar.setHeaderText("¿Desea marcar este préstamo como devuelto?");
        confirmar.setContentText(
                "Folio: " + seleccionado.getIdPrestamo()
                + "\nResponsable: " + seleccionado.getResponsable()
                + "\nMaterial: " + seleccionado.getNombreMaterial()
                + "\n\nEsta acción actualizará el estado del préstamo.");

        //================ CONFIRMAR DEVOLUCIÓN =================
        confirmar.setTitle("Confirmar devolución");
        confirmar.setHeaderText("¿Desea marcar este préstamo como devuelto?");
        confirmar.setContentText(
                "Folio: " + seleccionado.getIdPrestamo()
                + "\nResponsable: " + seleccionado.getResponsable()
                + "\nMaterial: " + seleccionado.getNombreMaterial()
                + "\n\nEsta acción actualizará el estado del préstamo.");

        Optional<ButtonType> resultado = confirmar.showAndWait();

        if (resultado.isEmpty() || resultado.get() != ButtonType.OK) {
            return;
        }

        //================ ACTUALIZAR PRÉSTAMO =================

        boolean actualizado =
                prestamosDAO.registrarDevolucion(
                        seleccionado.getIdPrestamo());

        if (actualizado) {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Préstamos");
            alert.setHeaderText(null);
            alert.setContentText(
                    "El préstamo fue marcado como devuelto correctamente.");

            alert.showAndWait();

            cargarPrestamos();

        } else {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Préstamos");
            alert.setHeaderText(null);
            alert.setContentText(
                    "No fue posible actualizar el préstamo.");

            alert.showAndWait();
        }

    }

}