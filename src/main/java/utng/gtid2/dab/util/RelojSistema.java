package utng.gtid2.dab.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class RelojSistema {

    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void iniciar(Label lblHora, Label lblFecha) {

        Timeline reloj = new Timeline(

                new KeyFrame(Duration.seconds(0), e -> {

                    lblHora.setText(
                            LocalTime.now().format(FORMATO_HORA));

                    lblFecha.setText(
                            LocalDate.now().format(FORMATO_FECHA));

                }),

                new KeyFrame(Duration.seconds(1))

        );

        reloj.setCycleCount(Timeline.INDEFINITE);

        reloj.play();

    }

}