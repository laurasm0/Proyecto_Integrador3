
package dev.laurafelipe.proyectointegrador3;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import dev.laurafelipe.proyectointegrador3.controller.AttendeeController;
import dev.laurafelipe.proyectointegrador3.controller.EventController;
import dev.laurafelipe.proyectointegrador3.controller.RegistrationController;
import dev.laurafelipe.proyectointegrador3.controller.VenueController;

import dev.laurafelipe.proyectointegrador3.repository.AttendeeRepository;
import dev.laurafelipe.proyectointegrador3.repository.EventRepository;
import dev.laurafelipe.proyectointegrador3.repository.RegistrationRepository;
import dev.laurafelipe.proyectointegrador3.repository.VenueRepository;

import dev.laurafelipe.proyectointegrador3.view.StylesView;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class FXApplication extends Application {

    public static final String MONGODB_URI =
            "mongodb://127.0.0.1:27017";

    private MongoClient mongoClient;

    public static void main(String[] arg) {
        launch(arg);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            mongoClient = MongoClients.create(MONGODB_URI);

            MongoDatabase database =
                    mongoClient.getDatabase("integrativeprojectdb");

            AttendeeRepository attendeeRepository =
                    new AttendeeRepository(database);

            VenueRepository venueRepository =
                    new VenueRepository(database);

            EventRepository eventRepository =
                    new EventRepository(database);

            RegistrationRepository registrationRepository =
                    new RegistrationRepository(database);

            AttendeeController attendeeController =
                    new AttendeeController(attendeeRepository);

            VenueController venueController =
                    new VenueController(
                            venueRepository,
                            eventRepository
                    );

            EventController eventController =
                    new EventController(
                            eventRepository,
                            venueRepository,
                            registrationRepository
                    );

            RegistrationController registrationController =
                    new RegistrationController(
                            registrationRepository,
                            eventRepository,
                            attendeeRepository
                    );

            Region eventsView = eventController.getView();
            Region venuesView = venueController.getView();
            Region attendeesView = attendeeController.getView();
            Region registrationsView =
                    registrationController.getView();

            BorderPane mainLayout = new BorderPane();
            mainLayout.getStyleClass().add("app-shell");

            /* BARRA LATERAL */

            VBox sidebar = new VBox(12);
            sidebar.getStyleClass().add("sidebar");
            sidebar.setPrefWidth(235);
            sidebar.setMinWidth(210);
            sidebar.setPadding(new Insets(22, 14, 20, 14));

            VBox brand = new VBox(5);
            brand.getStyleClass().add("sidebar-brand");

            Label brandMark = new Label("USTA");
            brandMark.getStyleClass().add("brand-mark");

            Label brandTitle = new Label("EVENTOS USTA");
            brandTitle.getStyleClass().add("sidebar-title");

            Label brandSubtitle =
                    new Label("GESTIÓN UNIVERSITARIA");
            brandSubtitle.getStyleClass().add("sidebar-subtitle");

            brand.getChildren().addAll(
                    brandMark,
                    brandTitle,
                    brandSubtitle
            );

            Label menuTitle = new Label("MENÚ PRINCIPAL");
            menuTitle.getStyleClass().add("menu-caption");

            VBox navigation = new VBox(8);
            navigation.getStyleClass().add("sidebar-navigation");

            /* CONTENIDO PRINCIPAL */

            BorderPane contentLayout = new BorderPane();
            contentLayout.getStyleClass().add("main-content");

            Label pageTitle = new Label("Eventos");
            pageTitle.getStyleClass().add("page-title");

            Label pageSubtitle = new Label(
                    "Administra las actividades académicas y universitarias"
            );
            pageSubtitle.getStyleClass().add("page-subtitle");

            VBox pageHeading = new VBox(
                    4,
                    pageTitle,
                    pageSubtitle
            );

            HBox topBar = new HBox(pageHeading);
            topBar.getStyleClass().add("top-bar");
            topBar.setAlignment(Pos.CENTER_LEFT);

            contentLayout.setTop(topBar);
            contentLayout.setCenter(eventsView);

            /* BOTONES DEL MENÚ */

            List<Button> navigationButtons = new ArrayList<>();

            Button eventsButton = createNavigationButton(
                    "Eventos", "▦", navigation, navigationButtons
            );

            Button venuesButton = createNavigationButton(
                    "Lugares", "⌖", navigation, navigationButtons
            );

            Button attendeesButton = createNavigationButton(
                    "Asistentes", "♙", navigation, navigationButtons
            );

            Button registrationsButton = createNavigationButton(
                    "Inscripciones", "☷", navigation, navigationButtons
            );

            eventsButton.setOnAction(event -> showSection(
                    eventsButton,
                    eventsView,
                    "Eventos",
                    "Administra las actividades académicas y universitarias",
                    eventController::refresh,
                    contentLayout,
                    pageTitle,
                    pageSubtitle,
                    navigationButtons
            ));

            venuesButton.setOnAction(event -> showSection(
                    venuesButton,
                    venuesView,
                    "Lugares",
                    "Consulta y administra los espacios disponibles",
                    venueController::refresh,
                    contentLayout,
                    pageTitle,
                    pageSubtitle,
                    navigationButtons
            ));

            attendeesButton.setOnAction(event -> showSection(
                    attendeesButton,
                    attendeesView,
                    "Asistentes",
                    "Registra y administra los asistentes",
                    () -> { },
                    contentLayout,
                    pageTitle,
                    pageSubtitle,
                    navigationButtons
            ));

            registrationsButton.setOnAction(event -> showSection(
                    registrationsButton,
                    registrationsView,
                    "Inscripciones",
                    "Gestiona las inscripciones a los eventos",
                    registrationController::refresh,
                    contentLayout,
                    pageTitle,
                    pageSubtitle,
                    navigationButtons
            ));

            Label sidebarFooter =
                    new Label("Proyecto Integrador · USTA");
            sidebarFooter.getStyleClass().add("sidebar-footer");

            VBox spacer = new VBox();
            VBox.setVgrow(spacer, Priority.ALWAYS);

            sidebar.getChildren().addAll(
                    brand,
                    menuTitle,
                    navigation,
                    spacer,
                    sidebarFooter
            );

            mainLayout.setLeft(sidebar);
            mainLayout.setCenter(contentLayout);

            eventsButton.getStyleClass().add("nav-button-active");

            /* ESCENA PRINCIPAL */

            Scene scene = new Scene(mainLayout, 1180, 720);

            StylesView.apply(scene);

            primaryStage.setTitle(
                    "Eventos USTA | Sistema de gestión"
            );

            primaryStage.setMinWidth(950);
            primaryStage.setMinHeight(620);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error fatal");
            alert.setHeaderText(
                    "Conexión fallida a la base de datos"
            );
            alert.setContentText(e.toString());

            alert.showAndWait();

            throw e;
        }
    }

    private Button createNavigationButton(
            String title,
            String symbol,
            VBox navigation,
            List<Button> buttons
    ) {
        Label icon = new Label(symbol);
        icon.getStyleClass().add("nav-icon");

        Label text = new Label(title);
        text.getStyleClass().add("nav-text");

        HBox content = new HBox(13, icon, text);
        content.setAlignment(Pos.CENTER_LEFT);

        Button button = new Button();
        button.setGraphic(content);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.getStyleClass().add("nav-button");

        navigation.getChildren().add(button);
        buttons.add(button);

        return button;
    }

    private void showSection(
            Button selectedButton,
            Region view,
            String title,
            String subtitle,
            Runnable refresh,
            BorderPane contentLayout,
            Label pageTitle,
            Label pageSubtitle,
            List<Button> navigationButtons
    ) {
        for (Button button : navigationButtons) {
            button.getStyleClass().remove("nav-button-active");
        }

        selectedButton.getStyleClass().add("nav-button-active");

        pageTitle.setText(title);
        pageSubtitle.setText(subtitle);

        contentLayout.setCenter(view);

        refresh.run();
    }

    @Override
    public void stop() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
