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
     */
    public static void cerrar(Control control) {

        Stage stage = (Stage) control.getScene().getWindow();

        stage.close();

    }

    public static void cambiarModal(Control control, String fxml, String titulo) throws IOException {

        Stage stageActual = (Stage) control.getScene().getWindow();

        stageActual.close();

        Platform.runLater(() -> {
            try {

                FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
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

    
    
}