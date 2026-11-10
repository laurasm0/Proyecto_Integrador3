
package dev.laurafelipe.proyectointegrador3.entity;

import org.mongojack.Id;
import org.mongojack.ObjectId;
public class Attendee {

    @ObjectId
    @Id
    public String attendeeId;

    public String name;
    public String documentNumber;
    public String email;

    @Override
    public String toString() {
        return "Attendee{" +
                "id=" + attendeeId + ", " +
                "name= " + name + ", " +
                "documentNumber= " + documentNumber + ", " +
                "email= " + email + "}";
    }
}
