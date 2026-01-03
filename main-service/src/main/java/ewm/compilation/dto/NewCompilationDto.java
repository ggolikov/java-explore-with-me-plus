package ewm.compilation.dto;

import lombok.Data;
import java.util.List;

@Data
public class NewCompilationDto {
    private String title;
    private Boolean pinned;
    private List<Long> events;
}