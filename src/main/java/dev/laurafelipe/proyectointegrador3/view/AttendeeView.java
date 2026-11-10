package dev.laurafelipe.proyectointegrador3.view;

import dev.laurafelipe.proyectointegrador3.entity.Attendee;
import dev.laurafelipe.proyectointegrador3.model.AttendeeModel;
import dev.laurafelipe.proyectointegrador3.service.AttendeeService;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Builder;

public class AttendeeView implements Builder<Region> {
    private final AttendeeModel model;
    private final AttendeeService service;
    private FilteredList<Attendee> filteredAttendees;

    public AttendeeView(AttendeeModel model, AttendeeService service) {
        this.model = model;
        this.service = service;
    }

    @Override
    public Region build() {
        Node newAttendeeButton = createNewAttendeeButton();
        Node table = createAttendeesTable();
        Node searchField = createSearchField();
        Node deleteAttendeeButton = createDeleteAttendeeButton();
        Node updateAttendeeButton = createUpdateAttendeeButton();

        VBox.setVgrow(table, Priority.ALWAYS);

        HBox navigationBarHBox = new HBox(
                newAttendeeButton,
                deleteAttendeeButton,
                updateAttendeeButton,
                searchField
        );

        navigationBarHBox.setPadding(new Insets(8));
        navigationBarHBox.setSpacing(8);

        return new VBox(
                navigationBarHBox,
                table
        );
    }

    private Node createSearchField() {
        TextField searchField = new TextField();
        searchField.setPromptText("Buscar por nombre o documento...");

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredAttendees.setPredicate(attendee -> {
                if (newValue == null || newValue.isBlank()) {
                    return true;
                }

                String search = newValue.toLowerCase();

                return attendee.name.toLowerCase().contains(search)
                        || attendee.documentNumber.toLowerCase().contains(search);
            });
        });

        return searchField;
    }

    private Node createUpdateAttendeeButton() {
        Button button = new Button("Actualizar");

        button.disableProperty().bind(
                model.selectedAttendee.isNull()
        );

        button.setOnAction(event -> {
            Attendee selectedAttendee = model.selectedAttendee.get();
            Stage updateAttendeeModal = createUpdateAttendeeModal(selectedAttendee);
            updateAttendeeModal.showAndWait();
        });

        return button;
    }

    private Stage createUpdateAttendeeModal(Attendee attendeeToUpdate) {
        Stage modal = new Stage();

        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Actualizar asistente");

        UpdateAttendeeFormView updateAttendeeForm =
                new UpdateAttendeeFormView(attendeeToUpdate, service);

        modal.setScene(new Scene(updateAttendeeForm.build()));

        return modal;
    }

    private Node createDeleteAttendeeButton() {
        Button button = new Button("Eliminar");

        button.disableProperty().bind(
                model.selectedAttendee.isNull()
        );

        button.setOnAction(event -> {
            try {
                String attendeeId = model.selectedAttendee.get().attendeeId;
                service.removeAttendeeById(attendeeId);
                service.findAllAttendees();

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Asistente eliminado");
                successAlert.setHeaderText("Asistente eliminado exitosamente");
                successAlert.showAndWait();

            } catch (Exception e) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("No se pudo eliminar el asistente");
                errorAlert.setContentText(e.getMessage());
                errorAlert.showAndWait();
            }
        });

        return button;
    }

    private Node createNewAttendeeButton() {
        Button button = new Button("Registrar asistente");

        button.setOnAction(event -> {
            Stage newAttendeeModal = createNewAttendeeModal();
            newAttendeeModal.showAndWait();
        });

        return button;
    }

    private Stage createNewAttendeeModal() {
        Stage modal = new Stage();

        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Registrar asistente");

        NewAttendeeFormView newAttendeeFormView =
                new NewAttendeeFormView(service);

        modal.setScene(new Scene(newAttendeeFormView.build()));

        return modal;
    }

    private Node createAttendeesTable() {
        TableView<Attendee> tableView = new TableView<>();

        model.selectedAttendee.bind(
                tableView.getSelectionModel().selectedItemProperty()
        );

        TableColumn<Attendee, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(
                p -> new ReadOnlyStringWrapper(p.getValue().attendeeId)
        );
        tableView.getColumns().add(idColumn);

        TableColumn<Attendee, String> nameColumn = new TableColumn<>("Nombre");
        nameColumn.setCellValueFactory(
                p -> new ReadOnlyStringWrapper(p.getValue().name)
        );
        tableView.getColumns().add(nameColumn);

        TableColumn<Attendee, String> documentColumn =
                new TableColumn<>("Numero de documento");
        documentColumn.setCellValueFactory(
                p -> new ReadOnlyStringWrapper(p.getValue().documentNumber)
        );
        tableView.getColumns().add(documentColumn);

        TableColumn<Attendee, String> emailColumn =
                new TableColumn<>("Correo electronico");
        emailColumn.setCellValueFactory(
                p -> new ReadOnlyStringWrapper(p.getValue().email)
        );
        tableView.getColumns().add(emailColumn);

        filteredAttendees = new FilteredList<>(model.attendees, attendee -> true);
        tableView.setItems(filteredAttendees);
        tableView.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS
        );

        return tableView;
    }

}
