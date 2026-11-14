package dev.laurafelipe.proyectointegrador3.entity;

import org.mongojack.Id;
import org.mongojack.ObjectId;

public class Registration {

    public static final String ACTIVE = "Activa";
    public static final String CANCELLED = "Cancelada";

    @ObjectId
    @Id
    public String registrationId;

    @ObjectId
    public String eventId;

    @ObjectId
    public String attendeeId;

    public String registrationDate;
    public String status;

    @Override
    public String toString() {
        return "Registration{" +
                "id=" + registrationId + ", " +
                "eventId= " + eventId + ", " +
                "attendeeId= " + attendeeId + ", " +
                "registrationDate= " + registrationDate + ", " +
                "status= " + status + "}";
    }
}
