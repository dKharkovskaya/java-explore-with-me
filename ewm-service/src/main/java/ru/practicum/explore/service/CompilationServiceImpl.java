package ru.practicum.explore.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.dto.compilation.CompilationDto;
import ru.practicum.explore.dto.compilation.NewCompilationDto;
import ru.practicum.explore.dto.compilation.UpdateCompilationRequest;
import ru.practicum.explore.error.exception.NotFoundException;
import ru.practicum.explore.mapper.CompilationMapper;
import ru.practicum.explore.model.Compilation;
import ru.practicum.explore.model.Event;
import ru.practicum.explore.repository.CompilationRepository;
import ru.practicum.explore.repository.EventRepository;
import ru.practicum.explore.repository.RequestRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    @Override
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);
        List<Long> eventsIds = newCompilationDto.getEvents();
        if (eventsIds != null && !eventsIds.isEmpty()) {
            List<Event> events = eventRepository.findAllById(eventsIds);
            compilation.setEvents(events);
        } else {
            compilation.setEvents(new ArrayList<>());
        }
        compilation = compilationRepository.save(compilation);
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilation) {
        Compilation compilation = findCompilationByIdOrThrowNotFoundException(compId);
        if (updateCompilation.getEvents() != null) {
            List<Long> eventsIds = updateCompilation.getEvents();
            List<Event> events = eventRepository.findAllById(eventsIds);
            compilation.setEvents(events);
        }
        compilation = compilationRepository.save(compilation);
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        List<Compilation> compilationPage;
        if (pinned != null) {
            compilationPage = compilationRepository.findAllByPinned(pinned, pageable);
        } else {
            compilationPage = compilationRepository.findAll(pageable).getContent();
        }
        return compilationPage.stream()
                .map(CompilationMapper::toCompilationDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilationById(Long compilationId) {
        Compilation compilation = compilationRepository.findById(compilationId)
                .orElseThrow(() -> new NotFoundException("Не найдено"));
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    public void deleteCompilation(Long compilationId) {
        findCompilationByIdOrThrowNotFoundException(compilationId);
        compilationRepository.deleteById(compilationId);
    }


    private Compilation findCompilationByIdOrThrowNotFoundException(Long compilationId) {
        return compilationRepository.findById(compilationId)
                .orElseThrow(() -> new NotFoundException("Объект не найден"));
    }
}
