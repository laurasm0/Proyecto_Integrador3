
package dev.laurafelipe.proyectointegrador3.view;

import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

import java.net.URL;

public class StylesView {

    private StylesView() {
    }

    public static void apply(Scene scene) {

        URL css = StylesView.class.getResource("/styles.css");

        if (css == null) {
            throw new IllegalStateException(
                    "No se encontro styles.css. " +
                            "Debe estar en src/main/resources/styles.css"
            );
        }

        scene.getStylesheets().clear();
        scene.getStylesheets().add(css.toExternalForm());

        scene.getRoot().getStyleClass().add("styled-scene");
    }

    public static void applyModal(Scene scene) {

        apply(scene);

        Parent originalContent = scene.getRoot();

        if (!originalContent.getStyleClass().contains("form-container")) {
            originalContent.getStyleClass().add("form-container");
        }

        ScrollPane scrollPane = new ScrollPane(originalContent);

        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );
        scrollPane.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scrollPane.setPannable(true);
        scrollPane.getStyleClass().add("form-scroll");

        scene.setRoot(scrollPane);
    }

    public static void configureModal(Stage modal) {

        modal.setWidth(600);
        modal.setHeight(680);
        modal.setMinWidth(480);
        modal.setMinHeight(400);
        modal.setResizable(true);
        modal.centerOnScreen();
    }
}
