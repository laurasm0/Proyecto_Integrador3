package dev.laurafelipe.proyectointegrador3.service;

import dev.laurafelipe.proyectointegrador3.entity.Event;
import dev.laurafelipe.proyectointegrador3.entity.Venue;
import dev.laurafelipe.proyectointegrador3.model.EventModel;
import dev.laurafelipe.proyectointegrador3.repository.EventRepository;
import dev.laurafelipe.proyectointegrador3.repository.RegistrationRepository;
import dev.laurafelipe.proyectointegrador3.repository.VenueRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.NoSuchElementException;

public class EventService {
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm");

    private final EventRepository repository;
    private final VenueRepository venueRepository;
    private final RegistrationRepository registrationRepository;
    private final EventModel model;

    public EventService(
            EventRepository repository,
            VenueRepository venueRepository,
            RegistrationRepository registrationRepository,
            EventModel model
    ) {
        this.repository = repository;
        this.venueRepository = venueRepository;
        this.registrationRepository = registrationRepository;
        this.model = model;
    }

    public void findAllEvents() {
        ObservableList<Venue> venues = FXCollections.observableArrayList(
                venueRepository.findAll()
        );
        model.venues.set(venues);

        ObservableList<Event> events = FXCollections.observableArrayList(
                repository.findAll()
        );
        model.events.set(events);
    }

    public List<Venue> getVenues() {
        return model.venues;
    }

    public String getVenueName(String venueId) {
        for (Venue venue : model.venues) {
            if (venue.venueId.equals(venueId)) {
                return venue.name;
            }
        }

        return "(lugar eliminado)";
    }

    public void createEvent(Event event) {
        validateEvent(event);

        event.name = event.name.trim();
        event.description = event.description.trim();

        repository.save(event);
    }

    public void removeEventById(String eventId) {
        if (repository.findById(eventId) == null) {
            throw new NoSuchElementException("Evento no encontrado");
        }

        if (registrationRepository.existsByEventId(eventId)) {
            throw new IllegalStateException(
                    "No se puede eliminar: el evento tiene inscripciones"
            );
        }

        repository.removeEventById(eventId);
    }

    public void updateEvent(Event event) {
        validateEvent(event);

        // No se puede bajar la capacidad por debajo de los inscritos activos
        long activeCount = registrationRepository.countActiveByEvent(event.eventId);

        if (event.capacity < activeCount) {
            throw new IllegalArgumentException(
                    "La capacidad no puede ser menor a los inscritos activos ("
                            + activeCount + ")"
            );
        }

        event.name = event.name.trim();
        event.description = event.description.trim();

        repository.update(event);
    }

    private void validateEvent(Event event) {
        if (event.name == null || event.name.trim().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        if (event.description == null || event.description.trim().isBlank()) {
            throw new IllegalArgumentException("La descripcion es obligatoria");
        }

        if (event.category == null || event.category.isBlank()) {
            throw new IllegalArgumentException("Seleccione una categoria");
        }

        if (event.date == null || event.date.isBlank()) {
            throw new IllegalArgumentException("Seleccione una fecha");
        }

        try {
            LocalDate.parse(event.date);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Fecha no valida. Formato: aaaa-mm-dd");
        }

        LocalTime start = parseTime(event.startTime, "inicio");
        LocalTime end = parseTime(event.endTime, "fin");

        if (!end.isAfter(start)) {
            throw new IllegalArgumentException(
                    "La hora de fin debe ser posterior a la hora de inicio"
            );
        }

        if (event.capacity <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor que cero");
        }

        if (event.venueId == null || event.venueId.isBlank()) {
            throw new IllegalArgumentException("Seleccione un lugar");
        }

        Venue venue = venueRepository.findById(event.venueId);

        if (venue == null) {
            throw new NoSuchElementException("Lugar no encontrado");
        }

        if (event.capacity > venue.capacity) {
            throw new IllegalArgumentException(
                    "La capacidad (" + event.capacity + ") supera la del lugar ("
                            + venue.capacity + ")"
            );
        }

        // Regla de negocio: no dos eventos en el mismo lugar y horario
        if (repository.existsOverlap(
                event.venueId,
                event.date,
                event.startTime,
                event.endTime,
                event.eventId
        )) {
            throw new IllegalStateException(
                    "Ya existe un evento en ese lugar y horario"
            );
        }
    }

    private LocalTime parseTime(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("La hora de " + label + " es obligatoria");
        }

        try {
            return LocalTime.parse(value.trim(), TIME_FORMAT);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Hora de " + label + " no valida. Formato: HH:mm (ej. 08:00)"
            );
        }
    }
}
