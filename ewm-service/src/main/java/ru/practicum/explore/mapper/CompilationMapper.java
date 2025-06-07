package ru.practicum.explore.mapper;

import ru.practicum.explore.dto.compilation.CompilationDto;
import ru.practicum.explore.dto.compilation.NewCompilationDto;
import ru.practicum.explore.dto.compilation.UpdateCompilationRequest;
import ru.practicum.explore.dto.event.EventShortDto;
import ru.practicum.explore.model.Compilation;
import ru.practicum.explore.model.Event;
import ru.practicum.explore.repository.EventRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CompilationMapper {
    private final EventRepository eventRepository;

    public CompilationMapper(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public static Compilation toCompilation(NewCompilationDto dto) {
        return Compilation.builder()
                .title(dto.getTitle())
                .pinned(dto.getPinned())
                .build();
    }

    public static CompilationDto toDto(Compilation compilation) {
        List<EventShortDto> events = compilation.getEvents().stream()
                .map(event -> EventShortDto.builder()
                        .id(event.getId())
                        .title(event.getTitle())
                        .annotation(event.getAnnotation())
                        .paid(event.getPaid())
                        .eventDate(event.getEventDate())
                        .confirmedRequests(event.getConfirmedRequests())
                        .views(event.getViews())
                        .initiator(event.getInitiator() != null ? ru.practicum.explore.dto.user.UserShortDto.builder()
                                .id(event.getInitiator().getId())
                                .name(event.getInitiator().getName())
                                .build() : null)
                        .category(event.getCategory() != null ? ru.practicum.explore.dto.category.CategoryDto.builder()
                                .id(event.getCategory().getId())
                                .name(event.getCategory().getName())
                                .build() : null)
                        .build())
                .collect(Collectors.toList());

        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .events((Set<EventShortDto>) events)
                .build();
    }

    public static void updateCompilationFromDto(Compilation compilation, UpdateCompilationRequest dto, EventRepository eventRepository) {
        if (dto.getTitle() != null) {
            compilation.setTitle(dto.getTitle());
        }

        if (dto.getPinned() != null) {
            compilation.setPinned(dto.getPinned());
        }

        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
            Set<Event> events = new HashSet<>(eventRepository.findAllById(dto.getEvents()));
            compilation.setEvents(events);
        }
    }
}