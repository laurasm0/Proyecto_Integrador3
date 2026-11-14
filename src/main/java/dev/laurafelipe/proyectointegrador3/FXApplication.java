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

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class FXApplication extends Application {

    public static final String MONGODB_URI = "mongodb://127.0.0.1:27017";

    private MongoClient mongoClient;

    public static void main(String[] arg) {
        launch(arg);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            mongoClient = MongoClients.create(MONGODB_URI);

            MongoDatabase database = mongoClient.getDatabase("integrativeprojectdb");

            AttendeeRepository attendeeRepository = new AttendeeRepository(database);
            VenueRepository venueRepository = new VenueRepository(database);
            EventRepository eventRepository = new EventRepository(database);
            RegistrationRepository registrationRepository = new RegistrationRepository(database);

            // AttendeeController queda exactamente como estaba
            AttendeeController attendeeController = new AttendeeController(attendeeRepository);

            VenueController venueController = new VenueController(
                    venueRepository,
                    eventRepository
            );

            EventController eventController = new EventController(
                    eventRepository,
                    venueRepository,
                    registrationRepository
            );

            RegistrationController registrationController = new RegistrationController(
                    registrationRepository,
                    eventRepository,
                    attendeeRepository
            );

            TabPane tabPane = new TabPane(
                    createTab("Eventos", eventController.getView(), eventController::refresh),
                    createTab("Lugares", venueController.getView(), venueController::refresh),
                    createTab("Asistentes", attendeeController.getView(), () -> { }),
                    createTab("Inscripciones", registrationController.getView(), registrationController::refresh)
            );

            tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

            Scene scene = new Scene(tabPane, 1000, 600);

            primaryStage.setTitle("Eventos USTA");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error fatal");
            alert.setHeaderText("Conexión fallida a la base de datos");
            alert.setContentText(e.toString());

            alert.showAndWait();

            throw e;
        }
    }

    // Cada vez que se selecciona una pestana se recargan sus datos
    private Tab createTab(String title, Region content, Runnable onSelected) {
        Tab tab = new Tab(title, content);

        tab.setOnSelectionChanged(event -> {
            if (tab.isSelected()) {
                onSelected.run();
            }
        });

        return tab;
    }

    @Override
    public void stop() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
