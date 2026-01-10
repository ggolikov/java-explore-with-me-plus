package ewm.event.service;

import ewm.common.exception.BadRequestException;
import ewm.common.exception.ConflictException;
import ewm.common.exception.NotFoundException;
import ewm.event.dto.*;
import ewm.event.mapper.EventMapper;
import ewm.event.model.Event;
import ewm.event.model.EventSort;
import ewm.event.model.EventState;
import ewm.event.model.EventStateActionAdmin;
import ewm.event.repository.EventRepository;
import ewm.user.model.User;
import ewm.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public EventFullDto create(Long userId, NewEventDto eventDto) {
        isEventTimeValid(eventDto.getEventDate());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // TODO Category
        Event event = EventMapper.mapToEvent(user, eventDto, null);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);
        event = eventRepository.save(event);
        return EventMapper.mapToEventFullDto(event);
    }

    @Override
    public EventFullDto get(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new BadRequestException("User is not the initiator");
        }
        return EventMapper.mapToEventFullDto(event);
    }

    @Override
    public List<EventFullDto> get(List<Long> users,
                                  List<EventState> states,
                                  List<Integer> categories,
                                  LocalDateTime rangeStart,
                                  LocalDateTime rangeEnd,
                                  int from,
                                  int size) {
        Pageable page = PageRequest.of(from / size, size);
        return eventRepository.findForAdmin(users, states, categories, rangeStart, rangeEnd, page)
                .stream()
                .map(EventMapper::mapToEventFullDto)
                .toList();
    }

    @Override
    public EventFullDto getPublicEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Event is not published");
        }
        return EventMapper.mapToEventFullDto(event);
    }

    @Override
    public List<EventShortDto> getEvents(Long userId, int from, int size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Pageable page = PageRequest.of(from / size, size);
        return eventRepository.findByInitiatorId(userId, page)
                .stream()
                .map(EventMapper::mapToEventShortDto)
                .toList();
    }

    @Override
    public List<EventShortDto> getPublicEvents(String text,
                                               List<Integer> categories,
                                               Boolean paid,
                                               LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd,
                                               Boolean onlyAvailable,
                                               EventSort sort,
                                               int from,
                                               int size) {
        Sort sortBy = null;
        if (sort.equals(EventSort.EVENT_DATE)) {
            sortBy = Sort.by(Sort.Direction.DESC, "event_date");
        } else if (sort.equals(EventSort.VIEWS)) {
            sortBy = Sort.by(Sort.Direction.DESC, "views");
        }
        Pageable page = PageRequest.of(from / size, size, sortBy);
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        return eventRepository.findPublicEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, page)
                .stream()
                .map(EventMapper::mapToEventShortDto)
                .toList();
    }

    @Override
    public EventFullDto update(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        Event currentEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
        if (currentEvent.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Event is already published");
        }
        if (!currentEvent.getInitiator().getId().equals(userId)) {
            throw new BadRequestException("User not allowed to update event");
        }
        Event updatedEvent = EventMapper.updateEvent(currentEvent, updateEventUserRequest);
        isEventTimeValid(updatedEvent.getEventDate());
        updatedEvent = eventRepository.save(updatedEvent);
        return EventMapper.mapToEventFullDto(updatedEvent);
    }

    @Override
    public EventFullDto update(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event currentEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
        if (currentEvent.getState().equals(EventState.PUBLISHED) &&
                updateEventAdminRequest.getEventDate() != null &&
                updateEventAdminRequest.getEventDate().isAfter(currentEvent.getPublishedOn().minusHours(1))) {
            throw new ConflictException("Invalid event time");
        }
        if (updateEventAdminRequest.getStateAction() != null) {
            if (currentEvent.getState().equals(EventState.PENDING)) {
                if (updateEventAdminRequest.getStateAction().equals(EventStateActionAdmin.PUBLISH_EVENT)) {
                    currentEvent.setState(EventState.PUBLISHED);
                    currentEvent.setPublishedOn(LocalDateTime.now());
                } else {
                    currentEvent.setState(EventState.CANCELED);
                }
            } else {
                throw new ConflictException("Invalid event state");
            }
        }
        Event updatedEvent = EventMapper.updateEvent(currentEvent, updateEventAdminRequest);
        updatedEvent = eventRepository.save(updatedEvent);
        return EventMapper.mapToEventFullDto(updatedEvent);
    }

    private void isEventTimeValid(LocalDateTime eventTime) {
        if (eventTime.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Invalid event time");
        }
    }
}
