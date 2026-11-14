package dev.laurafelipe.proyectointegrador3.console;

import dev.laurafelipe.proyectointegrador3.entity.Event;
import dev.laurafelipe.proyectointegrador3.entity.Venue;
import dev.laurafelipe.proyectointegrador3.model.EventModel;
import dev.laurafelipe.proyectointegrador3.repository.EventRepository;
import dev.laurafelipe.proyectointegrador3.repository.RegistrationRepository;
import dev.laurafelipe.proyectointegrador3.repository.VenueRepository;
import dev.laurafelipe.proyectointegrador3.service.EventService;

import java.util.Scanner;

public class EventConsole implements ConsoleSection {
    private final EventModel model = new EventModel();
    private final EventService service;

    public EventConsole(
            EventRepository repository,
            VenueRepository venueRepository,
            RegistrationRepository registrationRepository
    ) {
        this.service = new EventService(
                repository,
                venueRepository,
                registrationRepository,
                model
        );
    }

    @Override
    public String getTitle() {
        return "Eventos";
    }

    @Override
    public void run(Scanner scanner) {
        boolean back = false;

        while (!back) {
            printMenu();

            String optionSelected = scanner.nextLine().trim();

            try {
                switch (optionSelected) {
                    case "1" -> listEvents();
                    case "2" -> createEvent(scanner);
                    case "3" -> updateEvent(scanner);
                    case "4" -> removeEvent(scanner);
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

                --- Menu de eventos ---
                1. Listar
                2. Crear
                3. Editar
                4. Eliminar
                0. Volver
                """);

        System.out.print("Seleccione una opcion: ");
    }

    private String describeEvent(Event event) {
        return event.name + " | " + event.category + " | " + event.date + " "
                + event.startTime + "-" + event.endTime + " | "
                + service.getVenueName(event.venueId) + " | cap. " + event.capacity;
    }

    private String describeVenue(Venue venue) {
        return venue.name + " (cap. " + venue.capacity + ")";
    }

    private void listEvents() {
        System.out.println("\n--- Eventos ---");

        service.findAllEvents();

        if (model.events.isEmpty()) {
            System.out.println("No se encontraron eventos.");
            return;
        }

        for (Event event : model.events) {
            System.out.println(describeEvent(event));
        }
    }

    private void createEvent(Scanner scanner) {
        System.out.println("\n--- Crear Evento ---");

        service.findAllEvents();

        Venue venue = ConsoleHelper.selectFromList(
                scanner, "Lugares disponibles:", model.venues, this::describeVenue
        );

        if (venue == null) {
            System.out.println("Cancelado. Primero debe existir al menos un lugar.");
            return;
        }

        Event event = new Event();

        event.name = ConsoleHelper.readLine(scanner, "Nombre: ");
        event.description = ConsoleHelper.readLine(scanner, "Descripcion: ");
        event.category = ConsoleHelper.selectFromList(
                scanner, "Categorias:", Event.CATEGORIES, category -> category
        );
        event.date = ConsoleHelper.readLine(scanner, "Fecha (aaaa-mm-dd): ").trim();
        event.startTime = ConsoleHelper.readLine(scanner, "Hora de inicio (HH:mm): ").trim();
        event.endTime = ConsoleHelper.readLine(scanner, "Hora de fin (HH:mm): ").trim();
        event.venueId = venue.venueId;
        event.capacity = ConsoleHelper.readInt(scanner, "Capacidad: ");

        service.createEvent(event);

        System.out.println("\nEvento creado exitosamente.");
        System.out.println(event);
    }

    private void updateEvent(Scanner scanner) {
        service.findAllEvents();

        Event event = ConsoleHelper.selectFromList(
                scanner, "\n--- Editar Evento ---", model.events, this::describeEvent
        );

        if (event == null) {
            System.out.println("Cancelado.");
            return;
        }

        System.out.println("(deje vacio para conservar el valor actual)");

        Event updatedEvent = new Event();

        updatedEvent.eventId = event.eventId;
        updatedEvent.name = ConsoleHelper.readOrKeep(scanner, "Nombre", event.name);
        updatedEvent.description = ConsoleHelper.readOrKeep(scanner, "Descripcion", event.description);
        updatedEvent.category = event.category;
        updatedEvent.date = ConsoleHelper.readOrKeep(scanner, "Fecha (aaaa-mm-dd)", event.date).trim();
        updatedEvent.startTime = ConsoleHelper.readOrKeep(scanner, "Hora de inicio", event.startTime).trim();
        updatedEvent.endTime = ConsoleHelper.readOrKeep(scanner, "Hora de fin", event.endTime).trim();
        updatedEvent.venueId = event.venueId;
        updatedEvent.capacity = ConsoleHelper.readIntOrKeep(scanner, "Capacidad", event.capacity);

        // Categoria y lugar: se muestra la lista; si cancela se conserva el actual
        String category = ConsoleHelper.selectFromList(
                scanner, "Categoria (0 para conservar la actual):", Event.CATEGORIES, c -> c
        );
        if (category != null) {
            updatedEvent.category = category;
        }

        Venue venue = ConsoleHelper.selectFromList(
                scanner, "Lugar (0 para conservar el actual):", model.venues, this::describeVenue
        );
        if (venue != null) {
            updatedEvent.venueId = venue.venueId;
        }

        service.updateEvent(updatedEvent);

        System.out.println("Evento actualizado exitosamente.");
    }

    private void removeEvent(Scanner scanner) {
        service.findAllEvents();

        Event event = ConsoleHelper.selectFromList(
                scanner, "\n--- Eliminar Evento ---", model.events, this::describeEvent
        );

        if (event == null) {
            System.out.println("Cancelado.");
            return;
        }

        service.removeEventById(event.eventId);

        System.out.println("Evento eliminado exitosamente.");
    }
}
