package dev.laurafelipe.proyectointegrador3.console;

import java.util.Scanner;

// Cada entidad del menu de consola es una "seccion".
// Para agregar o quitar una, solo se agrega o quita una linea en Main.
public interface ConsoleSection {

    // Texto que aparece en el menu principal
    String getTitle();

    // Muestra el submenu y atiende las opciones hasta que el usuario elija volver
    void run(Scanner scanner);
}
