package dev.laurafelipe.proyectointegrador3.console;

import dev.laurafelipe.proyectointegrador3.entity.Attendee;
import dev.laurafelipe.proyectointegrador3.model.AttendeeModel;
import dev.laurafelipe.proyectointegrador3.repository.AttendeeRepository;
import dev.laurafelipe.proyectointegrador3.service.AttendeeService;

import java.util.Scanner;

// Usa AttendeeService (no el repositorio) para que apliquen tus validaciones
public class AttendeeConsole implements ConsoleSection {
    private final AttendeeRepository repository;
    private final AttendeeModel model = new AttendeeModel();
    private final AttendeeService service;

    public AttendeeConsole(AttendeeRepository repository) {
        this.repository = repository;
        this.service = new AttendeeService(repository, model);
    }

    @Override
    public String getTitle() {
        return "Asistentes";
    }

    @Override
    public void run(Scanner scanner) {
        boolean back = false;

        while (!back) {
            printMenu();

            String optionSelected = scanner.nextLine().trim();

            try {
                switch (optionSelected) {
                    case "1" -> createAttendee(scanner);
                    case "2" -> listAttendees();
                    case "3" -> removeAttendee(scanner);
                    case "4" -> System.out.println("Total de asistentes: " + repository.getCount());
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

                --- Menu de asistentes ---
                1. Crear
                2. Listar
                3. Eliminar
                4. Conteo de asistentes
                0. Volver
                """);

        System.out.print("Seleccione una opcion: ");
    }

    private void createAttendee(Scanner scanner) {
        System.out.println("\n--- Crear Asistente ---");

        Attendee attendee = new Attendee();

        attendee.name = ConsoleHelper.readLine(scanner, "Nombre: ");
        attendee.documentNumber = ConsoleHelper.readLine(scanner, "Numero de documento: ");
        attendee.email = ConsoleHelper.readLine(scanner, "Correo electronico: ");

        service.createAttendee(attendee);

        System.out.println("\nAsistente creado exitosamente.");
        System.out.println(attendee);
    }

    private void listAttendees() {
        System.out.println("\n--- Asistentes ---");

        service.findAllAttendees();

        if (model.attendees.isEmpty()) {
            System.out.println("No se encontraron asistentes.");
            return;
        }

        for (Attendee attendee : model.attendees) {
            System.out.println(attendee);
        }
    }

    private void removeAttendee(Scanner scanner) {
        service.findAllAttendees();

        Attendee attendee = ConsoleHelper.selectFromList(
                scanner,
                "\n--- Eliminar Asistente ---",
                model.attendees,
                a -> a.name + " - " + a.documentNumber
        );

        if (attendee == null) {
            System.out.println("Cancelado.");
            return;
        }

        service.removeAttendeeById(attendee.attendeeId);

        System.out.println("Asistente eliminado exitosamente.");
    }
}
