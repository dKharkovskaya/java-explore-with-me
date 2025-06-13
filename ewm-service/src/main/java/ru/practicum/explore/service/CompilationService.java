package ru.practicum.explore.service;

import ru.practicum.explore.dto.compilation.CompilationDto;
import ru.practicum.explore.dto.compilation.NewCompilationDto;
import ru.practicum.explore.dto.compilation.UpdateCompilationDto;

import java.util.List;

public interface CompilationService {

    CompilationDto createCompilation(NewCompilationDto compilationDto);

    CompilationDto updateCompilation(UpdateCompilationDto updateCompilationDto, Long compId);

    void deleteCompilation(Long compId);

    CompilationDto getCompilationById(Long compId);

    List<CompilationDto> getAllEvents(Boolean pinned, Integer from, Integer size);

}
