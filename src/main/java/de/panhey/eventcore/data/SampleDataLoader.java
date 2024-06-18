package de.panhey.eventcore.data;

import de.panhey.eventcore.entities.Event;
import de.panhey.eventcore.repositories.EventRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SampleDataLoader implements CommandLineRunner {

    private final EventRepository eventRepository;

    public SampleDataLoader(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public void run(String... args){

        if (eventRepository.count() == 0) {
            eventRepository.save(new Event("Event 1", "Description 1", LocalDateTime.now(), LocalDateTime.now().plusHours(2), "Address 1"));
            eventRepository.save(new Event("Event 2", "Description 2", LocalDateTime.now(), LocalDateTime.now().plusHours(2), "Address 2"));
            eventRepository.save(new Event("Event 3", "Description 3", LocalDateTime.now(), LocalDateTime.now().plusHours(2), "Address 3"));
            eventRepository.save(new Event("Event 4", "Description 4", LocalDateTime.now(), LocalDateTime.now().plusHours(2), "Address 4"));
            eventRepository.save(new Event("Event 5", "Description 5", LocalDateTime.now(), LocalDateTime.now().plusHours(2), "Address 5"));
        }
        // Add sample data here

    }
}