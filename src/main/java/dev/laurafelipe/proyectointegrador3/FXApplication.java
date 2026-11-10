package dev.laurafelipe.proyectointegrador3;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import dev.laurafelipe.proyectointegrador3.controller.AttendeeController;
import dev.laurafelipe.proyectointegrador3.repository.AttendeeRepository;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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

            AttendeeRepository repository = new AttendeeRepository(database);

            AttendeeController controller = new AttendeeController(repository);

            Scene scene = new Scene(controller.getView());

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

    @Override
    public void stop() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
