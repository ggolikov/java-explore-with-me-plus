package ewm.event.repository;

import ewm.event.model.Event;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EventRepository {
    Event save(Event event);

    List<Event> findByInitiatorId(Long initiatorId, Pageable page);

    Optional<Event> findById(Long eventId);
}
