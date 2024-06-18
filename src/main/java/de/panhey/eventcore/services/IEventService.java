package de.panhey.eventcore.services;

import de.panhey.eventcore.entities.Event;

import java.util.List;
import java.util.Optional;

public interface IEventService {

    List<Event> findAll();

    Optional<Event> findById(Long id);

    Event save(Event event);

    void deleteById(Long id);
}








