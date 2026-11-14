package dev.laurafelipe.proyectointegrador3.model;

import dev.laurafelipe.proyectointegrador3.entity.Attendee;
import dev.laurafelipe.proyectointegrador3.entity.Event;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;

public class RegistrationModel {
    public ListProperty<RegistrationRow> registrations = new SimpleListProperty<>(
            FXCollections.observableArrayList()
    );

    // Se necesitan para llenar los ComboBox del formulario
    public ListProperty<Event> events = new SimpleListProperty<>(
            FXCollections.observableArrayList()
    );

    public ListProperty<Attendee> attendees = new SimpleListProperty<>(
            FXCollections.observableArrayList()
    );

    public ObjectProperty<RegistrationRow> selectedRegistration = new SimpleObjectProperty<>();
}
