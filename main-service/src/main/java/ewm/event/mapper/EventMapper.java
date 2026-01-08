package ewm.event.mapper;

import ewm.category.model.Category;
import ewm.common.dto.LocationDto;
import ewm.common.model.Location;
import ewm.event.dto.EventFullDto;
import ewm.event.dto.EventShortDto;
import ewm.event.dto.NewEventDto;
import ewm.event.dto.UpdateEventUserRequest;
import ewm.event.model.Event;
import ewm.event.model.EventState;
import ewm.event.model.EventStateAction;
import ewm.user.mapper.UserMapper;
import ewm.user.model.User;

public class EventMapper {
    public static Event mapToEvent(User initiator,
                                   NewEventDto eventDto,
                                   Category category) {
        Event event = new Event();
        event.setTitle(eventDto.getTitle());
        event.setAnnotation(eventDto.getAnnotation());
        event.setDescription(eventDto.getDescription());

        // TODO Category
        event.setCategory(category);

        event.setEventDate(eventDto.getEventDate());
        Location location = new Location();
        location.setLat(eventDto.getLocation().getLat());
        location.setLon(eventDto.getLocation().getLon());
        event.setLocation(location);
        event.setPaid(eventDto.getPaid() != null ? eventDto.getPaid() : false);
        event.setParticipantLimit(eventDto.getParticipantLimit() != null ? eventDto.getParticipantLimit() : 0);
        event.setRequestModeration(eventDto.getRequestModeration() != null ? eventDto.getRequestModeration() : true);
        return event;
    }

    public static EventFullDto mapToEventFullDto(Event event) {
        EventFullDto eventFullDto = new EventFullDto();
        eventFullDto.setId(event.getId());
        eventFullDto.setTitle(event.getTitle());
        eventFullDto.setAnnotation(event.getAnnotation());
        eventFullDto.setDescription(event.getDescription());

        /* TODO Category
        eventFullDto.setCategory(null); */

        eventFullDto.setCreatedOn(String.valueOf(event.getCreatedOn()));
        eventFullDto.setEventDate(String.valueOf(event.getEventDate()));
        eventFullDto.setPublishedOn(String.valueOf(event.getPublishedOn()));
        eventFullDto.setInitiator(UserMapper.toShortDto(event.getInitiator()));
        LocationDto locationDto = new LocationDto();
        locationDto.setLat(event.getLocation().getLat());
        locationDto.setLon(event.getLocation().getLon());
        eventFullDto.setLocation(locationDto);
        eventFullDto.setPaid(event.getPaid());
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
        eventFullDto.setRequestModeration(event.getRequestModeration());
        eventFullDto.setState(String.valueOf(event.getState()));

        /* TODO Request
        eventFullDto.setConfirmedRequests(null); */

        // FIXME use stat-svc
        event.setViews(0L);
        return eventFullDto;
    }

    public static EventShortDto mapToEventShortDto(Event event) {
        EventShortDto eventShortDto = new EventShortDto();
        eventShortDto.setId(event.getId());
        eventShortDto.setTitle(event.getTitle());
        eventShortDto.setAnnotation(event.getAnnotation());

        /* TODO Category
        eventShortDto.setCategory(null); */

        /* TODO Request
        eventShortDto.setConfirmedRequests(null); */

        eventShortDto.setEventDate(String.valueOf(event.getEventDate()));
        eventShortDto.setInitiator(UserMapper.toShortDto(event.getInitiator()));
        eventShortDto.setPaid(event.getPaid());

        // FIXME use stat-svc
        eventShortDto.setViews(0L);

        return eventShortDto;
    }

    public static Event updateEvent(Event event, UpdateEventUserRequest updateEventUserRequest) {
        if (updateEventUserRequest.hasTitle()) {
            event.setTitle(updateEventUserRequest.getTitle());
        }
        if (updateEventUserRequest.hasAnnotation()) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.hasDescription()) {
            event.setDescription(updateEventUserRequest.getDescription());
        }

        /* TODO Category
        if (updateEventUserRequest.hasCategory()) {} */

        if (updateEventUserRequest.hasEventDate()) {
            event.setEventDate(updateEventUserRequest.getEventDate());
        }

        if (updateEventUserRequest.hasLocation()) {
            Location location = new Location();
            location.setLat(updateEventUserRequest.getLocation().getLat());
            location.setLon(updateEventUserRequest.getLocation().getLon());
            event.setLocation(location);
        }
        if (updateEventUserRequest.hasPaid()) {
            event.setPaid(updateEventUserRequest.getPaid());
        }
        if (updateEventUserRequest.hasParticipantLimit()) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }
        if (updateEventUserRequest.hasRequestModeration()) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }
        if (updateEventUserRequest.hasStateAction()) {
            EventState eventState = updateEventUserRequest.getStateAction() == EventStateAction.SEND_TO_REVIEW
                    ? EventState.PENDING : EventState.CANCELED;
            event.setState(eventState);
        }
        return event;
    }
}
