package ru.practicum.explore.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.dto.compilation.CompilationDto;
import ru.practicum.explore.dto.compilation.NewCompilationDto;
import ru.practicum.explore.dto.compilation.UpdateCompilationRequest;
import ru.practicum.explore.error.exception.ConflictException;
import ru.practicum.explore.error.exception.NotFoundException;
import ru.practicum.explore.mapper.CompilationMapper;
import ru.practicum.explore.model.Compilation;
import ru.practicum.explore.model.Event;
import ru.practicum.explore.repository.CompilationRepository;
import ru.practicum.explore.repository.EventRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto createCompilation(NewCompilationDto dto) {
        if (!compilationRepository.findByTitleIgnoreCase(dto.getTitle()).isEmpty()) {
            throw new NotFoundException("Подборка с названием " + dto.getTitle() + " уже существует");
        }
        Set<Event> events = new HashSet<>();
        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
            events = new HashSet<>(eventRepository.findAllByIdIn(dto.getEvents().stream().toList()));
        }

        Compilation compilation = CompilationMapper.toCompilation(dto, events);
        compilation = compilationRepository.save(compilation);
        return CompilationMapper.fromCompilation(compilation);
    }

    @Override
    public void deleteCompilation(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Compilation with id=" + compId + " not found");
        }
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest dto) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() -> new NotFoundException("Подборка с идентификатором " + compId + " не найдена"));

        if (dto.getPinned() != null) {
            compilation.setPinned(dto.getPinned());
        }

        if (dto.getTitle() != null) {
            if (!compilationRepository.findByTitleIgnoreCase(dto.getTitle()).isEmpty()) {
                throw new ConflictException("Подборка с названием " + dto.getTitle() + " уже существует");
            }
            if (compilation.getTitle().equalsIgnoreCase(dto.getTitle())) {
                throw new ConflictException("Подборка с названием " + dto.getTitle() + " уже существует");
            }
            compilation.setTitle(dto.getTitle());
        }

        if (dto.getEvents() != null) {
            Set<Event> events = new HashSet<>();
            if (!dto.getEvents().isEmpty()) {
                events = new HashSet<>(eventRepository.findAllByIdIn(dto.getEvents().stream().toList()));
            }
            compilation.setEvents(events);
        }

        return CompilationMapper.fromCompilation(compilationRepository.save(compilation));
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));
        Page<Compilation> compilations = pinned == null ? compilationRepository.findAll(pageable) : compilationRepository.findByPinned(pinned, pageable);
        return compilations.stream().map(CompilationMapper::fromCompilation).toList();
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        return CompilationMapper.fromCompilation(compilationRepository.findById(compId).orElseThrow(() -> new NotFoundException("Подборка не найдена")));
    }

    // Вспомогательный метод
    private Compilation getCompilationIfExists(Long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " not found"));
    }
}
