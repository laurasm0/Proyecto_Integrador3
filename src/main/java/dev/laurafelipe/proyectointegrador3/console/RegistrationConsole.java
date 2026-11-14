package dev.laurafelipe.proyectointegrador3.console;

import dev.laurafelipe.proyectointegrador3.entity.Attendee;
import dev.laurafelipe.proyectointegrador3.entity.Event;
import dev.laurafelipe.proyectointegrador3.entity.Registration;
import dev.laurafelipe.proyectointegrador3.model.RegistrationModel;
import dev.laurafelipe.proyectointegrador3.model.RegistrationRow;
import dev.laurafelipe.proyectointegrador3.repository.AttendeeRepository;
import dev.laurafelipe.proyectointegrador3.repository.EventRepository;
import dev.laurafelipe.proyectointegrador3.repository.RegistrationRepository;
import dev.laurafelipe.proyectointegrador3.service.RegistrationService;

import java.util.List;
import java.util.Scanner;

public class RegistrationConsole implements ConsoleSection {
    private final RegistrationModel model = new RegistrationModel();
    private final RegistrationService service;

    public RegistrationConsole(
            RegistrationRepository repository,
            EventRepository eventRepository,
            AttendeeRepository attendeeRepository
    ) {
        this.service = new RegistrationService(
                repository,
                eventRepository,
                attendeeRepository,
                model
        );
    }

    @Override
    public String getTitle() {
        return "Inscripciones";
    }

    @Override
    public void run(Scanner scanner) {
        boolean back = false;

        while (!back) {
            printMenu();

            String optionSelected = scanner.nextLine().trim();

            try {
                switch (optionSelected) {
                    case "1" -> listRegistrations();
                    case "2" -> createRegistration(scanner);
                    case "3" -> cancelRegistration(scanner);
                    case "4" -> showSeats();
                    case "0" -> back = true;
                    default -> System.out.println("Opcion no valida.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println("""

                --- Menu de inscripciones ---
                1. Listar
                2. Inscribir asistente a un evento
                3. Cancelar inscripcion
                4. Ver cupos por evento
                0. Volver
                """);

        System.out.print("Seleccione una opcion: ");
    }

    private String describe(RegistrationRow row) {
        return row.eventName() + " | " + row.attendeeName() + " | "
                + row.registration().registrationDate + " | "
                + row.registration().status;
    }

    private void listRegistrations() {
        System.out.println("\n--- Inscripciones ---");

        service.findAllRegistrations();

        if (model.registrations.isEmpty()) {
            System.out.println("No se encontraron inscripciones.");
            return;
        }

        for (RegistrationRow row : model.registrations) {
            System.out.println(describe(row));
        }
    }

    private void createRegistration(Scanner scanner) {
        System.out.println("\n--- Inscribir asistente ---");

        service.findAllRegistrations();

        Event event = ConsoleHelper.selectFromList(
                scanner,
                "Eventos disponibles:",
                service.getUpcomingEvents(),
                e -> e.name + " (" + e.date + ") - cupos: " + service.getAvailableSeats(e)
        );

        if (event == null) {
            System.out.println("Cancelado.");
            return;
        }

        Attendee attendee = ConsoleHelper.selectFromList(
                scanner,
                "Asistentes:",
                service.getAttendees(),
                a -> a.name + " - " + a.documentNumber
        );

        if (attendee == null) {
            System.out.println("Cancelado.");
            return;
        }

        service.createRegistration(event, attendee);

        System.out.println("Inscripcion registrada exitosamente.");
    }

    private void cancelRegistration(Scanner scanner) {
        service.findAllRegistrations();

        List<RegistrationRow> activeRows = model.registrations.stream()
                .filter(r -> Registration.ACTIVE.equals(r.registration().status))
                .toList();

        RegistrationRow row = ConsoleHelper.selectFromList(
                scanner, "\n--- Cancelar inscripcion ---", activeRows, this::describe
        );

        if (row == null) {
            System.out.println("Cancelado.");
            return;
        }

        service.cancelRegistration(row.registration());

        System.out.println("Inscripcion cancelada. El cupo fue liberado.");
    }

    private void showSeats() {
        System.out.println("\n--- Cupos por evento ---");

        service.findAllRegistrations();

        if (model.events.isEmpty()) {
            System.out.println("No hay eventos.");
            return;
        }

        for (Event event : model.events) {
            long available = service.getAvailableSeats(event);

            System.out.println(
                    event.name + " (" + event.date + "): "
                            + (event.capacity - available) + " / " + event.capacity
                            + " inscritos, " + available + " cupos libres"
            );
        }
    }
}
