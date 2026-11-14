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
import javafx.scene.control.Alert;
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

public class NewEventFormView implements Builder<Region> {
    private final EventService service;

    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty description = new SimpleStringProperty("");
    private final ObjectProperty<String> category = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private final StringProperty startTime = new SimpleStringProperty("");
    private final StringProperty endTime = new SimpleStringProperty("");
    private final ObjectProperty<Venue> venue = new SimpleObjectProperty<>();
    private final StringProperty capacity = new SimpleStringProperty("");

    private final StringProperty errorMessage = new SimpleStringProperty("");

    public NewEventFormView(EventService service) {
        this.service = service;
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
        Button submitButton = new Button("Registrar");

        submitButton.setOnAction(actionEvent -> {
            Event newEvent = new Event();

            newEvent.name = name.getValue();
            newEvent.description = description.getValue();
            newEvent.category = category.getValue();
            newEvent.date = date.getValue() == null ? null : date.getValue().toString();
            newEvent.startTime = startTime.getValue();
            newEvent.endTime = endTime.getValue();
            newEvent.venueId = venue.getValue() == null ? null : venue.getValue().venueId;
            newEvent.capacity = capacity.getValue().isBlank()
                    ? 0
                    : Integer.parseInt(capacity.getValue());

            try {
                service.createEvent(newEvent);

                service.findAllEvents();

                closeWindowFrom(actionEvent);

            } catch (IllegalArgumentException | IllegalStateException e) {
                errorMessage.setValue(e.getMessage());

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Datos no validos");
                alert.setHeaderText("No se puede registrar el evento");
                alert.setContentText(e.getMessage());
                alert.showAndWait();

            } catch (Exception e) {
                errorMessage.setValue("Error al registrar el evento");

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("No se pudo registrar el evento");
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
