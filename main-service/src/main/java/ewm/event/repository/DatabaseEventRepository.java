package ewm.event.repository;

import ewm.event.model.Event;
import ewm.event.model.EventState;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface DatabaseEventRepository extends EventRepository, JpaRepository<Event, Long> {
    @Override
    @Query("""
            SELECT e
            FROM EVENT e
            WHERE e.initiator.id IN :users
            AND e.state IN :states
            AND e.category.id IN :categories
            AND e.eventDate >= e.rangeStart
            AND e.eventDate <= e.rangeEnd
            """)
    List<Event> findForAdmin(List<Long> users,
                             List<EventState> states,
                             List<Integer> categories,
                             LocalDateTime rangeStart,
                             LocalDateTime rangeEnd,
                             Pageable page);
}
