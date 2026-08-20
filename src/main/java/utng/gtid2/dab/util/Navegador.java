package utng.gtid2.dab.util;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.stage.Modality;
import javafx.stage.Stage;

import utng.gtid2.dab.App;

public class Navegador {

    /**
     * Abre una ventana modal.
     *
     * @param fxml ruta del archivo FXML
     * @param titulo título de la ventana
     * @throws IOException si ocurre un error al cargar el FXML
     */
    public static void abrirModal(String fxml, String titulo) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                App.class.getResource(fxml + ".fxml"));

        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle(titulo);
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.showAndWait();
    }

    /**
     * Cierra la ventana donde está un botón o cualquier control.
     *
     * @param control control que pertenece a la ventana que se desea cerrar
     */
    public static void cerrar(Control control) {

        Stage stage = (Stage) control.getScene().getWindow();
        stage.close();
    }

    /**
     * Cambia de una ventana modal a otra.
     *
     * @param control control desde donde se realiza la navegación
     * @param fxml ruta del archivo FXML
     * @param titulo título de la ventana
     * @throws IOException si ocurre un error al cargar el FXML
     */
    public static void cambiarModal(
            Control control,
            String fxml,
            String titulo) throws IOException {

        Stage stageActual = (Stage) control.getScene().getWindow();
        stageActual.close();

        Platform.runLater(() -> {

            try {

                FXMLLoader loader = new FXMLLoader(
                        App.class.getResource(fxml + ".fxml"));

                Parent root = loader.load();

                Stage nuevaVentana = new Stage();

                nuevaVentana.setTitle(titulo);
                nuevaVentana.setScene(new Scene(root));
                nuevaVentana.initModality(Modality.APPLICATION_MODAL);
                nuevaVentana.setResizable(false);
                nuevaVentana.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Verifica si el usuario actual tiene permisos de administrador.
     *
     * @return true si el usuario tiene rol de administrador;
     *         false en caso contrario
     */
    public static boolean esAdministrador() {

        if (Sesion.getUsuarioActual() == null) {
            return false;
        }

        String rol = Sesion.getUsuarioActual().getRol();

        return rol != null && rol.equalsIgnoreCase("Administrador");
    }

    /**
     * Verifica si el usuario es administrador y muestra
     * un mensaje cuando intenta acceder a un módulo restringido.
     *
     * @return true si tiene permiso; false si no tiene permiso
     */
    public static boolean verificarAdministrador() {

        if (esAdministrador()) {
            return true;
        }

        javafx.scene.control.Alert alerta =
                new javafx.scene.control.Alert(
                        javafx.scene.control.Alert.AlertType.WARNING);

        alerta.setTitle("Acceso restringido");
        alerta.setHeaderText("Acceso no permitido");
        alerta.setContentText(
                "Este módulo está disponible únicamente para administradores.");

        alerta.showAndWait();

        return false;
    }

    /**
     * Cambia a una ventana modal verificando previamente
     * si el usuario tiene permisos de administrador.
     *
     * @param control control desde donde se realiza la navegación
     * @param fxml ruta del archivo FXML
     * @param titulo título de la ventana
     * @return true si se realizó la navegación; false si fue denegada
     * @throws IOException si ocurre un error al cargar el FXML
     */
    public static boolean cambiarModalAdministrador(
            Control control,
            String fxml,
            String titulo) throws IOException {

        if (!verificarAdministrador()) {
            return false;
        }

        cambiarModal(control, fxml, titulo);

        return true;
    }
}