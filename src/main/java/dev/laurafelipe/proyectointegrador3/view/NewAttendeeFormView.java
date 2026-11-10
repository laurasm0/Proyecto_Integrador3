package dev.laurafelipe.proyectointegrador3.view;

import dev.laurafelipe.proyectointegrador3.entity.Attendee;
import dev.laurafelipe.proyectointegrador3.service.AttendeeService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Builder;

public class NewAttendeeFormView implements Builder<Region> {
    private final AttendeeService service;

    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty documentNumber = new SimpleStringProperty("");
    private final StringProperty email = new SimpleStringProperty("");

    private final StringProperty errorMessage = new SimpleStringProperty("");

    public NewAttendeeFormView(AttendeeService service) {
        this.service = service;
    }

    @Override
    public Region build() {
        Node nameField = createNameField();
        Node documentNumberField = createDocumentNumberField();
        Node emailField = createEmailField();
        Node errorMessageLabel = createErrorMessageLabel();
        Node submitButton = createSubmitButton();

        VBox form = new VBox(
                new Label("Nombre: "),
                nameField,
                new Label("Numero de documento: "),
                documentNumberField,
                new Label("Correo electronico: "),
                emailField,
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

    private Node createDocumentNumberField() {
        TextField documentNumberField = new TextField();
        documentNumberField.textProperty().bindBidirectional(documentNumber);
        return documentNumberField;
    }

    private Node createEmailField() {
        TextField emailField = new TextField();
        emailField.textProperty().bindBidirectional(email);
        return emailField;
    }

    private Node createErrorMessageLabel() {
        Label errorMessageLabel = new Label("");
        errorMessageLabel.textProperty().bindBidirectional(errorMessage);
        return errorMessageLabel;
    }

    private Node createSubmitButton() {
        Button submitButton = new Button("Registrar");

        submitButton.setOnAction(event -> {
            Attendee attendee = new Attendee();

            attendee.name = name.getValue();
            attendee.documentNumber = documentNumber.getValue();
            attendee.email = email.getValue();

            try {
                service.createAttendee(attendee);
                service.findAllAttendees();
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
