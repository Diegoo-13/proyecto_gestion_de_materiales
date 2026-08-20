package utng.gtid2.dab.controllers.prestamos;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import utng.gtid2.dab.dao.MaterialDAO;
import utng.gtid2.dab.dao.PrestamosDAO;
import utng.gtid2.dab.modelo.Material;
import utng.gtid2.dab.modelo.Prestamo;
import utng.gtid2.dab.util.Navegador;

public class NuevoPrestamoController implements Initializable {

    // =========================================================
    // CAMPOS DEL FORMULARIO
    // =========================================================

    @FXML
    private TextField txtResponsable;

    @FXML
    private ComboBox<Material> cmbMaterial;

    @FXML
    private TextField txtTelefono;

    @FXML
    private TextField txtCantidad;

    @FXML
    private DatePicker dpFechaPrestamo;

    @FXML
    private DatePicker dpFechaDevolucion;

    @FXML
    private ComboBox<String> cmbHoraDevolucion;

    @FXML
    private TextArea txtaObservaciones;


    // =========================================================
    // BOTONES
    // =========================================================

    @FXML
    private Button btnCerrar;

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnRegistrarPrestamo;


    // =========================================================
    // DAO
    // =========================================================

    private final MaterialDAO materialDAO =
            new MaterialDAO();

    private final PrestamosDAO prestamosDAO =
            new PrestamosDAO();


    // =========================================================
    // INICIALIZACIÓN
    // =========================================================

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // =====================================================
        // FECHA DEL PRÉSTAMO
        // =====================================================

        dpFechaPrestamo.setValue(
                LocalDate.now()
        );


        // =====================================================
        // CANTIDAD
        // =====================================================

        configurarCantidad();


        // =====================================================
        // HORARIOS
        // =====================================================

        cmbHoraDevolucion.getItems().addAll(

                "08:00",
                "09:00",
                "10:00",
                "11:00",
                "12:00",
                "13:00",
                "14:00",
                "15:00",
                "16:00",
                "17:00",
                "18:00",
                "19:00",
                "20:00"
        );

        cmbHoraDevolucion
                .getSelectionModel()
                .selectFirst();


        // =====================================================
        // CARGAR MATERIALES
        // =====================================================

        cmbMaterial.getItems().clear();

        cmbMaterial.getItems().addAll(
                materialDAO.obtenerTodosLosMateriales()
        );

        if (!cmbMaterial.getItems().isEmpty()) {

            cmbMaterial
                    .getSelectionModel()
                    .selectFirst();
        }


        // =====================================================
        // TELÉFONO
        // =====================================================

        txtTelefono.textProperty().addListener(
                (obs, oldValue, newValue) -> {

                    String numeros =
                            newValue.replaceAll("\\D", "");

                    if (numeros.length() > 10) {

                        numeros =
                                numeros.substring(0, 10);
                    }


                    StringBuilder formato =
                            new StringBuilder();


                    for (int i = 0;
                         i < numeros.length();
                         i++) {

                        if (i == 3 ||
                            i == 6 ||
                            i == 8) {

                            formato.append(" ");
                        }

                        formato.append(
                                numeros.charAt(i)
                        );
                    }


                    if (!newValue.equals(
                            formato.toString())) {

                        txtTelefono.setText(
                                formato.toString()
                        );

                        txtTelefono.positionCaret(
                                txtTelefono
                                        .getText()
                                        .length()
                        );
                    }
                }
        );


        // =====================================================
        // ENTER ENTRE CAMPOS
        // =====================================================

        txtResponsable.setOnAction(
                e -> txtTelefono.requestFocus()
        );


        txtTelefono.setOnAction(
                e -> txtCantidad.requestFocus()
        );


        txtCantidad.setOnAction(
                e -> cmbMaterial.requestFocus()
        );


        cmbMaterial.setOnAction(
                e -> cmbHoraDevolucion.requestFocus()
        );


        cmbHoraDevolucion.setOnAction(
                e -> dpFechaDevolucion.requestFocus()
        );


