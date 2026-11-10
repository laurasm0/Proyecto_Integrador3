package dev.laurafelipe.proyectointegrador3.controller;

import dev.laurafelipe.proyectointegrador3.model.AttendeeModel;
import dev.laurafelipe.proyectointegrador3.repository.AttendeeRepository;
import dev.laurafelipe.proyectointegrador3.service.AttendeeService;
import dev.laurafelipe.proyectointegrador3.view.AttendeeView;
import javafx.scene.layout.Region;

public class AttendeeController {
    private final AttendeeView view;
    private final AttendeeService service;

    public AttendeeController(AttendeeRepository repository) {
        AttendeeModel model = new AttendeeModel();
        this.service = new AttendeeService(repository, model);
        this.view = new AttendeeView(model, service);
    }

    public Region getView() {
        service.findAllAttendees();
        return view.build();
    }
}
