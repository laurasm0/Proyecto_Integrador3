package dev.laurafelipe.proyectointegrador3.view;

import dev.laurafelipe.proyectointegrador3.entity.Venue;
import dev.laurafelipe.proyectointegrador3.service.VenueService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Builder;

public class UpdateVenueFormView implements Builder<Region> {
    private final VenueService service;
    private final Venue venueToUpdate;

    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty type = new SimpleStringProperty("");
    private final StringProperty capacity = new SimpleStringProperty("");

    private final StringProperty errorMessage = new SimpleStringProperty("");

    public UpdateVenueFormView(Venue venueToUpdate, VenueService service) {
        this.service = service;
        this.venueToUpdate = venueToUpdate;

        name.setValue(venueToUpdate.name);
        type.setValue(venueToUpdate.type);
        capacity.setValue(String.valueOf(venueToUpdate.capacity));
    }

    @Override
    public Region build() {
        Node nameField = createNameField();
        Node typeField = createTypeField();
        Node capacityField = createCapacityField();
        Node errorMessageLabel = createErrorMessageLabel();
        Node submitButton = createSubmitButton();

        VBox form = new VBox(
                new Label("Nombre: "),
                nameField,
                new Label("Tipo: "),
                typeField,
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

    private Node createTypeField() {
        ComboBox<String> typeField = new ComboBox<>();
        typeField.setEditable(true);
        typeField.getItems().addAll(
                "Auditorio", "Aula", "Laboratorio", "Coliseo", "Plaza", "Cancha"
        );
        typeField.getEditor().textProperty().bindBidirectional(type);
        return typeField;
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

        submitButton.setOnAction(event -> {
            // Se arma una copia: el objeto de la tabla solo cambia si el servicio acepta
            Venue updatedVenue = new Venue();

            updatedVenue.venueId = venueToUpdate.venueId;
            updatedVenue.name = name.getValue();
            updatedVenue.type = type.getValue();
            updatedVenue.capacity = capacity.getValue().isBlank()
                    ? 0
                    : Integer.parseInt(capacity.getValue());

            try {
                service.updateVenue(updatedVenue);
                service.findAllVenues();
                closeWindowFrom(event);

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