        dpFechaDevolucion.setOnAction(
                e -> txtaObservaciones.requestFocus()
        );
    }


    // =========================================================
    // CONFIGURAR CANTIDAD
    // =========================================================

    private void configurarCantidad() {

        /*
         * Solo permite introducir números.
         *
         * Ejemplos válidos:
         *
         * 1
         * 10
         * 50
         * 100
         *
         * No permite:
         *
         * abc
         * 10a
         * -5
         * 1.5
         */

        txtCantidad.textProperty().addListener(
                (observable, oldValue, newValue) -> {

                    if (!newValue.matches("\\d*")) {

                        txtCantidad.setText(
                                newValue.replaceAll(
                                        "[^\\d]",
                                        ""
                                )
                        );
                    }


                    /*
                     * Limitar a 3 dígitos.
                     */

                    if (txtCantidad.getText().length() > 4) {

                        txtCantidad.setText(
                                txtCantidad
                                        .getText()
                                        .substring(0, 4)
                        );
                    }


                    /*
                     * Mantener el cursor al final.
                     */

                    txtCantidad.positionCaret(
                            txtCantidad
                                    .getText()
                                    .length()
                    );
                }
        );


        /*
         * Valor inicial.
         */

        txtCantidad.setText("1");


        /*
         * Enter después de escribir la cantidad.
         */

        txtCantidad.setOnKeyPressed(event -> {

            if (event.getCode() == KeyCode.ENTER) {

                cmbMaterial.requestFocus();
            }
        });
    }


    // =========================================================
    // CERRAR
    // =========================================================

    @FXML
    private void cerrarVentana(ActionEvent event) {

        Navegador.cerrar(btnCerrar);
    }


    // =========================================================
    // CANCELAR
    // =========================================================

    @FXML
    private void cancelarPrestamo(ActionEvent event) {

        limpiarFormulario();

        Navegador.cerrar(btnCancelar);
    }


    // =========================================================
    // REGISTRAR PRÉSTAMO
    // =========================================================

    @FXML
    private void registrarPrestamo(ActionEvent event) {

        // =====================================================
        // MATERIAL
        // =====================================================

        Material material =
                cmbMaterial.getValue();


        if (material == null) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Préstamos",
                    "Seleccione un material."
            );

            return;
        }


        // =====================================================
        // CANTIDAD VACÍA
        // =====================================================

        String cantidadTexto =
                txtCantidad.getText().trim();


        if (cantidadTexto.isEmpty()) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Cantidad requerida",
                    "Ingrese la cantidad de material que desea prestar."
            );

            txtCantidad.requestFocus();

            return;
        }


        // =====================================================
        // CONVERTIR CANTIDAD
        // =====================================================

        int cantidad;

        try {

            cantidad =
                    Integer.parseInt(cantidadTexto);

        } catch (NumberFormatException e) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Cantidad inválida",
                    "La cantidad debe ser un número entero."
            );

            txtCantidad.requestFocus();

            return;
        }


        // =====================================================
        // VALIDAR RANGO
        // =====================================================

        if (cantidad < 1) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Cantidad inválida",
                    "La cantidad debe ser mayor que 0."
            );

            txtCantidad.requestFocus();

            return;
        }


        if (cantidad > 100) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Cantidad inválida",
                    "La cantidad máxima permitida es 100."
            );

            txtCantidad.requestFocus();

            return;
        }


        // =====================================================
        // FECHA DE DEVOLUCIÓN
        // =====================================================

        if (dpFechaDevolucion.getValue() == null) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Fecha requerida",
                    "Seleccione la fecha de devolución."
            );

            return;
        }


        // =====================================================
        // CREAR PRÉSTAMO
        // =====================================================

        Prestamo prestamo =
                new Prestamo();


        prestamo.setIdMaterial(
                material.getIdMaterial()
        );


        prestamo.setCantidad(
                cantidad
        );


        prestamo.setFechaPrestamo(
                dpFechaPrestamo.getValue()
        );


        prestamo.setFechaDevolucion(
                dpFechaDevolucion.getValue()
        );


        prestamo.setHoraDevolucion(
                LocalTime.parse(
                        cmbHoraDevolucion.getValue()
                )
        );


        prestamo.setResponsable(
                txtResponsable
                        .getText()
                        .trim()
        );


        prestamo.setTelefono(
                txtTelefono
                        .getText()
                        .replace(" ", "")
                        .trim()
        );


        prestamo.setObservaciones(
                txtaObservaciones
                        .getText()
                        .trim()
        );


        prestamo.setEstado(
                "Activo"
        );


        // =====================================================
        // INSERTAR
        // =====================================================

        if (prestamosDAO.insertar(prestamo)) {

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Préstamos",
                    "Préstamo registrado correctamente."
            );


            limpiarFormulario();


            Navegador.cerrar(
                    btnRegistrarPrestamo
            );

        } else {

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Préstamos",
                    "No fue posible registrar el préstamo."
            );
        }
    }


    // =========================================================
    // LIMPIAR FORMULARIO
    // =========================================================

    private void limpiarFormulario() {

        txtResponsable.clear();

        txtTelefono.clear();

        txtCantidad.setText("1");

        txtaObservaciones.clear();


        dpFechaPrestamo.setValue(
                LocalDate.now()
        );


        dpFechaDevolucion.setValue(
                null
        );


        cmbHoraDevolucion
                .getSelectionModel()
                .selectFirst();


        if (!cmbMaterial.getItems().isEmpty()) {

            cmbMaterial
                    .getSelectionModel()
                    .selectFirst();
        }
    }


    // =========================================================
    // ALERTAS
    // =========================================================

    private void mostrarAlerta(
            Alert.AlertType tipo,
            String titulo,
            String mensaje) {

        Alert alert =
                new Alert(tipo);

        alert.setTitle(titulo);

        alert.setHeaderText(null);

        alert.setContentText(mensaje);

        alert.showAndWait();
    }
}