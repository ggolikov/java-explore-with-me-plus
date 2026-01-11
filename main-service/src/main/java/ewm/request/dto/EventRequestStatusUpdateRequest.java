package ewm.request.dto;

import lombok.*;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestStatusUpdateRequest {

    @NotEmpty
    private List<Long> requestIds;

    @NotNull
    private RequestUpdateStatus status; // CONFIRMED / REJECTED

    public enum RequestUpdateStatus {
        CONFIRMED, REJECTED
    }
}