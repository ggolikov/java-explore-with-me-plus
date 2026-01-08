package ewm.common.error;

import lombok.Value;

@Value
public class ErrorResponse {
    String error;
    String description;
}
