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
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        if (attendee.documentNumber == null ||
                !attendee.documentNumber.matches("[0-9]{9}")) {
            throw new IllegalArgumentException(
                    "El documento debe tener exactamente 9 digitos numericos"
            );
        }

        if (attendee.email == null ||
                !attendee.email.trim().matches(
                        "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException(
                    "Correo no valido. Ejemplo: usuario@gmail.com"
            );
        }

        attendee.email = attendee.email.trim();

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
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        if (attendee.documentNumber == null ||
                !attendee.documentNumber.matches("[0-9]{9}")) {
            throw new IllegalArgumentException(
                    "El documento debe tener exactamente 9 digitos numericos"
            );
        }

        if (attendee.email == null ||
                !attendee.email.trim().matches(
                        "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException(
                    "Correo no valido. Ejemplo: usuario@gmail.com"
            );
        }

        attendee.email = attendee.email.trim();

        repository.update(attendee);
    }
}
