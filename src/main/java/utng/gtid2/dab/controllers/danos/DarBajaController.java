package utng.gtid2.dab.controllers.danos;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import utng.gtid2.dab.dao.MaterialDanadoDAO;
import utng.gtid2.dab.modelo.MaterialDanado;
import utng.gtid2.dab.util.Navegador;

public class DarBajaController implements Initializable {

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtReporto;

    @FXML
    private TextField txtMaterial;

    @FXML
    private TextField txtEstado;

    @FXML
    private TextField txtCategoria;

    @FXML
    private TextField txtFechaReporte;

    @FXML
    private TextArea txtaMotivo;

    @FXML
    private Button btnCerrarVentana;

    @FXML
    private Button btnCancelarBaja;

    @FXML
    private Button btnConfirmarBaja;

    // ============================================================
    // DAO
    // ============================================================

    private final MaterialDanadoDAO materialDanadoDAO =
            new MaterialDanadoDAO();

    // Material seleccionado
    private MaterialDanado materialSeleccionado;

    // ============================================================
    // INICIALIZACIÓN
    // ============================================================

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Ya no ponemos datos de ejemplo aquí.
    }

    // ============================================================
    // RECIBIR MATERIAL SELECCIONADO
    // ============================================================

    public void cargarDatos(MaterialDanado material) {

        this.materialSeleccionado = material;

        if (material == null) {
            return;
        }

        // ID
        txtId.setText(
                String.valueOf(
                        material.getIdMaterialDanado()
                )
        );

        // REPORTÓ
        txtReporto.setText(
                String.valueOf(
                        material.getIdUsuario()
                )
        );

        // MATERIAL
        txtMaterial.setText(
                material.getNombreMaterial()
        );

        // ESTADO
        txtEstado.setText(
                material.getEstado()
        );

        // CATEGORÍA
        txtCategoria.setText(
                material.getCategoria()
        );

        // FECHA
        if (material.getFechaReporte() != null) {

            txtFechaReporte.setText(
                    material.getFechaReporte()
                            .format(
                                    DateTimeFormatter.ofPattern(
                                            "dd/MM/yyyy"
                                    )
                            )
            );
        }
    }

    // ============================================================
    // CERRAR
    // ============================================================

    @FXML
    private void cerrarVentana(ActionEvent event) {

        Navegador.cerrar(btnCerrarVentana);
    }

    // ============================================================
    // CANCELAR
    // ============================================================

    @FXML
    private void cancelarBaja(ActionEvent event) {

        txtaMotivo.clear();

        Navegador.cerrar(btnCancelarBaja);
    }

    // ============================================================
    // CONFIRMAR BAJA
    // ============================================================

    @FXML
    private void confirmarBaja(ActionEvent event) {

        // Verificar que exista material seleccionado
        if (materialSeleccionado == null) {

            mostrarAlerta(
                    "Error",
                    "No se seleccionó ningún material."
            );

            return;
        }

        // Obtener motivo
        String motivo = txtaMotivo.getText()
                .trim();

        // Verificar motivo
        if (motivo.isEmpty()) {

            mostrarAlerta(
                    "Motivo requerido",
                    "Escriba el motivo por el cual se dará de baja el material."
            );

            return;
        }

        // ========================================================
        // ACTUALIZAR EN POSTGRESQL
        // ========================================================

        boolean actualizado =
                materialDanadoDAO.darDeBaja(
                        materialSeleccionado.getIdMaterialDanado(),
                        motivo
                );

        if (actualizado) {

            mostrarAlerta(
                    "Baja realizada",
                    "El material fue dado de baja correctamente."
            );

            Navegador.cerrar(btnConfirmarBaja);

        } else {

            mostrarAlerta(
                    "Error",
                    "No se pudo dar de baja el material."
            );
        }
    }

    // ============================================================
    // ALERTA
    // ============================================================

    private void mostrarAlerta(
            String titulo,
            String mensaje) {

        Alert alerta = new Alert(
                Alert.AlertType.INFORMATION
        );

        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }
}