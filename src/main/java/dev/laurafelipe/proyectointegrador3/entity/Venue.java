package dev.laurafelipe.proyectointegrador3.entity;

import org.mongojack.Id;
import org.mongojack.ObjectId;

public class Venue {

    @ObjectId
    @Id
    public String venueId;

    public String name;
    public String type;
    public int capacity;

    @Override
    public String toString() {
        return "Venue{" +
                "id=" + venueId + ", " +
                "name= " + name + ", " +
                "type= " + type + ", " +
                "capacity= " + capacity + "}";
    }
}
