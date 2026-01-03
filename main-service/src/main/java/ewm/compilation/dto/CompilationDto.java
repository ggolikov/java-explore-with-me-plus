package ewm.compilation.dto;

import ewm.event.dto.EventShortDto;
import lombok.Data;
import java.util.List;

@Data
public class CompilationDto {
    private Long id;
    private String title;
    private Boolean pinned;
    private List<EventShortDto> events;
}