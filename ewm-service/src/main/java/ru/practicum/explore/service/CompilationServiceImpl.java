package ru.practicum.explore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.dto.compilation.CompilationDto;
import ru.practicum.explore.dto.compilation.NewCompilationDto;
import ru.practicum.explore.dto.compilation.UpdateCompilationDto;
import ru.practicum.explore.error.exception.NotFoundException;
import ru.practicum.explore.mapper.CompilationMapper;
import ru.practicum.explore.model.Compilation;
import ru.practicum.explore.model.Event;
import ru.practicum.explore.repository.CompilationRepository;
import ru.practicum.explore.repository.EventRepository;

import java.util.*;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto createCompilation(NewCompilationDto compilationDto) {
        Compilation compilation = CompilationMapper.toCompilation(compilationDto);
        List<Long> eventsIds = compilationDto.getEvents();
        if (eventsIds != null && !eventsIds.isEmpty()) {
            List<Event> events = eventRepository.findAllById(eventsIds);
            compilation.setEvents(events);
        } else {
            compilation.setEvents(new ArrayList<>());
        }
        compilation = compilationRepository.save(compilation);
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Transactional
    @Override
    public CompilationDto updateCompilation(UpdateCompilationDto updateCompilationDto, Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow();
        if (!isNull(updateCompilationDto.getEvents())) {
            compilation.setEvents(eventRepository.findAllById(updateCompilationDto.getEvents()));
        }
        if (updateCompilationDto.getPinned() != null) {
            compilation.setPinned(updateCompilationDto.getPinned());
        }

        if (updateCompilationDto.getTitle() != null && !updateCompilationDto.getTitle().isBlank()) {
            compilation.setTitle(updateCompilationDto.getTitle());
        }
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Transactional
    @Override
    public void deleteCompilation(Long compId) {
        boolean exists = compilationRepository.existsById(compId);
        if (!exists) {
            throw new NotFoundException();
        }
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow();
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    public List<CompilationDto> getAllEvents(Boolean pinned, Integer from, Integer size) {
        List<Compilation> all = compilationRepository.findAll();
        List<CompilationDto> result = all.stream()
                .filter(compilation -> {
                    if (isNull(pinned)) {
                        return true;
                    } else if (isNull(compilation.getPinned())) {
                        return false;
                    }
                    return compilation.getPinned().equals(pinned);
                })
                .skip(from)
                .limit(size)
                .map(CompilationMapper::toCompilationDto)
                .toList();
        return result;
    }
}
