package ewm.request.repository;

import ewm.request.model.ParticipationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    boolean existsByEventIdAndRequesterId(Long eventId, Long requesterId);

    List<ParticipationRequest> findAllByRequesterId(Long requesterId);

    List<ParticipationRequest> findAllByEventId(Long eventId);

    Optional<ParticipationRequest> findByIdAndRequesterId(Long id, Long requesterId);

    List<ParticipationRequest> findAllByIdInAndEventId(Collection<Long> ids, Long eventId);
}
