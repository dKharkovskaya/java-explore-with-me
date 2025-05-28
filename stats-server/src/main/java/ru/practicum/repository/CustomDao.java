package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Stats;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomDao  {
    List<Stats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
