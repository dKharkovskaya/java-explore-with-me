package ru.practicum.explore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.explore.enums.State;
import ru.practicum.explore.model.Event;
import ru.practicum.explore.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiator(User user);

    @Query("SELECT event FROM Event event " +
            "WHERE (:users IS NULL OR event.initiator.id IN :users) " +
            "AND (:states IS NULL OR event.state IN :states) " +
            "AND (:categories IS NULL OR event.category.id IN :categories) " +
            "AND (:rangeStart IS NULL OR event.eventDate >= :rangeStart) " +
            "AND (:rangeEnd IS NULL OR event.eventDate <= :rangeEnd)")
    List<Event> getEventsWithFilter(List<Long> users, List<String> states, List<Long> categories,
                                    LocalDateTime rangeStart, LocalDateTime rangeEnd);

    @Query("SELECT event FROM Event event " +
            "WHERE event.state= :state " +
            "AND (:text IS NULL OR (LOWER(event.description) LIKE %:text% OR LOWER(event.annotation) LIKE %:text%)) " +
            "AND (:paid IS NULL OR event.paid = :paid) " +
            "AND (:rangeStart IS NULL OR event.eventDate >= :rangeStart) " +
            "AND (:rangeEnd IS NULL OR event.eventDate <= :rangeEnd) " +
            "ORDER BY  event.eventDate")
    List<Event> getPublicEventsWithFilter(State state, String text, Boolean paid, LocalDateTime rangeStart,
                                          LocalDateTime rangeEnd);

}
