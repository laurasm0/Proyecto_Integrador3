package dev.laurafelipe.proyectointegrador3.service;

import dev.laurafelipe.proyectointegrador3.entity.Venue;
import dev.laurafelipe.proyectointegrador3.model.VenueModel;
import dev.laurafelipe.proyectointegrador3.repository.EventRepository;
import dev.laurafelipe.proyectointegrador3.repository.VenueRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.NoSuchElementException;

public class VenueService {
    private final VenueRepository repository;
    private final EventRepository eventRepository;
    private final VenueModel model;

    public VenueService(
            VenueRepository repository,
            EventRepository eventRepository,
            VenueModel model
    ) {
        this.repository = repository;
        this.eventRepository = eventRepository;
        this.model = model;
    }

    public void findAllVenues() {
        ObservableList<Venue> venues = FXCollections.observableArrayList(
                repository.findAll()
        );
        model.venues.set(venues);
    }

    public void createVenue(Venue venue) {
        validateVenue(venue);

        venue.name = venue.name.trim();
        venue.type = venue.type.trim();

        repository.save(venue);
    }

    public void removeVenueById(String venueId) {
        if (repository.findById(venueId) == null) {
            throw new NoSuchElementException("Lugar no encontrado");
        }

        // Mongo no valida integridad referencial, la validamos nosotros
        if (eventRepository.existsByVenueId(venueId)) {
            throw new IllegalStateException(
                    "No se puede eliminar: hay eventos asignados a este lugar"
            );
        }

        repository.removeVenueById(venueId);
    }

    public void updateVenue(Venue venue) {
        validateVenue(venue);

        venue.name = venue.name.trim();
        venue.type = venue.type.trim();

        repository.update(venue);
    }

    private void validateVenue(Venue venue) {
        if (venue.name == null || venue.name.trim().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        if (venue.type == null || venue.type.trim().isBlank()) {
            throw new IllegalArgumentException("El tipo es obligatorio");
        }

        if (venue.capacity <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor que cero");
        }
    }
}
