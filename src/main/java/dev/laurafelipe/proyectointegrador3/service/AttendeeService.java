package dev.laurafelipe.proyectointegrador3.service;

import dev.laurafelipe.proyectointegrador3.entity.Attendee;
import dev.laurafelipe.proyectointegrador3.model.AttendeeModel;
import dev.laurafelipe.proyectointegrador3.repository.AttendeeRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.NoSuchElementException;

public class AttendeeService {
    private final AttendeeRepository repository;
    private final AttendeeModel model;

    public AttendeeService(AttendeeRepository repository, AttendeeModel model) {
        this.repository = repository;
        this.model = model;
    }

    public void findAllAttendees() {
        ObservableList<Attendee> attendees = FXCollections.observableArrayList(
                repository.findAll()
        );
        model.attendees.set(attendees);
    }

    public void createAttendee(Attendee attendee) {
        if (attendee.name == null || attendee.name.trim().isBlank()) {
            throw new IllegalArgumentException("Nombre no valido");
        }

        if (attendee.documentNumber == null || attendee.documentNumber.trim().isBlank()) {
            throw new IllegalArgumentException("Numero de documento no valido");
        }

        if (attendee.email == null || attendee.email.trim().isBlank()) {
            throw new IllegalArgumentException("Correo electronico no valido");
        }

        repository.save(attendee);
    }

    public void removeAttendeeById(String attendeeId) {
        if (repository.findById(attendeeId) == null) {
            throw new NoSuchElementException("Asistente no encontrado");
        }

        repository.removeAttendeeById(attendeeId);
    }

    public void updateAttendee(Attendee attendee) {
        if (attendee.name == null || attendee.name.trim().isBlank()) {
            throw new IllegalArgumentException("Nombre no valido");
        }

        if (attendee.documentNumber == null || attendee.documentNumber.trim().isBlank()) {
            throw new IllegalArgumentException("Numero de documento no valido");
        }

        if (attendee.email == null || attendee.email.trim().isBlank()) {
            throw new IllegalArgumentException("Correo electronico no valido");
        }

        repository.update(attendee);
    }
}
