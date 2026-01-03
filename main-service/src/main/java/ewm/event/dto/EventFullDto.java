package ewm.event.dto;

import ewm.category.dto.CategoryDto;
import ewm.common.dto.Location;
import ewm.user.dto.UserShortDto;
import lombok.Data;

@Data
public class EventFullDto {
    private Long id;
    private String title;
    private String annotation;
    private String description;
    private CategoryDto category;
    private String createdOn;
    private String eventDate;
    private String publishedOn;
    private UserShortDto initiator;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private String state;
    private Long confirmedRequests;
    private Long views;
}