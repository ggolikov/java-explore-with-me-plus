package ewm.event.repository;

import ewm.event.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DatabaseEventRepository extends EventRepository, JpaRepository<Event, Long> {
}
