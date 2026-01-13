package ewm.event.service;

import client.StatsClient;
import ewm.category.model.Category;
import ewm.category.repository.CategoryRepository;
import ewm.common.exception.BadRequestException;
import ewm.common.exception.ConflictException;
import ewm.common.exception.NotFoundException;
import ewm.event.dto.*;
import ewm.event.mapper.EventMapper;
import ewm.event.model.Event;
import ewm.event.model.EventSort;
import ewm.event.model.EventState;
import ewm.event.model.EventStateActionAdmin;
import ewm.event.repository.DatabaseEventSearchRepository;
import ewm.event.repository.EventRepository;
import ewm.user.model.User;
import ewm.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final DatabaseEventSearchRepository  databaseEventSearchRepository;
    private final CategoryRepository categoryRepository;
    private final StatsClient statsClient;

    @Override
    public EventFullDto create(Long userId, NewEventDto eventDto) {
        isEventTimeValid(eventDto.getEventDate());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Category category = categoryRepository.findById(eventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Category not found"));

        Event event = EventMapper.mapToEvent(user, eventDto, category);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);
        event = eventRepository.save(event);

        List<Event> eventList = List.of(event);

        return this.mapToEventFullDto(eventList).getFirst();
    }

    @Override
    public EventFullDto get(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new BadRequestException("User is not the initiator");
        }

        List<Event> eventList = List.of(event);

        return this.mapToEventFullDto(eventList).getFirst();
    }

    @Override
    public List<EventFullDto> get(List<Long> users,
                                  List<EventState> states,
                                  List<Long> categories,
                                  LocalDateTime rangeStart,
                                  LocalDateTime rangeEnd,
                                  int from,
                                  int size) {
        Pageable page = PageRequest.of(from / size, size);

        List<Event> eventList = databaseEventSearchRepository.findForAdmin(users, states, categories, rangeStart, rangeEnd, page);

        return this.mapToEventFullDto(eventList);
    }

    @Override
    public EventFullDto getPublicEvent(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Event is not published");
        }

        List<Event> eventList = List.of(event);
        registerHit(request);

        return this.mapToEventFullDto(eventList).getFirst();
    }

    @Override
    public List<EventShortDto> getEvents(Long userId, int from, int size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Pageable page = PageRequest.of(from / size, size);

        List<Event> eventList = eventRepository.findByInitiatorId(userId, page);

        return this.mapToEventShortDto(eventList);
    }

    @Override
    public List<EventShortDto> getPublicEvents(String text,
                                               List<Long> categories,
                                               Boolean paid,
                                               LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd,
                                               Boolean onlyAvailable,
                                               EventSort sort,
                                               int from,
                                               int size,
                                               HttpServletRequest request) {
        Pageable page;
        if (sort != null) {
            Sort sortBy = null;
            if (sort.equals(EventSort.EVENT_DATE)) {
                sortBy = Sort.by(Sort.Direction.DESC, "eventDate");
            } else if (sort.equals(EventSort.VIEWS)) {
                sortBy = Sort.by(Sort.Direction.DESC, "views");
            }
            page = PageRequest.of(from / size, size, sortBy);
        } else {
            page = PageRequest.of(from / size, size);
        }
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new BadRequestException("Range start date is before end date");
        }
        registerHit(request);

        List<Event> eventList = databaseEventSearchRepository.findPublicEvents(text, categories, paid, rangeStart,
                rangeEnd,
                onlyAvailable, page);

        return this.mapToEventShortDto(eventList);
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

        if (updateEventUserRequest.hasCategory() &&
                !updatedEvent.getCategory().getId().equals(updateEventUserRequest.getCategory())) {
            Category category = categoryRepository.findById(updateEventUserRequest.getCategory())
                            .orElseThrow(() -> new NotFoundException("Category not found"));
            updatedEvent.setCategory(category);
        }

        isEventTimeValid(updatedEvent.getEventDate());
        updatedEvent = eventRepository.save(updatedEvent);


        List<Event> eventList = List.of(updatedEvent);

        return this.mapToEventFullDto(eventList).getFirst();
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

        if (updateEventAdminRequest.hasCategory() &&
                !updatedEvent.getCategory().getId().equals(updateEventAdminRequest.getCategory())) {
            Category category = categoryRepository.findById(updateEventAdminRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category not found"));
            updatedEvent.setCategory(category);
        }

        updatedEvent = eventRepository.save(updatedEvent);

        List<Event> eventList = List.of(updatedEvent);

        return this.mapToEventFullDto(eventList).getFirst();
    }

    private void isEventTimeValid(LocalDateTime eventTime) {
        if (eventTime.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Invalid event time");
        }
    }

    private void registerHit(HttpServletRequest request) {
        EndpointHitDto endpointHitDto = new EndpointHitDto();
        endpointHitDto.setApp("main-service");
        endpointHitDto.setUri(request.getRequestURI());
        endpointHitDto.setIp(request.getRemoteAddr());
        endpointHitDto.setTimestamp(LocalDateTime.now());
        statsClient.hit(endpointHitDto);
    }

    private Map<Long, Integer> getEventsViews(List<Event> eventList) {
        List<String> uris = eventList.stream()
                .map(e -> "/events/" + e.getId())
                .toList();

        LocalDateTime start = eventList.stream()
                .map(Event::getCreatedOn)
                .min(Comparator.naturalOrder())
                .orElse(null);
        LocalDateTime end = LocalDateTime.now();

        List<ViewStatsDto> stats = statsClient.getStats(start, end, uris, true);

        Map<Long, Integer> map = new HashMap<>();
        for (ViewStatsDto s : stats) {
            map.put(Long.parseLong(s.getUri().split("/")[2]), Long.valueOf(s.getHits()).intValue());
        }

        return map;
    }

    private List<EventFullDto> mapToEventFullDto(List<Event> eventList) {
        Map<Long, Integer> eventsViews = this.getEventsViews(eventList);

        return eventList.stream()
                .map(EventMapper::mapToEventFullDto)
                .peek(efo -> {
                    efo.setViews(eventsViews.getOrDefault(efo.getId(), 0).longValue());
                })
                .toList();
    }

    private List<EventShortDto> mapToEventShortDto(List<Event> eventList) {
        Map<Long, Integer> eventsViews = this.getEventsViews(eventList);

        return eventList.stream()
                .map(EventMapper::mapToEventShortDto)
                .peek(efo -> {
                    efo.setViews(eventsViews.getOrDefault(efo.getId(), 0).longValue());
                })
                .toList();
    }
}
