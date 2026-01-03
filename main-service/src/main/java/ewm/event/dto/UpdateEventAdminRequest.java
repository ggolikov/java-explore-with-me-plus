package ewm.event.dto;

import ewm.common.dto.Location;
import lombok.Data;

@Data
public class UpdateEventAdminRequest {
    private String title;
    private String annotation;
    private String description;
    private Long category;
    private String eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private String stateAction; // PUBLISH_EVENT | REJECT_EVENT
}