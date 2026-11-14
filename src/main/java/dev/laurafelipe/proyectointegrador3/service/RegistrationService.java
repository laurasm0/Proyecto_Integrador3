package dev.laurafelipe.proyectointegrador3.service;

import dev.laurafelipe.proyectointegrador3.entity.Attendee;
import dev.laurafelipe.proyectointegrador3.entity.Event;
import dev.laurafelipe.proyectointegrador3.entity.Registration;
import dev.laurafelipe.proyectointegrador3.model.RegistrationModel;
import dev.laurafelipe.proyectointegrador3.model.RegistrationRow;
import dev.laurafelipe.proyectointegrador3.repository.AttendeeRepository;
import dev.laurafelipe.proyectointegrador3.repository.EventRepository;
import dev.laurafelipe.proyectointegrador3.repository.RegistrationRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistrationService {
    private final RegistrationRepository repository;
    private final EventRepository eventRepository;
    private final AttendeeRepository attendeeRepository;
    private final RegistrationModel model;

    public RegistrationService(
            RegistrationRepository repository,
            EventRepository eventRepository,
            AttendeeRepository attendeeRepository,
            RegistrationModel model
    ) {
        this.repository = repository;
        this.eventRepository = eventRepository;
        this.attendeeRepository = attendeeRepository;
        this.model = model;
    }

    public void findAllRegistrations() {
        List<Event> events = eventRepository.findAll();
        List<Attendee> attendees = attendeeRepository.findAll();

        Map<String, String> eventNames = new HashMap<>();
        for (Event event : events) {
            eventNames.put(event.eventId, event.name);
        }

        Map<String, String> attendeeNames = new HashMap<>();
        for (Attendee attendee : attendees) {
            attendeeNames.put(attendee.attendeeId, attendee.name);
        }

        List<RegistrationRow> rows = repository.findAll().stream()
                .map(registration -> new RegistrationRow(
                        registration,
                        eventNames.getOrDefault(registration.eventId, "(evento eliminado)"),
                        attendeeNames.getOrDefault(registration.attendeeId, "(asistente eliminado)")
                ))
                .toList();

        model.events.set(FXCollections.observableArrayList(events));
        model.attendees.set(FXCollections.observableArrayList(attendees));

        ObservableList<RegistrationRow> observableRows =
                FXCollections.observableArrayList(rows);
        model.registrations.set(observableRows);
    }

    // Eventos a los que todavia se puede inscribir (fecha de hoy en adelante)
    public List<Event> getUpcomingEvents() {
        LocalDate today = LocalDate.now();

        return model.events.stream()
                .filter(event -> !LocalDate.parse(event.date).isBefore(today))
                .toList();
    }

    public List<Attendee> getAttendees() {
        return model.attendees;
    }

    public long getAvailableSeats(Event event) {
        return event.capacity - repository.countActiveByEvent(event.eventId);
    }

    public void createRegistration(Event event, Attendee attendee) {
        if (event == null) {
            throw new IllegalArgumentException("Seleccione un evento");
        }

        if (attendee == null) {
            throw new IllegalArgumentException("Seleccione un asistente");
        }

        // Regla: no se puede inscribir a eventos cuya fecha ya paso
        if (LocalDate.parse(event.date).isBefore(LocalDate.now())) {
            throw new IllegalStateException("No se puede inscribir a un evento que ya paso");
        }

        // Regla: un asistente no se inscribe dos veces al mismo evento
        if (repository.existsActive(event.eventId, attendee.attendeeId)) {
            throw new IllegalStateException("El asistente ya esta inscrito en este evento");
        }

        // Regla: no superar la capacidad maxima
        if (getAvailableSeats(event) <= 0) {
            throw new IllegalStateException("El evento alcanzo su capacidad maxima");
        }

        Registration registration = new Registration();
        registration.eventId = event.eventId;
        registration.attendeeId = attendee.attendeeId;
        registration.registrationDate = LocalDate.now().toString();
        registration.status = Registration.ACTIVE;

        repository.save(registration);
    }

    // Regla: al cancelar se libera el cupo. El cupo se calcula contando solo las
    // inscripciones ACTIVAS, asi que basta con cambiar el estado.
    public void cancelRegistration(Registration registration) {
        if (!Registration.ACTIVE.equals(registration.status)) {
            throw new IllegalStateException("La inscripcion ya esta cancelada");
        }

        registration.status = Registration.CANCELLED;

        repository.update(registration);
    }
}
