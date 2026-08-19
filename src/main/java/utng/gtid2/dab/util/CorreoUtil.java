package utng.gtid2.dab.util;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class CorreoUtil {

    private static final String CORREO_EMISOR =
            "sistemagestionmateriales.cgti@gmail.com";

    private static final String CONTRASENA_APLICACION =
            System.getenv("CORREO_PASSWORD");

    public static boolean enviarCodigo(String correoDestino, String codigo) {

        // Comprobar que la variable de entorno existe
        // sin mostrar nunca su contenido.
        System.out.println(
                "CORREO EMISOR: " + CORREO_EMISOR
        );

        System.out.println(
                "CORREO_PASSWORD configurada: "
                + (CONTRASENA_APLICACION != null
                && !CONTRASENA_APLICACION.isBlank())
        );

        // Si la variable no existe o está vacía,
        // evitar intentar conectarse a Gmail.
        if (CONTRASENA_APLICACION == null
                || CONTRASENA_APLICACION.isBlank()) {

            System.out.println(
                    "ERROR: No se encontró la variable de entorno CORREO_PASSWORD."
            );

            return false;
        }

        Properties propiedades = new Properties();

        propiedades.put(
                "mail.smtp.host",
                "smtp.gmail.com"
        );

        propiedades.put(
                "mail.smtp.port",
                "587"
        );

        propiedades.put(
                "mail.smtp.auth",
                "true"
        );

        propiedades.put(
                "mail.smtp.starttls.enable",
                "true"
        );

        Session sesion = Session.getInstance(
                propiedades,
                new Authenticator() {

                    @Override
                    protected PasswordAuthentication
                    getPasswordAuthentication() {

                        return new PasswordAuthentication(
                                CORREO_EMISOR,
                                CONTRASENA_APLICACION
                        );
                    }
                }
        );

        try {

            Message mensaje = new MimeMessage(sesion);

            mensaje.setFrom(
                    new InternetAddress(CORREO_EMISOR)
            );

            mensaje.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(correoDestino)
            );

            mensaje.setSubject(
                    "Código de recuperación - Sistema de Gestión de Materiales"
            );

            String contenido =
                    "Hola,\n\n"
                    + "Se solicitó recuperar la contraseña de tu cuenta "
                    + "en el Sistema de Gestión de Materiales.\n\n"
                    + "Tu código de verificación es:\n\n"
                    + codigo
                    + "\n\n"
                    + "Este código tiene una vigencia de 15 minutos.\n\n"
                    + "Si tú no solicitaste este cambio, puedes ignorar este correo.\n\n"
                    + "Sistema de Gestión de Materiales\n"
                    + "Área de Soporte Técnico";

            mensaje.setText(contenido);

            Transport.send(mensaje);

            return true;

        } catch (MessagingException e) {

            e.printStackTrace();

            return false;
        }
    }
}