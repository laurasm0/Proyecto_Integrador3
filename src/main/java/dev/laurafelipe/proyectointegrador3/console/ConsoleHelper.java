package dev.laurafelipe.proyectointegrador3.console;

import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

// Utilidades de lectura/impresion compartidas por todas las secciones de consola
public final class ConsoleHelper {

    private ConsoleHelper() {
    }

    public static String readLine(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    // Si el usuario deja el campo vacio se conserva el valor actual
    public static String readOrKeep(Scanner scanner, String label, String current) {
        System.out.print(label + " [" + current + "]: ");
        String value = scanner.nextLine();
        return value.isBlank() ? current : value;
    }

    public static int readInt(Scanner scanner, String prompt) {
        while (true) {
            String text = readLine(scanner, prompt).trim();

            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un numero valido.");
            }
        }
    }

    public static int readIntOrKeep(Scanner scanner, String label, int current) {
        while (true) {
            System.out.print(label + " [" + current + "]: ");
            String text = scanner.nextLine().trim();

            if (text.isEmpty()) {
                return current;
            }

            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un numero valido.");
            }
        }
    }

    // Muestra una lista numerada y devuelve el elemento elegido (o null si cancela)
    public static <T> T selectFromList(
            Scanner scanner,
            String title,
            List<T> items,
            Function<T, String> describe
    ) {
        if (items.isEmpty()) {
            System.out.println("No hay elementos disponibles.");
            return null;
        }

        System.out.println(title);

        for (int i = 0; i < items.size(); i++) {
            System.out.println((i + 1) + ". " + describe.apply(items.get(i)));
        }

        System.out.println("0. Cancelar");

        int option = readInt(scanner, "Seleccione una opcion: ");

        if (option < 1 || option > items.size()) {
            return null;
        }

        return items.get(option - 1);
    }
}
