package dev.laurafelipe.proyectointegrador3.view;

import dev.laurafelipe.proyectointegrador3.entity.Attendee;
import dev.laurafelipe.proyectointegrador3.entity.Event;
import dev.laurafelipe.proyectointegrador3.service.RegistrationService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Builder;
import javafx.util.StringConverter;

public class NewRegistrationFormView implements Builder<Region> {
    private final RegistrationService service;

    private final ObjectProperty<Event> selectedEvent = new SimpleObjectProperty<>();
    private final ObjectProperty<Attendee> selectedAttendee = new SimpleObjectProperty<>();

    private final StringProperty availableSeats = new SimpleStringProperty("");
    private final StringProperty errorMessage = new SimpleStringProperty("");

    public NewRegistrationFormView(RegistrationService service) {
        this.service = service;

        // Muestra los cupos disponibles cuando se elige un evento
        selectedEvent.addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                availableSeats.setValue("");
            } else {
                availableSeats.setValue(
                        "Cupos disponibles: " + service.getAvailableSeats(newValue)
                );
            }
        });
    }

    @Override
    public Region build() {
        Node eventField = createEventField();
        Node availableSeatsLabel = createAvailableSeatsLabel();
        Node attendeeField = createAttendeeField();
        Node errorMessageLabel = createErrorMessageLabel();
        Node submitButton = createSubmitButton();

        VBox form = new VBox(
                new Label("Evento: "),
                eventField,
                availableSeatsLabel,
                new Label("Asistente: "),
                attendeeField,
                errorMessageLabel,
                submitButton
        );

        form.setSpacing(8);
        form.setPadding(new Insets(8));

        return form;
    }

    private Node createEventField() {
        // Solo eventos futuros (el servicio igual valida la regla)
        ComboBox<Event> eventField = new ComboBox<>(
                FXCollections.observableArrayList(service.getUpcomingEvents())
        );

        eventField.setConverter(new StringConverter<>() {
            @Override
            public String toString(Event event) {
                return event == null ? "" : event.name + " (" + event.date + ")";
            }

            @Override
            public Event fromString(String string) {
                return null;
            }
        });

        eventField.valueProperty().bindBidirectional(selectedEvent);

        return eventField;
    }

    private Node createAvailableSeatsLabel() {
        Label availableSeatsLabel = new Label("");
        availableSeatsLabel.textProperty().bind(availableSeats);
        return availableSeatsLabel;
    }

    private Node createAttendeeField() {
        ComboBox<Attendee> attendeeField = new ComboBox<>(
                FXCollections.observableArrayList(service.getAttendees())
        );

        attendeeField.setConverter(new StringConverter<>() {
            @Override
            public String toString(Attendee attendee) {
                return attendee == null
                        ? ""
                        : attendee.name + " - " + attendee.documentNumber;
            }

            @Override
            public Attendee fromString(String string) {
                return null;
            }
        });

        attendeeField.valueProperty().bindBidirectional(selectedAttendee);

        return attendeeField;
    }

    private Node createErrorMessageLabel() {
        Label errorMessageLabel = new Label("");
        errorMessageLabel.textProperty().bindBidirectional(errorMessage);
        return errorMessageLabel;
    }

    private Node createSubmitButton() {
        Button submitButton = new Button("Inscribir");

        submitButton.setOnAction(event -> {
            try {
                service.createRegistration(
                        selectedEvent.getValue(),
                        selectedAttendee.getValue()
                );

                service.findAllRegistrations();

                closeWindowFrom(event);

            } catch (IllegalArgumentException | IllegalStateException e) {
                errorMessage.setValue(e.getMessage());

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("No se puede inscribir");
                alert.setHeaderText("No se puede registrar la inscripcion");
                alert.setContentText(e.getMessage());
                alert.showAndWait();

            } catch (Exception e) {
                errorMessage.setValue("Error al registrar la inscripcion");

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("No se pudo registrar la inscripcion");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        });

        return submitButton;
    }

    private void closeWindowFrom(ActionEvent event) {
        ((Node) (event.getSource())).getScene().getWindow().hide();
    }
}
