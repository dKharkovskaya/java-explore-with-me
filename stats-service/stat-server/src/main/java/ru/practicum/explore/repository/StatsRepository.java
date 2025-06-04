package ru.practicum.explore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explore.StatsDtoOutput;
import ru.practicum.explore.model.Stats;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StatsRepository extends JpaRepository<Stats, Long> {

    @Query("SELECT new ru.practicum.explore.StatsDtoOutput(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM Stats s " +
            "WHERE s.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(DISTINCT s.ip) DESC")
    List<StatsDtoOutput> findAllUniqueId(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.explore.StatsDtoOutput(s.app, s.uri, COUNT(s.ip)) " +
            "FROM Stats s " +
            "WHERE s.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip) DESC")
    List<StatsDtoOutput> findAllNotUniqueId(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.explore.StatsDtoOutput(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM Stats s " +
            "WHERE s.uri = ?1 AND s.timestamp BETWEEN ?2 AND ?3 " +
            "GROUP BY s.app, s.uri")
    Optional<StatsDtoOutput> findByUriAndUniqueId(String uri, LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.explore.StatsDtoOutput(s.app, s.uri, COUNT(s.ip)) " +
            "FROM Stats s " +
            "WHERE s.uri = ?1 AND s.timestamp BETWEEN ?2 AND ?3 " +
            "GROUP BY s.app, s.uri")
    Optional<StatsDtoOutput> findByUriAndNotUniqueId(String uri, LocalDateTime start, LocalDateTime end);
}
