
package dev.laurafelipe.proyectointegrador3;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import dev.laurafelipe.proyectointegrador3.console.AttendeeConsole;
import dev.laurafelipe.proyectointegrador3.console.ConsoleMenu;
import dev.laurafelipe.proyectointegrador3.console.EventConsole;
import dev.laurafelipe.proyectointegrador3.console.RegistrationConsole;
import dev.laurafelipe.proyectointegrador3.console.VenueConsole;
import dev.laurafelipe.proyectointegrador3.repository.AttendeeRepository;
import dev.laurafelipe.proyectointegrador3.repository.EventRepository;
import dev.laurafelipe.proyectointegrador3.repository.RegistrationRepository;
import dev.laurafelipe.proyectointegrador3.repository.VenueRepository;

import java.util.List;
import java.util.Scanner;

public class Main {

    // Esto es una constante
    public static final String MONGODB_URI = "mongodb://127.0.0.1:27017";

    public static void main(String[] args) {

        try (MongoClient client = MongoClients.create(MONGODB_URI)) {

            MongoDatabase database = client.getDatabase("integrativeprojectdb");

            AttendeeRepository attendeeRepository = new AttendeeRepository(database);
            VenueRepository venueRepository = new VenueRepository(database);
            EventRepository eventRepository = new EventRepository(database);
            RegistrationRepository registrationRepository = new RegistrationRepository(database);

            Scanner scanner = new Scanner(System.in);

            // Para agregar o quitar una seccion del menu, solo se edita esta lista.
            // Cada seccion vive en el paquete "console" y usa los mismos services
            // que la interfaz grafica, asi que las reglas de negocio son las mismas.
            ConsoleMenu menu = new ConsoleMenu(List.of(
                    new EventConsole(eventRepository, venueRepository, registrationRepository),
                    new VenueConsole(venueRepository, eventRepository),
                    new AttendeeConsole(attendeeRepository),
                    new RegistrationConsole(registrationRepository, eventRepository, attendeeRepository)
            ));

            menu.run(scanner);

        }

        // Comentar esto porque despues vamos a hacer la interfaz grafica
        // FXApplication.main(args);
    }
}
