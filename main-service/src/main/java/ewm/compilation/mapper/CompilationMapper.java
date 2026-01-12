package ewm.compilation.mapper;

import ewm.compilation.dto.CompilationDto;
import ewm.compilation.model.Compilation;
import ewm.event.mapper.EventMapper;

import java.util.stream.Collectors;

public class CompilationMapper {

    public static CompilationDto toDto(Compilation c) {
        return new CompilationDto(
                c.getId(),
                c.getTitle(),
                c.getPinned(),
                c.getEvents().stream()
                        .map(EventMapper::mapToEventShortDto)
                        .collect(Collectors.toSet())
        );
    }

}
