package ewm.event.service;

import ewm.event.dto.EventFullDto;
import ewm.event.dto.EventShortDto;
import ewm.event.dto.NewEventDto;
import ewm.event.dto.UpdateEventUserRequest;

import java.util.List;

public interface EventService {
    EventFullDto create(Long userId, NewEventDto eventDto);

    EventFullDto get(Long userId, Long eventId);

    List<EventShortDto> getEvents(Long userId, int from, int size);

    EventFullDto update(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);
}
