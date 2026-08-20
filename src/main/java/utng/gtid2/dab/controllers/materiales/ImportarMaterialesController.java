package utng.gtid2.dab.controllers.materiales;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import utng.gtid2.dab.dao.MaterialDAO;
import utng.gtid2.dab.modelo.Material;

/**
 * Controlador de la ventana utilizada para importar materiales
 * desde un archivo Excel.
 *
 * <p>Permite seleccionar un archivo XLSX, leer sus registros,
 * validar la información y registrar los materiales en la base
 * de datos.</p>
 *
 * <p>El archivo Excel debe contener las siguientes columnas:</p>
 *
 * <ul>
 *     <li>Nombre</li>
 *     <li>Descripción</li>
 *     <li>Categoría</li>
 *     <li>Tipo</li>
 *     <li>Cantidad</li>
 *     <li>Stock mínimo</li>
 *     <li>Unidad</li>
 *     <li>Estado</li>
 *     <li>Ubicación</li>
 * </ul>
 *
 * <p>El campo Estado no se toma del Excel para determinar el estado
 * inicial. Los materiales importados se registran automáticamente
 * como {@code Disponible}.</p>
 *
 * @author UTNG
 * @version 1.0
 */
public class ImportarMaterialesController {

    // =========================================================
    // CONTROLES FXML
    // =========================================================

    /**
     * Campo donde se muestra el nombre del archivo seleccionado.
     */
    @FXML
    private TextField txtArchivo;

    /**
     * Botón para seleccionar el archivo Excel.
     */
    @FXML
    private Button btnSeleccionarArchivo;

    /**
     * Botón para cancelar la operación.
     */
    @FXML
    private Button btnCancelar;

    /**
     * Botón para iniciar la importación.
     */
    @FXML
    private Button btnContinuar;

    // =========================================================
    // DATOS
    // =========================================================

    /**
     * Archivo seleccionado por el usuario.
     */
    private File archivoSeleccionado;

    /**
     * DAO utilizado para registrar los materiales.
     */
    private final MaterialDAO materialDAO =
            new MaterialDAO();

    /**
     * Formateador utilizado para obtener el contenido de
     * las celdas de Excel como texto.
     */
    private final DataFormatter dataFormatter =
            new DataFormatter();

    // =========================================================
    // SELECCIONAR ARCHIVO
    // =========================================================

    /**
     * Abre el selector de archivos y permite seleccionar
     * un archivo Excel con extensión XLSX.
     *
     * @param event evento generado al presionar Seleccionar
     */
    @FXML
    private void seleccionarArchivo(ActionEvent event) {

        FileChooser fileChooser =
                new FileChooser();

        fileChooser.setTitle(
                "Seleccionar archivo Excel"
        );

        FileChooser.ExtensionFilter filtroExcel =
                new FileChooser.ExtensionFilter(
                        "Archivos Excel (*.xlsx)",
                        "*.xlsx"
                );

        fileChooser
                .getExtensionFilters()
                .add(filtroExcel);

        Stage stage =
                (Stage) btnSeleccionarArchivo
                        .getScene()
                        .getWindow();

        File archivo =
                fileChooser.showOpenDialog(stage);

        if (archivo != null) {

            archivoSeleccionado = archivo;

            txtArchivo.setText(
                    archivo.getName()
            );

            btnContinuar.setDisable(false);
        }
    }

    // =========================================================
    // CONTINUAR / IMPORTAR
    // =========================================================

    /**
     * Inicia el proceso de lectura e importación del archivo Excel.
     *
     * @param event evento generado al presionar Continuar
     */
    @FXML
    private void continuar(ActionEvent event) {

        if (archivoSeleccionado == null) {

            mostrarAlerta(
                    "Archivo requerido",
                    "Selecciona un archivo Excel antes de continuar.",
                    Alert.AlertType.WARNING
            );

            return;
        }

        try {

            List<Material> materiales =
                    leerExcel(archivoSeleccionado);

            if (materiales.isEmpty()) {

                mostrarAlerta(
                        "Sin registros",
                        "El archivo Excel no contiene materiales para importar.",
                        Alert.AlertType.WARNING
                );

                return;
            }

            int importados = 0;

            for (Material material : materiales) {

                if (materialDAO.agregarMaterial(material)) {
                    importados++;
                }
            }

            if (importados == materiales.size()) {

                mostrarAlerta(
                        "Importación completada",
                        "Se registraron correctamente "
                                + importados
                                + " materiales.",
                        Alert.AlertType.INFORMATION
                );

                cerrarVentana();

            } else {

                mostrarAlerta(
                        "Importación parcial",
                        "Se encontraron "
                                + materiales.size()
                                + " registros válidos, pero solamente "
                                + importados
                                + " pudieron registrarse.",
                        Alert.AlertType.WARNING
                );
            }

        } catch (IOException e) {

            e.printStackTrace();

            mostrarAlerta(
                    "Error al leer Excel",
                    "No fue posible leer el archivo Excel.\n\n"
                            + "Verifica que sea un archivo .xlsx válido.",
                    Alert.AlertType.ERROR
            );

        } catch (Exception e) {

            e.printStackTrace();

            mostrarAlerta(
                    "Error de importación",
                    "Ocurrió un error durante la importación:\n\n"
                            + e.getMessage(),
                    Alert.AlertType.ERROR
            );
        }
    }

