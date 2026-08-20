package utng.gtid2.dab.util;

import javafx.scene.control.Alert;
import utng.gtid2.dab.modelo.Usuario;

/**
 * Clase utilitaria encargada de controlar los permisos
 * de acceso a los diferentes módulos del sistema.
 *
 * <p>
 * Los permisos se determinan utilizando el rol del usuario
 * que actualmente tiene la sesión iniciada.
 * </p>
 */
public class Permisos {

    /**
     * Constructor privado para evitar crear objetos
     * de esta clase utilitaria.
     */
    private Permisos() {
    }

    /**
     * Comprueba si el usuario actual tiene rol de administrador.
     *
     * @return true si existe un usuario en sesión y su rol
     *         es Administrador; false en cualquier otro caso.
     */
    public static boolean esAdministrador() {

        Usuario usuario = Sesion.getUsuarioActual();

        return usuario != null
                && usuario.getRol() != null
                && usuario.getRol().equalsIgnoreCase("Administrador");
    }

    /**
     * Muestra un mensaje indicando que el módulo solicitado
     * requiere permisos de administrador.
     */
    public static void mostrarAccesoDenegado() {

        Alert alerta = new Alert(Alert.AlertType.WARNING);

        alerta.setTitle("Acceso restringido");
        alerta.setHeaderText("Acceso no permitido");

        alerta.setContentText(
                "Este módulo está disponible únicamente "
                + "para administradores."
        );

        alerta.showAndWait();
    }
}