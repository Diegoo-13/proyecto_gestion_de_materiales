package utng.gtid2.dab;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import utng.gtid2.dab.dao.UsuarioDAO;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    // Indica si Materiales Registrados debe abrirse
    // mostrando únicamente materiales con stock bajo.
    private static boolean filtrarStockBajo = false;

    @Override
    public void start(Stage stage) throws IOException {

        UsuarioDAO usuarioDAO = new UsuarioDAO();

        String pantallaInicial;

        if (usuarioDAO.existenUsuarios()) {
            pantallaInicial = "login/Login";
        } else {
            pantallaInicial = "login/ConfiguracionInicial";
        }

        scene = new Scene(
                loadFXML(pantallaInicial),
                1366,
                768
        );

        stage.setTitle(
                "Sistema de Gestión y Control de Materiales"
        );

        stage.setScene(scene);

        // Abrir la aplicación maximizada
        stage.setMaximized(true);

        // Tamaño mínimo recomendado para evitar deformaciones
        stage.setMinWidth(1366);
        stage.setMinHeight(768);

        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {

        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {

        FXMLLoader fxmlLoader =
                new FXMLLoader(
                        App.class.getResource(
                                fxml + ".fxml"
                        )
                );

        return fxmlLoader.load();
    }

    /**
     * Indica que la pantalla de materiales registrados
     * debe abrirse aplicando el filtro de stock bajo.
     */
    public static void solicitarFiltroStockBajo() {

        filtrarStockBajo = true;
    }

    /**
     * Obtiene y consume la solicitud de filtro de stock bajo.
     *
     * Una vez obtenida la solicitud, se restablece su valor
     * para evitar que el filtro se aplique nuevamente
     * al abrir la pantalla desde otro lugar.
     *
     * @return true si se solicitó mostrar únicamente
     *         materiales con stock bajo.
     */
    public static boolean consumirFiltroStockBajo() {

        boolean aplicarFiltro = filtrarStockBajo;

        filtrarStockBajo = false;

        return aplicarFiltro;
    }

    public static void main(String[] args) {

        launch();
    }
}