package ru.practicum.explore.mapper;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.explore.dto.compilation.CompilationDto;
import ru.practicum.explore.dto.compilation.NewCompilationDto;
import ru.practicum.explore.model.Compilation;

import static java.util.Objects.isNull;


@Slf4j
@UtilityClass
public class CompilationMapper {

    public static CompilationDto toCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .events(!isNull(compilation.getEvents()) ? compilation.getEvents().stream()
                        .map(EventMapper::toEventShortDto)
                        .toList() : null)
                .title(compilation.getTitle())
                .pinned(!isNull(compilation.getPinned()) && compilation.getPinned())
                .build();
    }

    public static Compilation toCompilation(NewCompilationDto newCompilationDto) {
        return Compilation.builder()
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.getPinned())
                .build();
    }

}