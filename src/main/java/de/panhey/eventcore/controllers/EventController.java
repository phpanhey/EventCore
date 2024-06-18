package de.panhey.eventcore.controllers;

import de.panhey.eventcore.entities.Event;
import de.panhey.eventcore.services.IEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private IEventService eventService;

    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        return eventService.findById(id)
                .map(event -> ResponseEntity.ok().body(event))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Event createEvent(@RequestBody Event event) {
        return eventService.save(event);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody Event eventDetails) {
        return eventService.findById(id)
                .map(event -> {
                    event.setName(eventDetails.getName());
                    event.setDescription(eventDetails.getDescription());
                    event.setStartDateTime(eventDetails.getStartDateTime());
                    event.setEndDateTime(eventDetails.getEndDateTime());
                    event.setAddress(eventDetails.getAddress());
                    return ResponseEntity.ok().body(eventService.save(event));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        return eventService.findById(id)
                .map(event -> {
                    eventService.deleteById(event.getId());
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}