package dev.laurafelipe.proyectointegrador3.view;

import dev.laurafelipe.proyectointegrador3.entity.Venue;
import dev.laurafelipe.proyectointegrador3.model.VenueModel;
import dev.laurafelipe.proyectointegrador3.service.VenueService;
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

public class VenueView implements Builder<Region> {
    private final VenueModel model;
    private final VenueService service;
    private FilteredList<Venue> filteredVenues;

    public VenueView(VenueModel model, VenueService service) {
        this.model = model;
        this.service = service;
    }

    @Override
    public Region build() {
        Node newVenueButton = createNewVenueButton();
        Node table = createVenuesTable();
        Node searchField = createSearchField();
        Node deleteVenueButton = createDeleteVenueButton();
        Node updateVenueButton = createUpdateVenueButton();

        VBox.setVgrow(table, Priority.ALWAYS);

        HBox navigationBarHBox = new HBox(
                newVenueButton,
                deleteVenueButton,
                updateVenueButton,
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
        searchField.setPromptText("Buscar por nombre o tipo...");

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredVenues.setPredicate(venue -> {
                if (newValue == null || newValue.isBlank()) {
                    return true;
                }

                String search = newValue.toLowerCase();

                return venue.name.toLowerCase().contains(search)
                        || venue.type.toLowerCase().contains(search);
            });
        });

        return searchField;
    }

    private Node createUpdateVenueButton() {
        Button button = new Button("Actualizar");

        button.disableProperty().bind(
                model.selectedVenue.isNull()
        );

        button.setOnAction(event -> {
            Venue selectedVenue = model.selectedVenue.get();
            Stage updateVenueModal = createUpdateVenueModal(selectedVenue);
            updateVenueModal.showAndWait();
        });

        return button;
    }

    private Stage createUpdateVenueModal(Venue venueToUpdate) {
        Stage modal = new Stage();

        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Actualizar lugar");

        UpdateVenueFormView updateVenueForm =
                new UpdateVenueFormView(venueToUpdate, service);

        modal.setScene(new Scene(updateVenueForm.build()));

        return modal;
    }

    private Node createDeleteVenueButton() {
        Button button = new Button("Eliminar");

        button.disableProperty().bind(
                model.selectedVenue.isNull()
        );

        button.setOnAction(event -> {
            try {
                String venueId = model.selectedVenue.get().venueId;
                service.removeVenueById(venueId);
                service.findAllVenues();

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Lugar eliminado");
                successAlert.setHeaderText("Lugar eliminado exitosamente");
                successAlert.showAndWait();

            } catch (Exception e) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("No se pudo eliminar el lugar");
                errorAlert.setContentText(e.getMessage());
                errorAlert.showAndWait();
            }
        });

        return button;
    }

    private Node createNewVenueButton() {
        Button button = new Button("Registrar lugar");

        button.setOnAction(event -> {
            Stage newVenueModal = createNewVenueModal();
            newVenueModal.showAndWait();
        });

        return button;
    }

    private Stage createNewVenueModal() {
        Stage modal = new Stage();

        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Registrar lugar");

        NewVenueFormView newVenueFormView =
                new NewVenueFormView(service);

        modal.setScene(new Scene(newVenueFormView.build()));

        return modal;
    }

    private Node createVenuesTable() {
        TableView<Venue> tableView = new TableView<>();

        model.selectedVenue.bind(
                tableView.getSelectionModel().selectedItemProperty()
        );

        TableColumn<Venue, String> nameColumn = new TableColumn<>("Nombre");
        nameColumn.setCellValueFactory(
                p -> new ReadOnlyStringWrapper(p.getValue().name)
        );
        tableView.getColumns().add(nameColumn);

        TableColumn<Venue, String> typeColumn = new TableColumn<>("Tipo");
        typeColumn.setCellValueFactory(
                p -> new ReadOnlyStringWrapper(p.getValue().type)
        );
        tableView.getColumns().add(typeColumn);

        TableColumn<Venue, String> capacityColumn = new TableColumn<>("Capacidad");
        capacityColumn.setCellValueFactory(
                p -> new ReadOnlyStringWrapper(String.valueOf(p.getValue().capacity))
        );
        tableView.getColumns().add(capacityColumn);

        filteredVenues = new FilteredList<>(model.venues, venue -> true);
        tableView.setItems(filteredVenues);
        tableView.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS
        );

        return tableView;
    }

}
