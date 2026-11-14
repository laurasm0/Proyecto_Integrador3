package dev.laurafelipe.proyectointegrador3.model;

import dev.laurafelipe.proyectointegrador3.entity.Registration;

// Una fila de la tabla: la inscripcion + los nombres ya resueltos
public record RegistrationRow(
        Registration registration,
        String eventName,
        String attendeeName
) {
}
