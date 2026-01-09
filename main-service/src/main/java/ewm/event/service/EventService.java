package ewm.event.service;

import ewm.event.dto.*;
import ewm.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto create(Long userId, NewEventDto eventDto);

    EventFullDto get(Long userId, Long eventId);

    List<EventFullDto> get(List<Long> users,
                           List<EventState> states,
                           List<Integer> categories,
                           LocalDateTime rangeStart,
                           LocalDateTime rangeEnd,
                           int from,
                           int size);

    List<EventShortDto> getEvents(Long userId, int from, int size);

    EventFullDto update(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    EventFullDto update(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);
}
