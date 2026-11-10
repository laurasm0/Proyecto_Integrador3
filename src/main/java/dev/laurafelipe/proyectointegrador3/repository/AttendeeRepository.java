
package dev.laurafelipe.proyectointegrador3.repository;


import dev.laurafelipe.proyectointegrador3.entity.Attendee;
import com.mongodb.client.MongoDatabase;
import org.bson.UuidRepresentation;
import org.mongojack.JacksonMongoCollection;
import java.util.ArrayList;
import java.util.List;

public class AttendeeRepository {

    private final String COLLECTION_NAME = "attendees";
    private final JacksonMongoCollection<Attendee> collection;

    public AttendeeRepository(MongoDatabase database) {
        this.collection = JacksonMongoCollection
                .builder()
                .build(
                        database,
                        COLLECTION_NAME,
                        Attendee.class,
                        UuidRepresentation.STANDARD
                );
    }

    public List<Attendee> findAll() {
        return collection.find().into(new ArrayList<Attendee>());
    }

    public void save(Attendee attendee) {
        collection.insertOne(attendee);
    }

    public Attendee findById(String id) {
        return collection.findOneById(id);
    }

    public Long getCount() {
        return collection.countDocuments();
    }

    public void removeAttendeeById(String attendeeId) {
        collection.removeById(attendeeId);
    }

    public void update(Attendee attendee) {
        collection.replaceOneById(attendee.attendeeId, attendee);
    }
}
