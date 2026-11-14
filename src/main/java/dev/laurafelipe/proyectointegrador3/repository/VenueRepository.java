package dev.laurafelipe.proyectointegrador3.repository;

import dev.laurafelipe.proyectointegrador3.entity.Venue;
import com.mongodb.client.MongoDatabase;
import org.bson.UuidRepresentation;
import org.mongojack.JacksonMongoCollection;
import java.util.ArrayList;
import java.util.List;

public class VenueRepository {

    private final String COLLECTION_NAME = "venues";
    private final JacksonMongoCollection<Venue> collection;

    public VenueRepository(MongoDatabase database) {
        this.collection = JacksonMongoCollection
                .builder()
                .build(
                        database,
                        COLLECTION_NAME,
                        Venue.class,
                        UuidRepresentation.STANDARD
                );
    }

    public List<Venue> findAll() {
        return collection.find().into(new ArrayList<Venue>());
    }

    public void save(Venue venue) {
        collection.insertOne(venue);
    }

    public Venue findById(String id) {
        return collection.findOneById(id);
    }

    public Long getCount() {
        return collection.countDocuments();
    }

    public void removeVenueById(String venueId) {
        collection.removeById(venueId);
    }

    public void update(Venue venue) {
        collection.replaceOneById(venue.venueId, venue);
    }
}
