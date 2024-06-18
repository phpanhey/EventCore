package de.panhey.eventcore.services;

import de.panhey.eventcore.entities.Event;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EventServiceTest {

    @Autowired
    private IEventService eventService;

    @Test
    void createEvent() {
        // given
        Event event = new Event( "Test Event", "Test Description", LocalDateTime.now(), LocalDateTime.now(), "Test Address");

        // when
        eventService.save(event);

        // then
        assertNotNull(event.getId());
    }

}