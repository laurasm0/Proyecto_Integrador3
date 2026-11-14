package dev.laurafelipe.proyectointegrador3.controller;

import dev.laurafelipe.proyectointegrador3.model.EventModel;
import dev.laurafelipe.proyectointegrador3.repository.EventRepository;
import dev.laurafelipe.proyectointegrador3.repository.RegistrationRepository;
import dev.laurafelipe.proyectointegrador3.repository.VenueRepository;
import dev.laurafelipe.proyectointegrador3.service.EventService;
import dev.laurafelipe.proyectointegrador3.view.EventView;
import javafx.scene.layout.Region;

public class EventController {
    private final EventView view;
    private final EventService service;

    public EventController(
            EventRepository repository,
            VenueRepository venueRepository,
            RegistrationRepository registrationRepository
    ) {
        EventModel model = new EventModel();
        this.service = new EventService(
                repository,
                venueRepository,
                registrationRepository,
                model
        );
        this.view = new EventView(model, service);
    }

    public Region getView() {
        service.findAllEvents();
        return view.build();
    }

    // Se llama cuando se vuelve a seleccionar la pestana
    public void refresh() {
        service.findAllEvents();
    }
}
