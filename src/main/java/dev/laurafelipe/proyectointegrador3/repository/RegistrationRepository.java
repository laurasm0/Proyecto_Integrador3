package dev.laurafelipe.proyectointegrador3.repository;

import dev.laurafelipe.proyectointegrador3.entity.Registration;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.UuidRepresentation;
import org.bson.types.ObjectId;
import org.mongojack.JacksonMongoCollection;
import java.util.ArrayList;
import java.util.List;

public class RegistrationRepository {

    private final String COLLECTION_NAME = "registrations";
    private final JacksonMongoCollection<Registration> collection;

    public RegistrationRepository(MongoDatabase database) {
        this.collection = JacksonMongoCollection
                .builder()
                .build(
                        database,
                        COLLECTION_NAME,
                        Registration.class,
                        UuidRepresentation.STANDARD
                );

        // Refuerzo en la BD: un asistente no puede tener dos inscripciones ACTIVAS
        // al mismo evento (las canceladas no cuentan)
        this.collection.createIndex(
                Indexes.ascending("eventId", "attendeeId"),
                new IndexOptions()
                        .unique(true)
                        .partialFilterExpression(
                                Filters.eq("status", Registration.ACTIVE)
                        )
        );
    }

    public List<Registration> findAll() {
        return collection.find().into(new ArrayList<Registration>());
    }

    public void save(Registration registration) {
        collection.insertOne(registration);
    }

    public Registration findById(String id) {
        return collection.findOneById(id);
    }

    public Long getCount() {
        return collection.countDocuments();
    }

    public void update(Registration registration) {
        collection.replaceOneById(registration.registrationId, registration);
    }

    public long countActiveByEvent(String eventId) {
        return collection.countDocuments(
                Filters.and(
                        Filters.eq("eventId", new ObjectId(eventId)),
                        Filters.eq("status", Registration.ACTIVE)
                )
        );
    }

    public boolean existsActive(String eventId, String attendeeId) {
        return collection.countDocuments(
                Filters.and(
                        Filters.eq("eventId", new ObjectId(eventId)),
                        Filters.eq("attendeeId", new ObjectId(attendeeId)),
                        Filters.eq("status", Registration.ACTIVE)
                )
        ) > 0;
    }

    public boolean existsByEventId(String eventId) {
        return collection.countDocuments(
                Filters.eq("eventId", new ObjectId(eventId))
        ) > 0;
    }

    public boolean existsByAttendeeId(String attendeeId) {
        return collection.countDocuments(
                Filters.eq("attendeeId", new ObjectId(attendeeId))
        ) > 0;
    }
}
