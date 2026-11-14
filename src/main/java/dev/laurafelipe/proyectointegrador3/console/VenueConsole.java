package dev.laurafelipe.proyectointegrador3.console;

import dev.laurafelipe.proyectointegrador3.entity.Venue;
import dev.laurafelipe.proyectointegrador3.model.VenueModel;
import dev.laurafelipe.proyectointegrador3.repository.EventRepository;
import dev.laurafelipe.proyectointegrador3.repository.VenueRepository;
import dev.laurafelipe.proyectointegrador3.service.VenueService;

import java.util.Scanner;

public class VenueConsole implements ConsoleSection {
    private final VenueModel model = new VenueModel();
    private final VenueService service;

    public VenueConsole(VenueRepository repository, EventRepository eventRepository) {
        this.service = new VenueService(repository, eventRepository, model);
    }

    @Override
    public String getTitle() {
        return "Lugares";
    }

    @Override
    public void run(Scanner scanner) {
        boolean back = false;

        while (!back) {
            printMenu();

            String optionSelected = scanner.nextLine().trim();

            try {
                switch (optionSelected) {
                    case "1" -> listVenues();
                    case "2" -> createVenue(scanner);
                    case "3" -> updateVenue(scanner);
                    case "4" -> removeVenue(scanner);
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

                --- Menu de lugares ---
                1. Listar
                2. Crear
                3. Editar
                4. Eliminar
                0. Volver
                """);

        System.out.print("Seleccione una opcion: ");
    }

    private String describe(Venue venue) {
        return venue.name + " | " + venue.type + " | cap. " + venue.capacity;
    }

    private void listVenues() {
        System.out.println("\n--- Lugares ---");

        service.findAllVenues();

        if (model.venues.isEmpty()) {
            System.out.println("No se encontraron lugares.");
            return;
        }

        for (Venue venue : model.venues) {
            System.out.println(describe(venue));
        }
    }

    private void createVenue(Scanner scanner) {
        System.out.println("\n--- Crear Lugar ---");

        Venue venue = new Venue();

        venue.name = ConsoleHelper.readLine(scanner, "Nombre: ");
        venue.type = ConsoleHelper.readLine(scanner, "Tipo (Auditorio, Aula, Coliseo...): ");
        venue.capacity = ConsoleHelper.readInt(scanner, "Capacidad: ");

        service.createVenue(venue);

        System.out.println("\nLugar creado exitosamente.");
        System.out.println(venue);
    }

    private void updateVenue(Scanner scanner) {
        service.findAllVenues();

        Venue venue = ConsoleHelper.selectFromList(
                scanner, "\n--- Editar Lugar ---", model.venues, this::describe
        );

        if (venue == null) {
            System.out.println("Cancelado.");
            return;
        }

        System.out.println("(deje vacio para conservar el valor actual)");

        Venue updatedVenue = new Venue();

        updatedVenue.venueId = venue.venueId;
        updatedVenue.name = ConsoleHelper.readOrKeep(scanner, "Nombre", venue.name);
        updatedVenue.type = ConsoleHelper.readOrKeep(scanner, "Tipo", venue.type);
        updatedVenue.capacity = ConsoleHelper.readIntOrKeep(scanner, "Capacidad", venue.capacity);

        service.updateVenue(updatedVenue);

        System.out.println("Lugar actualizado exitosamente.");
    }

    private void removeVenue(Scanner scanner) {
        service.findAllVenues();

        Venue venue = ConsoleHelper.selectFromList(
                scanner, "\n--- Eliminar Lugar ---", model.venues, this::describe
        );

        if (venue == null) {
            System.out.println("Cancelado.");
            return;
        }

        service.removeVenueById(venue.venueId);

        System.out.println("Lugar eliminado exitosamente.");
    }
}
