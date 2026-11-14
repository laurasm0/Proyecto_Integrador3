package dev.laurafelipe.proyectointegrador3.entity;

import org.mongojack.Id;
import org.mongojack.ObjectId;

import java.util.List;

public class Event {

    public static final List<String> CATEGORIES =
            List.of("Academico", "Cultural", "Deportivo", "Investigacion");

    @ObjectId
    @Id
    public String eventId;

    public String name;
    public String description;
    public String category;

    // Formato ISO para poder comparar como texto: "2026-11-20" y "08:00"
    public String date;
    public String startTime;
    public String endTime;

    // Referencia a venues._id (se guarda como ObjectId)
    @ObjectId
    public String venueId;

    public int capacity;

    @Override
    public String toString() {
        return "Event{" +
                "id=" + eventId + ", " +
                "name= " + name + ", " +
                "category= " + category + ", " +
                "date= " + date + ", " +
                "startTime= " + startTime + ", " +
                "endTime= " + endTime + ", " +
                "venueId= " + venueId + ", " +
                "capacity= " + capacity + "}";
    }
}
