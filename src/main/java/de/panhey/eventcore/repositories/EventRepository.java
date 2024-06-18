package de.panhey.eventcore.repositories;
import de.panhey.eventcore.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
