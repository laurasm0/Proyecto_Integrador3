
package dev.laurafelipe.proyectointegrador3;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import dev.laurafelipe.proyectointegrador3.entity.Attendee;
import dev.laurafelipe.proyectointegrador3.repository.AttendeeRepository;

import java.util.List;
import java.util.Scanner;

public class Main {

    // Esto es una constante
    public static final String MONGODB_URI = "mongodb://127.0.0.1:27017";

    public static void main(String[] args) {

        try (MongoClient client = MongoClients.create(MONGODB_URI)) {

            MongoDatabase database = client.getDatabase("integrativeprojectdb");

            AttendeeRepository repository = new AttendeeRepository(database);

            Scanner scanner = new Scanner(System.in);

            printMenu();

            String optionSelected = scanner.nextLine();

            switch (optionSelected) {
                case "1" -> createAttendee(scanner, repository);
                case "2" -> listAttendees(repository);
                case "3" -> System.out.println("Proximamente.");
                case "4" -> showCount(repository);
                default -> System.out.println("Opcion no valida.");
            }

        }

        // Comentar esto porque despues vamos a hacer la interfaz grafica
        // FXApplication.main(args);
    }

    private static void printMenu() {
        System.out.println("""
                Menu de asistentes
                1. Crear
                2. Listar
                3. Encontrar por ID
                4. Conteo de asistentes
                """);

        System.out.print("Seleccione una opcion: ");
    }

    private static void createAttendee(
            Scanner scanner,
            AttendeeRepository repository
    ) {
        System.out.println("\n--- Crear Asistente ---");

        System.out.print("Nombre: ");
        String name = scanner.nextLine();

        System.out.print("Numero de documento: ");
        String documentNumber = scanner.nextLine();

        System.out.print("Correo electronico: ");
        String email = scanner.nextLine();

        Attendee attendee = new Attendee();

        attendee.name = name;
        attendee.documentNumber = documentNumber;
        attendee.email = email;

        repository.save(attendee);

        System.out.println("\nAsistente creado exitosamente.");
        System.out.println(attendee);
    }

    private static void listAttendees(AttendeeRepository repository) {
        System.out.println("\n--- Asistentes ---");

        List<Attendee> attendees = repository.findAll();

        if (attendees.isEmpty()) {
            System.out.println("No se encontraron asistentes.");
            return;
        }

        for (Attendee attendee : attendees) {
            System.out.println(attendee);
        }
    }

    private static void showCount(AttendeeRepository repository) {
        System.out.println("Total de asistentes: " + repository.getCount());
    }
}