    // =========================================================
    // LEER EXCEL
    // =========================================================

    /**
     * Lee todos los registros del archivo Excel y los convierte
     * en objetos {@link Material}.
     *
     * <p>La primera fila del archivo se considera como encabezado
     * y no se procesa como material.</p>
     *
     * @param archivo archivo Excel que será leído
     * @return lista de materiales encontrados
     * @throws IOException si ocurre un problema al abrir o leer
     *                     el archivo
     */
    private List<Material> leerExcel(File archivo)
            throws IOException {

        List<Material> materiales =
                new ArrayList<>();

        try (
                FileInputStream fis =
                        new FileInputStream(archivo);

                Workbook workbook =
                        new XSSFWorkbook(fis)
        ) {

            Sheet sheet =
                    workbook.getSheetAt(0);

            if (sheet.getPhysicalNumberOfRows() <= 1) {
                return materiales;
            }

            /*
             * La fila 0 corresponde a los encabezados.
             */
            for (int i = 1;
                 i <= sheet.getLastRowNum();
                 i++) {

                Row row =
                        sheet.getRow(i);

                if (row == null ||
                        filaVacia(row)) {

                    continue;
                }

                Material material =
                        convertirFilaMaterial(row, i + 1);

                materiales.add(material);
            }
        }

        return materiales;
    }

    // =========================================================
    // CONVERTIR FILA
    // =========================================================

    /**
     * Convierte una fila del Excel en un objeto Material.
     *
     * <p>El orden esperado de las columnas es:</p>
     *
     * <pre>
     * 0 Nombre
     * 1 Descripción
     * 2 Categoría
     * 3 Tipo
     * 4 Cantidad
     * 5 Stock mínimo
     * 6 Unidad
     * 7 Estado
     * 8 Ubicación
     * </pre>
     *
     * @param row fila de Excel
     * @param numeroFila número visible de la fila
     * @return material construido a partir de la fila
     */
    private Material convertirFilaMaterial(
            Row row,
            int numeroFila) {

        String nombre =
                obtenerTexto(row, 0);

        String descripcion =
                obtenerTexto(row, 1);

        String categoria =
                obtenerTexto(row, 2);

        String tipo =
                obtenerTexto(row, 3);

        String cantidadTexto =
                obtenerTexto(row, 4);

        String stockMinimoTexto =
                obtenerTexto(row, 5);

        String unidad =
                obtenerTexto(row, 6);

        /*
         * La columna Estado existe en el formato,
         * pero no se utiliza para determinar el estado
         * inicial del material.
         */
        String estado =
                "Disponible";

        String ubicacion =
                obtenerTexto(row, 8);

        validarTexto(
                nombre,
                "Nombre",
                numeroFila
        );

        validarTexto(
                categoria,
                "Categoría",
                numeroFila
        );

        validarTexto(
                tipo,
                "Tipo",
                numeroFila
        );

        validarTexto(
                unidad,
                "Unidad",
                numeroFila
        );

        validarTexto(
                ubicacion,
                "Ubicación",
                numeroFila
        );

        int cantidad =
                convertirEntero(
                        cantidadTexto,
                        "Cantidad",
                        numeroFila
                );

        int stockMinimo =
                convertirEntero(
                        stockMinimoTexto,
                        "Stock mínimo",
                        numeroFila
                );

        if (cantidad < 0) {

            throw new IllegalArgumentException(
                    "La cantidad de la fila "
                            + numeroFila
                            + " no puede ser negativa."
            );
        }

        if (stockMinimo < 0) {

            throw new IllegalArgumentException(
                    "El stock mínimo de la fila "
                            + numeroFila
                            + " no puede ser negativo."
            );
        }

        /*
         * Normalizamos el tipo.
         */
        if (!tipo.equalsIgnoreCase("Activo")
                && !tipo.equalsIgnoreCase("Consumible")) {

            throw new IllegalArgumentException(
                    "El tipo de la fila "
                            + numeroFila
                            + " debe ser 'Activo' o 'Consumible'."
            );
        }

        Material material =
                new Material();

        material.setNomMaterial(nombre);

        material.setDescripcion(
                descripcion
        );

        material.setStockActual(
                cantidad
        );

        material.setStockMinimo(
                stockMinimo
        );

        material.setTipo(
                tipo.equalsIgnoreCase("Activo")
                        ? "Activo"
                        : "Consumible"
        );

        material.setUnidad(
                unidad
        );

        material.setEstado(
                estado
        );

        /*
         * Obtenemos o creamos la categoría.
         */
        int idCategoria =
                materialDAO.obtenerOCrearCategoria(
                        categoria
                );

        if (idCategoria == -1) {

            throw new IllegalArgumentException(
                    "No se pudo obtener la categoría '"
                            + categoria
                            + "' en la fila "
                            + numeroFila
            );
        }

        /*
         * Obtenemos o creamos la ubicación.
         */
        int idUbicacion =
                materialDAO.obtenerOCrearUbicacion(
                        ubicacion
                );

        if (idUbicacion == -1) {

            throw new IllegalArgumentException(
                    "No se pudo obtener la ubicación '"
                            + ubicacion
                            + "' en la fila "
                            + numeroFila
            );
        }

        material.setIdCategoria(
                idCategoria
        );

        material.setIdUbicacion(
                idUbicacion
        );

        return material;
    }

