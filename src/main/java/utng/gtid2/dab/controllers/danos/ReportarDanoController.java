package utng.gtid2.dab.controllers.danos;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import utng.gtid2.dab.dao.MaterialDanadoDAO;
import utng.gtid2.dab.util.Navegador;
import utng.gtid2.dab.conexionbd.Conexion;
import utng.gtid2.dab.modelo.MaterialDanado;
import javafx.scene.control.Alert;

public class ReportarDanoController implements Initializable {

    // ============================================================
    // CONTROLES
    // ============================================================

    @FXML
    private ComboBox<String> cbMaterial;

    @FXML
    private ComboBox<String> cbTipoDanio;

    @FXML
    private TextField txtEstado;

    @FXML
    private TextField txtReporto;

    @FXML
    private DatePicker dpFechaReporte;

    @FXML
    private TextArea txtaDescripcion;

    @FXML
    private Button btnCerrarVentana;

    @FXML
    private Button btnCancelarDano;

    @FXML
    private Button btnGuardarReporte;

    // ============================================================
    // DAO
    // ============================================================

    private final MaterialDanadoDAO materialDanadoDAO =
            new MaterialDanadoDAO();

    // ============================================================
    // INICIALIZAR
    // ============================================================

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        System.out.println("ENTRO A REPORTAR DAÑO");

        // ========================================================
        // FECHA ACTUAL
        // ========================================================

        dpFechaReporte.setValue(LocalDate.now());

        
        // ========================================================
        // ESTADO
        // ========================================================

        txtEstado.setText("Reportado");
        txtEstado.setEditable(false);

        // ========================================================
        // CARGAR MATERIALES
        // ========================================================

        cargarMateriales();

        // ========================================================
        // TIPOS DE DAÑO
        // ========================================================

        cbTipoDanio.getItems().clear();

