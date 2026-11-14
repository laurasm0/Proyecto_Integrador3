package dev.laurafelipe.proyectointegrador3.controller;

import dev.laurafelipe.proyectointegrador3.model.VenueModel;
import dev.laurafelipe.proyectointegrador3.repository.EventRepository;
import dev.laurafelipe.proyectointegrador3.repository.VenueRepository;
import dev.laurafelipe.proyectointegrador3.service.VenueService;
import dev.laurafelipe.proyectointegrador3.view.VenueView;
import javafx.scene.layout.Region;

public class VenueController {
    private final VenueView view;
    private final VenueService service;

    public VenueController(VenueRepository repository, EventRepository eventRepository) {
        VenueModel model = new VenueModel();
        this.service = new VenueService(repository, eventRepository, model);
        this.view = new VenueView(model, service);
    }

    public Region getView() {
        service.findAllVenues();
        return view.build();
    }

    // Se llama cuando se vuelve a seleccionar la pestana
    public void refresh() {
        service.findAllVenues();
    }
}
