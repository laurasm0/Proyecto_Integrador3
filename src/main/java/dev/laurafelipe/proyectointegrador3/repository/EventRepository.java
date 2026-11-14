package dev.laurafelipe.proyectointegrador3.repository;

import dev.laurafelipe.proyectointegrador3.entity.Event;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.UuidRepresentation;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.mongojack.JacksonMongoCollection;
import java.util.ArrayList;
import java.util.List;

public class EventRepository {

    private final String COLLECTION_NAME = "events";
    private final JacksonMongoCollection<Event> collection;

    public EventRepository(MongoDatabase database) {
        this.collection = JacksonMongoCollection
                .builder()
                .build(
                        database,
                        COLLECTION_NAME,
                        Event.class,
                        UuidRepresentation.STANDARD
                );
    }

    public List<Event> findAll() {
        return collection.find().into(new ArrayList<Event>());
    }

    public void save(Event event) {
        collection.insertOne(event);
    }

    public Event findById(String id) {
        return collection.findOneById(id);
    }

    public Long getCount() {
        return collection.countDocuments();
    }

    public void removeEventById(String eventId) {
        collection.removeById(eventId);
    }

    public void update(Event event) {
        collection.replaceOneById(event.eventId, event);
    }

    public boolean existsByVenueId(String venueId) {
        return collection.countDocuments(
                Filters.eq("venueId", new ObjectId(venueId))
        ) > 0;
    }

    // Dos rangos se traslapan si: inicioA < finB  Y  finA > inicioB
    // (las horas "HH:mm" se pueden comparar como texto)
    public boolean existsOverlap(
            String venueId,
            String date,
            String startTime,
            String endTime,
            String excludeEventId
    ) {
        List<Bson> filters = new ArrayList<>();

        filters.add(Filters.eq("venueId", new ObjectId(venueId)));
        filters.add(Filters.eq("date", date));
        filters.add(Filters.lt("startTime", endTime));
        filters.add(Filters.gt("endTime", startTime));

        if (excludeEventId != null) {
            filters.add(Filters.ne("_id", new ObjectId(excludeEventId)));
        }

        return collection.countDocuments(Filters.and(filters)) > 0;
    }
}