        cbTipoDanio.getItems().addAll(
                "Golpe",
                "Pantalla dañada",
                "No enciende",
                "Cable dañado",
                "Falla eléctrica",
                "Piezas faltantes",
                "Otro"
        );
    }

    // ============================================================
    // CARGAR MATERIALES
    // ============================================================

    private void cargarMateriales() {

        String sql =
                "SELECT id_material, nom_material "
                + "FROM material "
                + "ORDER BY nom_material";

        cbMaterial.getItems().clear();

        try (Connection conexion = Conexion.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                int idMaterial =
                        rs.getInt("id_material");

                String nombreMaterial =
                        rs.getString("nom_material");

                cbMaterial.getItems().add(
                        idMaterial + " - " + nombreMaterial
                );
            }

        } catch (SQLException e) {

            System.err.println(
                    "Error al cargar materiales:"
            );

            e.printStackTrace();
        }
    }

    // ============================================================
    // BUSCAR ID DEL USUARIO
    // ============================================================

    private int buscarIdUsuario(String nombre) {

        String sql =
                "SELECT id_usuario "
                + "FROM usuario "
                + "WHERE LOWER(nombre || ' ' || apellido_p) "
                + "LIKE LOWER(?) "
                + "LIMIT 1";

        try (Connection conexion = Conexion.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(
                    1,
                    "%" + nombre.trim() + "%"
            );

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return rs.getInt("id_usuario");
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "Error al buscar usuario:"
            );

            e.printStackTrace();
        }

        return -1;
    }

    // ============================================================
    // CERRAR VENTANA
    // ============================================================

    @FXML
    private void cerrarVentana(ActionEvent event) {

        Navegador.cerrar(btnCerrarVentana);
    }

    // ============================================================
    // CANCELAR
    // ============================================================

    @FXML
    private void cancelarDano(ActionEvent event) {

        Navegador.cerrar(btnCancelarDano);
    }

    // ============================================================
    // GUARDAR REPORTE
    // ============================================================

   @FXML
        private void guardarReporte(ActionEvent event) {

        // ========================================================
        // VALIDAR MATERIAL
        // ========================================================

        if (cbMaterial.getValue() == null
                || cbMaterial.getValue().trim().isEmpty()) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Campo requerido",
                        "Debes seleccionar un material."
                );

                cbMaterial.requestFocus();
                return;
        }

        // ========================================================
        // VALIDAR QUIÉN REPORTÓ
        // ========================================================

        if (txtReporto.getText() == null
                || txtReporto.getText().trim().isEmpty()) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Campo requerido",
                        "Debes escribir quién reportó el daño."
                );

                txtReporto.requestFocus();
                return;
        }

        // ========================================================
        // VALIDAR TIPO DE DAÑO
        // ========================================================

        if (cbTipoDanio.getValue() == null
                || cbTipoDanio.getValue().trim().isEmpty()) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Campo requerido",
                        "Debes seleccionar el tipo de daño."
                );

                cbTipoDanio.requestFocus();
                return;
        }

        // ========================================================
        // VALIDAR FECHA
        // ========================================================

        if (dpFechaReporte.getValue() == null) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Campo requerido",
                        "Debes seleccionar la fecha del reporte."
                );

                dpFechaReporte.requestFocus();
                return;
        }

        // ========================================================
        // VALIDAR DESCRIPCIÓN
        // ========================================================

        if (txtaDescripcion.getText() == null
                || txtaDescripcion.getText().trim().isEmpty()) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Campo requerido",
                        "Debes escribir una descripción del daño."
                );

                txtaDescripcion.requestFocus();
                return;
        }

        // ========================================================
        // OBTENER ID DEL MATERIAL
        // ========================================================

        String materialSeleccionado =
                cbMaterial.getValue();

        int posicionGuion =
                materialSeleccionado.indexOf(" - ");

        if (posicionGuion == -1) {

                mostrarAlerta(
                        Alert.AlertType.ERROR,
                        "Material inválido",
                        "El material seleccionado no es válido."
                );

                return;
        }

        int idMaterial;

        try {

                idMaterial = Integer.parseInt(
                        materialSeleccionado
                                .substring(0, posicionGuion)
                                .trim()
                );

        } catch (NumberFormatException e) {

                mostrarAlerta(
                        Alert.AlertType.ERROR,
                        "Error",
                        "No se pudo obtener el ID del material."
                );

                return;
        }

        // ========================================================
        // BUSCAR ID DEL USUARIO
        // ========================================================

        String nombreReporta =
                txtReporto.getText().trim();

        int idUsuario =
                buscarIdUsuario(nombreReporta);

        if (idUsuario == -1) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Usuario no encontrado",
                        "No se encontró un usuario con el nombre: "
                                + nombreReporta
                );

                txtReporto.requestFocus();
                return;
        }

        // ========================================================
        // FECHA
        // ========================================================

        LocalDate fecha =
                dpFechaReporte.getValue();

        // ========================================================
        // ESTADO FIJO
        // ========================================================

        String estado = "Reportado";

        // ========================================================
        // TIPO DE DAÑO
        // ========================================================

        String tipoDanio =
                cbTipoDanio.getValue();

        // ========================================================
        // DESCRIPCIÓN
        // ========================================================

        String descripcion =
                txtaDescripcion.getText().trim();

        String descripcionCompleta =
                "Tipo de daño: "
                + tipoDanio
                + ". "
                + descripcion;

        // ========================================================
        // CREAR OBJETO
        // ========================================================

        MaterialDanado md =
                new MaterialDanado();

        md.setIdMaterial(idMaterial);
        md.setIdUsuario(idUsuario);
        md.setFechaReporte(fecha);
        md.setEstado(estado);
        md.setDescripcionDanio(descripcionCompleta);
        md.setMotivoBaja(null);

        // ========================================================
        // INSERTAR
        // ========================================================

        boolean guardado =
                materialDanadoDAO.insertar(md);

        if (guardado) {

                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Reporte guardado",
                        "El reporte de daño se guardó correctamente."
                );

                limpiarFormulario();

                Navegador.cerrar(
                        btnGuardarReporte
                );

        } else {

                mostrarAlerta(
                        Alert.AlertType.ERROR,
                        "Error",
                        "No se pudo guardar el reporte de daño."
                );
        }
        }

        private void limpiarFormulario() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'limpiarFormulario'");
}

        private void mostrarAlerta(
        Alert.AlertType tipo,
        String titulo,
        String mensaje) {

    Alert alerta = new Alert(tipo);

    alerta.setTitle(titulo);
    alerta.setHeaderText(null);
    alerta.setContentText(mensaje);

    alerta.showAndWait();
}
        }
