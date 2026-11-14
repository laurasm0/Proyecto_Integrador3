package dev.laurafelipe.proyectointegrador3.controller;

import dev.laurafelipe.proyectointegrador3.model.RegistrationModel;
import dev.laurafelipe.proyectointegrador3.repository.AttendeeRepository;
import dev.laurafelipe.proyectointegrador3.repository.EventRepository;
import dev.laurafelipe.proyectointegrador3.repository.RegistrationRepository;
import dev.laurafelipe.proyectointegrador3.service.RegistrationService;
import dev.laurafelipe.proyectointegrador3.view.RegistrationView;
import javafx.scene.layout.Region;

public class RegistrationController {
    private final RegistrationView view;
    private final RegistrationService service;

    public RegistrationController(
            RegistrationRepository repository,
            EventRepository eventRepository,
            AttendeeRepository attendeeRepository
    ) {
        RegistrationModel model = new RegistrationModel();
        this.service = new RegistrationService(
                repository,
                eventRepository,
                attendeeRepository,
                model
        );
        this.view = new RegistrationView(model, service);
    }

    public Region getView() {
        service.findAllRegistrations();
        return view.build();
    }

    // Se llama cuando se vuelve a seleccionar la pestana
    public void refresh() {
        service.findAllRegistrations();
    }
}
