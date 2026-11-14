package dev.laurafelipe.proyectointegrador3.model;

import dev.laurafelipe.proyectointegrador3.entity.Event;
import dev.laurafelipe.proyectointegrador3.entity.Venue;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;

public class EventModel {
    public ListProperty<Event> events = new SimpleListProperty<>(
            FXCollections.observableArrayList()
    );

    // Se necesitan para mostrar el nombre del lugar y llenar el ComboBox
    public ListProperty<Venue> venues = new SimpleListProperty<>(
            FXCollections.observableArrayList()
    );

    public ObjectProperty<Event> selectedEvent = new SimpleObjectProperty<>();
}
