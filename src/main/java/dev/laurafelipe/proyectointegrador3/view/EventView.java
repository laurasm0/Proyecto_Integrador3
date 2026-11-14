package dev.laurafelipe.proyectointegrador3.view;

import dev.laurafelipe.proyectointegrador3.entity.Event;
import dev.laurafelipe.proyectointegrador3.model.EventModel;
import dev.laurafelipe.proyectointegrador3.service.EventService;
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

public class EventView implements Builder<Region> {
    private static final String ALL_CATEGORIES = "Todas las categorias";

    private final EventModel model;
    private final EventService service;
    private FilteredList<Event> filteredEvents;

    private final TextField searchField = new TextField();
    private final ComboBox<String> categoryFilter = new ComboBox<>();

    public EventView(EventModel model, EventService service) {
        this.model = model;
        this.service = service;
    }

    @Override
    public Region build() {
        Node newEventButton = createNewEventButton();
        Node table = createEventsTable();
        Node searchNode = createSearchField();
        Node categoryNode = createCategoryFilter();
        Node deleteEventButton = createDeleteEventButton();
        Node updateEventButton = createUpdateEventButton();

        VBox.setVgrow(table, Priority.ALWAYS);

        HBox navigationBarHBox = new HBox(
                newEventButton,
                deleteEventButton,
                updateEventButton,
                searchNode,
                categoryNode
        );

        navigationBarHBox.setPadding(new Insets(8));
        navigationBarHBox.setSpacing(8);

        return new VBox(
                navigationBarHBox,
                table
        );
    }

    private Node createSearchField() {
        searchField.setPromptText("Buscar por nombre...");

        searchField.textProperty().addListener(
                (observable, oldValue, newValue) -> applyFilters()
        );

        return searchField;
    }

    private Node createCategoryFilter() {
        categoryFilter.getItems().add(ALL_CATEGORIES);
        categoryFilter.getItems().addAll(Event.CATEGORIES);
        categoryFilter.setValue(ALL_CATEGORIES);

        categoryFilter.valueProperty().addListener(
                (observable, oldValue, newValue) -> applyFilters()
        );

        return categoryFilter;
    }

    // Combina el texto de busqueda con la categoria seleccionada
    private void applyFilters() {
        String search = searchField.getText() == null
                ? ""
                : searchField.getText().trim().toLowerCase();
        String category = categoryFilter.getValue();

        filteredEvents.setPredicate(event -> {
            boolean matchesSearch = search.isEmpty()
                    || event.name.toLowerCase().contains(search);

            boolean matchesCategory = category == null
                    || category.equals(ALL_CATEGORIES)
                    || category.equals(event.category);

            return matchesSearch && matchesCategory;
        });
    }

    private Node createUpdateEventButton() {
        Button button = new Button("Actualizar");

        button.disableProperty().bind(
                model.selectedEvent.isNull()
        );

        button.setOnAction(event -> {
            Event selectedEvent = model.selectedEvent.get();

            // Refresca la lista de lugares por si se creo o cambio alguno
            service.findAllEvents();

            Stage updateEventModal = createUpdateEventModal(selectedEvent);
            updateEventModal.showAndWait();
        });

        return button;
    }

    private Stage createUpdateEventModal(Event eventToUpdate) {
        Stage modal = new Stage();

        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Actualizar evento");

        UpdateEventFormView updateEventForm =
                new UpdateEventFormView(eventToUpdate, service);

        modal.setScene(new Scene(updateEventForm.build()));

        return modal;
    }

    private Node createDeleteEventButton() {
        Button button = new Button("Eliminar");

        button.disableProperty().bind(
                model.selectedEvent.isNull()
        );

        button.setOnAction(event -> {
            try {
                String eventId = model.selectedEvent.get().eventId;
                service.removeEventById(eventId);
                service.findAllEvents();

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Evento eliminado");
                successAlert.setHeaderText("Evento eliminado exitosamente");
                successAlert.showAndWait();

            } catch (Exception e) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("No se pudo eliminar el evento");
                errorAlert.setContentText(e.getMessage());
                errorAlert.showAndWait();
            }
        });

        return button;
    }

    private Node createNewEventButton() {
        Button button = new Button("Registrar evento");

        button.setOnAction(event -> {
            // Refresca la lista de lugares por si se creo alguno nuevo
            service.findAllEvents();

            Stage newEventModal = createNewEventModal();
            newEventModal.showAndWait();
        });

        return button;
    }

    private Stage createNewEventModal() {
        Stage modal = new Stage();

        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Registrar evento");

        NewEventFormView newEventFormView =
                new NewEventFormView(service);

        modal.setScene(new Scene(newEventFormView.build()));

        return modal;
    }

    private Node createEventsTable() {
        TableView<Event> tableView = new TableView<>();

        model.selectedEvent.bind(
                tableView.getSelectionModel().selectedItemProperty()
        );

        TableColumn<Event, String> nameColumn = new TableColumn<>("Nombre");
        nameColumn.setCellValueFactory(
                p -> new ReadOnlyStringWrapper(p.getValue().name)
        );
        tableView.getColumns().add(nameColumn);

        TableColumn<Event, String> categoryColumn = new TableColumn<>("Categoria");
        categoryColumn.setCellValueFactory(
                p -> new ReadOnlyStringWrapper(p.getValue().category)
        );
        tableView.getColumns().add(categoryColumn);

        TableColumn<Event, String> dateColumn = new TableColumn<>("Fecha");
        dateColumn.setCellValueFactory(
                p -> new ReadOnlyStringWrapper(p.getValue().date)
        );
        tableView.getColumns().add(dateColumn);

        TableColumn<Event, String> scheduleColumn = new TableColumn<>("Horario");
        scheduleColumn.setCellValueFactory(
                p -> new ReadOnlyStringWrapper(
                        p.getValue().startTime + " - " + p.getValue().endTime
                )
        );
        tableView.getColumns().add(scheduleColumn);

        TableColumn<Event, String> venueColumn = new TableColumn<>("Lugar");
        venueColumn.setCellValueFactory(
                p -> new ReadOnlyStringWrapper(service.getVenueName(p.getValue().venueId))
        );
        tableView.getColumns().add(venueColumn);

        TableColumn<Event, String> capacityColumn = new TableColumn<>("Capacidad");
        capacityColumn.setCellValueFactory(
                p -> new ReadOnlyStringWrapper(String.valueOf(p.getValue().capacity))
        );
        tableView.getColumns().add(capacityColumn);

        filteredEvents = new FilteredList<>(model.events, event -> true);
        tableView.setItems(filteredEvents);
        tableView.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS
        );

        return tableView;
    }

}
