package dev.laurafelipe.proyectointegrador3.console;

import java.util.List;
import java.util.Scanner;

// Menu principal: lista las secciones que reciba y delega en la elegida.
public class ConsoleMenu {
    private final List<ConsoleSection> sections;

    public ConsoleMenu(List<ConsoleSection> sections) {
        this.sections = sections;
    }

    public void run(Scanner scanner) {
        boolean exit = false;

        while (!exit) {
            printMenu();

            String optionSelected = scanner.nextLine().trim();

            if (optionSelected.equals("0")) {
                exit = true;
                continue;
            }

            try {
                int index = Integer.parseInt(optionSelected) - 1;

                if (index < 0 || index >= sections.size()) {
                    System.out.println("Opcion no valida.");
                } else {
                    sections.get(index).run(scanner);
                }
            } catch (NumberFormatException e) {
                System.out.println("Opcion no valida.");
            }
        }

        System.out.println("Hasta luego.");
    }

    private void printMenu() {
        System.out.println();
        System.out.println("=== Menu principal (modo consola) ===");

        for (int i = 0; i < sections.size(); i++) {
            System.out.println((i + 1) + ". " + sections.get(i).getTitle());
        }

        System.out.println("0. Salir");
        System.out.print("Seleccione una opcion: ");
    }
}
