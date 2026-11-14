package dev.laurafelipe.proyectointegrador3.view;

import dev.laurafelipe.proyectointegrador3.entity.Registration;
import dev.laurafelipe.proyectointegrador3.model.RegistrationModel;
import dev.laurafelipe.proyectointegrador3.model.RegistrationRow;
import dev.laurafelipe.proyectointegrador3.service.RegistrationService;
import javafx.beans.binding.Bindings;
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

public class RegistrationView implements Builder<Region> {
    private final RegistrationModel model;
    private final RegistrationService service;
    private FilteredList<RegistrationRow> filteredRegistrations;

    public RegistrationView(RegistrationModel model, RegistrationService service) {
        this.model = model;
        this.service = service;
    }

    @Override
    public Region build() {
        Node newRegistrationButton = createNewRegistrationButton();
        Node table = createRegistrationsTable();
        Node searchField = createSearchField();
        Node cancelRegistrationButton = createCancelRegistrationButton();

        VBox.setVgrow(table, Priority.ALWAYS);

        HBox navigationBarHBox = new HBox(
                newRegistrationButton,
                cancelRegistrationButton,
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
        searchField.setPromptText("Buscar por evento o asistente...");

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredRegistrations.setPredicate(row -> {
                if (newValue == null || newValue.isBlank()) {
                    return true;
                }

                String search = newValue.toLowerCase();

                return row.eventName().toLowerCase().contains(search)
                        || row.attendeeName().toLowerCase().contains(search);
            });
        });

        return searchField;
    }

    private Node createCancelRegistrationButton() {
        Button button = new Button("Cancelar inscripcion");

        // Solo se puede cancelar si hay una fila seleccionada y esta Activa
        button.disableProperty().bind(
                Bindings.createBooleanBinding(
                        () -> model.selectedRegistration.get() == null
                                || !Registration.ACTIVE.equals(
                                model.selectedRegistration.get().registration().status
                        ),
                        model.selectedRegistration
                )
        );

        button.setOnAction(event -> {
            try {
                Registration registration =
                        model.selectedRegistration.get().registration();

                service.cancelRegistration(registration);
                service.findAllRegistrations();

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Inscripcion cancelada");
                successAlert.setHeaderText("Inscripcion cancelada exitosamente");
                successAlert.showAndWait();

            } catch (Exception e) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("No se pudo cancelar la inscripcion");
                errorAlert.setContentText(e.getMessage());
                errorAlert.showAndWait();
            }
        });

        return button;
    }

    private Node createNewRegistrationButton() {
        Button button = new Button("Inscribir asistente");

        button.setOnAction(event -> {
            // Refresca eventos y asistentes por si hubo cambios en otras pestanas
            service.findAllRegistrations();

            Stage newRegistrationModal = createNewRegistrationModal();
            newRegistrationModal.showAndWait();
        });

        return button;
    }

    private Stage createNewRegistrationModal() {
        Stage modal = new Stage();

        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Inscribir asistente");

        NewRegistrationFormView newRegistrationFormView =
                new NewRegistrationFormView(service);

        modal.setScene(new Scene(newRegistrationFormView.build()));

        return modal;
    }

    private Node createRegistrationsTable() {
        TableView<RegistrationRow> tableView = new TableView<>();

        model.selectedRegistration.bind(
                tableView.getSelectionModel().selectedItemProperty()
        );

        TableColumn<RegistrationRow, String> eventColumn = new TableColumn<>("Evento");
        eventColumn.setCellValueFactory(
                p -> new ReadOnlyStringWrapper(p.getValue().eventName())
        );
        tableView.getColumns().add(eventColumn);

        TableColumn<RegistrationRow, String> attendeeColumn = new TableColumn<>("Asistente");
        attendeeColumn.setCellValueFactory(
                p -> new ReadOnlyStringWrapper(p.getValue().attendeeName())
        );
        tableView.getColumns().add(attendeeColumn);

        TableColumn<RegistrationRow, String> dateColumn =
                new TableColumn<>("Fecha de inscripcion");
        dateColumn.setCellValueFactory(
                p -> new ReadOnlyStringWrapper(p.getValue().registration().registrationDate)
        );
        tableView.getColumns().add(dateColumn);

        TableColumn<RegistrationRow, String> statusColumn = new TableColumn<>("Estado");
        statusColumn.setCellValueFactory(
                p -> new ReadOnlyStringWrapper(p.getValue().registration().status)
        );
        tableView.getColumns().add(statusColumn);

        filteredRegistrations = new FilteredList<>(model.registrations, row -> true);
        tableView.setItems(filteredRegistrations);
        tableView.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS
        );

        return tableView;
    }

}