    // =========================================================
    // OBTENER TEXTO DE CELDA
    // =========================================================

    /**
     * Obtiene el contenido de una celda como texto.
     *
     * @param row fila de Excel
     * @param columna índice de la columna
     * @return contenido de la celda o cadena vacía
     */
    private String obtenerTexto(
            Row row,
            int columna) {

        Cell cell =
                row.getCell(
                        columna,
                        Row.MissingCellPolicy
                                .RETURN_BLANK_AS_NULL
                );

        if (cell == null) {
            return "";
        }

        return dataFormatter
                .formatCellValue(cell)
                .trim();
    }

    // =========================================================
    // VALIDAR TEXTO
    // =========================================================

    /**
     * Verifica que un campo obligatorio tenga información.
     *
     * @param valor valor obtenido del Excel
     * @param nombreCampo nombre del campo
     * @param numeroFila número de fila del Excel
     */
    private void validarTexto(
            String valor,
            String nombreCampo,
            int numeroFila) {

        if (valor == null ||
                valor.isBlank()) {

            throw new IllegalArgumentException(
                    "El campo '"
                            + nombreCampo
                            + "' está vacío en la fila "
                            + numeroFila
            );
        }
    }

    // =========================================================
    // CONVERTIR ENTERO
    // =========================================================

    /**
     * Convierte un texto en un número entero.
     *
     * @param valor texto obtenido del Excel
     * @param nombreCampo nombre del campo
     * @param numeroFila número de fila
     * @return valor entero
     */
    private int convertirEntero(
            String valor,
            String nombreCampo,
            int numeroFila) {

        try {

            return Integer.parseInt(
                    valor.trim()
            );

        } catch (NumberFormatException e) {

            throw new IllegalArgumentException(
                    "El campo '"
                            + nombreCampo
                            + "' de la fila "
                            + numeroFila
                            + " debe contener un número entero."
            );
        }
    }

    // =========================================================
    // FILA VACÍA
    // =========================================================

    /**
     * Determina si una fila de Excel está completamente vacía.
     *
     * @param row fila que será evaluada
     * @return true si la fila está vacía
     */
    private boolean filaVacia(Row row) {

        for (int i = 0; i < 9; i++) {

            if (!obtenerTexto(row, i).isBlank()) {
                return false;
            }
        }

        return true;
    }

    // =========================================================
    // CANCELAR
    // =========================================================

    /**
     * Cancela la operación y cierra la ventana.
     *
     * @param event evento generado al presionar Cancelar
     */
    @FXML
    private void cancelar(ActionEvent event) {

        cerrarVentana();
    }

    // =========================================================
    // CERRAR
    // =========================================================

    /**
     * Cierra la ventana modal actual.
     */
    private void cerrarVentana() {

        if (btnCancelar != null
                && btnCancelar.getScene() != null
                && btnCancelar.getScene().getWindow() != null) {

            Stage stage =
                    (Stage) btnCancelar
                            .getScene()
                            .getWindow();

            stage.close();
        }
    }

    // =========================================================
    // ALERTA
    // =========================================================

    /**
     * Muestra una alerta al usuario.
     *
     * @param titulo título de la alerta
     * @param mensaje mensaje mostrado
     * @param tipo tipo de alerta
     */
    private void mostrarAlerta(
            String titulo,
            String mensaje,
            Alert.AlertType tipo) {

        Alert alert =
                new Alert(tipo);

        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        alert.showAndWait();
    }
}