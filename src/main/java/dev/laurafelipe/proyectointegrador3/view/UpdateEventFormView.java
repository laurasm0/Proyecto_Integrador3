package dev.laurafelipe.proyectointegrador3.view;

import dev.laurafelipe.proyectointegrador3.entity.Event;
import dev.laurafelipe.proyectointegrador3.entity.Venue;
import dev.laurafelipe.proyectointegrador3.service.EventService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Builder;
import javafx.util.StringConverter;

import java.time.LocalDate;

public class UpdateEventFormView implements Builder<Region> {
    private final EventService service;
    private final Event eventToUpdate;

    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty description = new SimpleStringProperty("");
    private final ObjectProperty<String> category = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private final StringProperty startTime = new SimpleStringProperty("");
    private final StringProperty endTime = new SimpleStringProperty("");
    private final ObjectProperty<Venue> venue = new SimpleObjectProperty<>();
    private final StringProperty capacity = new SimpleStringProperty("");

    private final StringProperty errorMessage = new SimpleStringProperty("");

    public UpdateEventFormView(Event eventToUpdate, EventService service) {
        this.service = service;
        this.eventToUpdate = eventToUpdate;

        name.setValue(eventToUpdate.name);
        description.setValue(eventToUpdate.description);
        category.setValue(eventToUpdate.category);
        date.setValue(LocalDate.parse(eventToUpdate.date));
        startTime.setValue(eventToUpdate.startTime);
        endTime.setValue(eventToUpdate.endTime);
        capacity.setValue(String.valueOf(eventToUpdate.capacity));

        // Se busca el lugar actual del evento dentro de la lista de lugares
        for (Venue currentVenue : service.getVenues()) {
            if (currentVenue.venueId.equals(eventToUpdate.venueId)) {
                venue.setValue(currentVenue);
                break;
            }
        }
    }

    @Override
    public Region build() {
        Node nameField = createNameField();
        Node descriptionField = createDescriptionField();
        Node categoryField = createCategoryField();
        Node dateField = createDateField();
        Node startTimeField = createTimeField(startTime);
        Node endTimeField = createTimeField(endTime);
        Node venueField = createVenueField();
        Node capacityField = createCapacityField();
        Node errorMessageLabel = createErrorMessageLabel();
        Node submitButton = createSubmitButton();

        VBox form = new VBox(
                new Label("Nombre: "),
                nameField,
                new Label("Descripcion: "),
                descriptionField,
                new Label("Categoria: "),
                categoryField,
                new Label("Fecha: "),
                dateField,
                new Label("Hora de inicio (HH:mm): "),
                startTimeField,
                new Label("Hora de fin (HH:mm): "),
                endTimeField,
                new Label("Lugar: "),
                venueField,
                new Label("Capacidad: "),
                capacityField,
                errorMessageLabel,
                submitButton
        );

        form.setSpacing(8);
        form.setPadding(new Insets(8));

        return form;
    }

    private Node createNameField() {
        TextField nameField = new TextField();
        nameField.textProperty().bindBidirectional(name);
        return nameField;
    }

    private Node createDescriptionField() {
        TextArea descriptionField = new TextArea();
        descriptionField.setPrefRowCount(3);
        descriptionField.setWrapText(true);
        descriptionField.textProperty().bindBidirectional(description);
        return descriptionField;
    }

    private Node createCategoryField() {
        ComboBox<String> categoryField = new ComboBox<>(
                FXCollections.observableArrayList(Event.CATEGORIES)
        );
        categoryField.valueProperty().bindBidirectional(category);
        return categoryField;
    }

    private Node createDateField() {
        DatePicker dateField = new DatePicker();
        dateField.setEditable(false);
        dateField.valueProperty().bindBidirectional(date);
        return dateField;
    }

    private Node createTimeField(StringProperty timeProperty) {
        TextField timeField = new TextField();
        timeField.setPromptText("08:00");

        timeField.setTextFormatter(
                new TextFormatter<String>(change -> {
                    String text = change.getControlNewText();

                    if (text.matches("[0-9:]{0,5}")) {
                        return change;
                    }

                    return null;
                })
        );

        timeField.textProperty().bindBidirectional(timeProperty);

        return timeField;
    }

    private Node createVenueField() {
        ComboBox<Venue> venueField = new ComboBox<>(
                FXCollections.observableArrayList(service.getVenues())
        );

        venueField.setConverter(new StringConverter<>() {
            @Override
            public String toString(Venue venue) {
                return venue == null
                        ? ""
                        : venue.name + " (cap. " + venue.capacity + ")";
            }

            @Override
            public Venue fromString(String string) {
                return null;
            }
        });

        venueField.valueProperty().bindBidirectional(venue);

        return venueField;
    }

    private Node createCapacityField() {
        TextField capacityField = new TextField();

        capacityField.setTextFormatter(
                new TextFormatter<String>(change -> {
                    String text = change.getControlNewText();

                    if (text.matches("[0-9]{0,6}")) {
                        return change;
                    }

                    return null;
                })
        );

        capacityField.textProperty().bindBidirectional(capacity);

        return capacityField;
    }

    private Node createErrorMessageLabel() {
        Label errorMessageLabel = new Label("");
        errorMessageLabel.textProperty().bindBidirectional(errorMessage);
        return errorMessageLabel;
    }

    private Node createSubmitButton() {
        Button submitButton = new Button("Actualizar");

        submitButton.setOnAction(actionEvent -> {
            // Se arma una copia: el objeto de la tabla solo cambia si el servicio acepta
            Event updatedEvent = new Event();

            updatedEvent.eventId = eventToUpdate.eventId;
            updatedEvent.name = name.getValue();
            updatedEvent.description = description.getValue();
            updatedEvent.category = category.getValue();
            updatedEvent.date = date.getValue() == null ? null : date.getValue().toString();
            updatedEvent.startTime = startTime.getValue();
            updatedEvent.endTime = endTime.getValue();
            updatedEvent.venueId = venue.getValue() == null ? null : venue.getValue().venueId;
            updatedEvent.capacity = capacity.getValue().isBlank()
                    ? 0
                    : Integer.parseInt(capacity.getValue());

            try {
                service.updateEvent(updatedEvent);
                service.findAllEvents();
                closeWindowFrom(actionEvent);

            } catch (Exception e) {
                System.err.println(e);
                errorMessage.setValue(e.getMessage());
            }
        });

        return submitButton;
    }

    private void closeWindowFrom(ActionEvent event) {
        ((Node) (event.getSource())).getScene().getWindow().hide();
    }
}
