package ru.practicum.explore.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    List<Event> getEventsWithFilter(
            @Param("users") List<Long> users,
            @Param("states") List<String> states,
            @Param("categories") List<Long> categories,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd);

    @Query("SELECT event FROM Event event " +
            "WHERE event.state= :state " +
            "AND (:text IS NULL OR (LOWER(event.description) LIKE %:text% OR LOWER(event.annotation) LIKE %:text%)) " +
            "AND (:paid IS NULL OR event.paid = :paid) " +
            "AND (:rangeStart IS NULL OR event.eventDate >= :rangeStart) " +
            "AND (:rangeEnd IS NULL OR event.eventDate <= :rangeEnd) " +
            "ORDER BY  event.eventDate")
    List<Event> getPublicEventsWithFilter(State state, String text, Boolean paid, LocalDateTime rangeStart,
                                          LocalDateTime rangeEnd);

    @Query(value = "select * from events as e where " +
            "(cast(:users as BIGINT) is null or e.initiator_id in (cast(:users as BIGINT))) " +
            "and (cast(:states as TEXT) is null or e.state in (cast(:states as TEXT))) " +
            "and (cast(:categories as BIGINT) is null or e.category_id in (cast(:categories as BIGINT))) " +
            "and (cast(:rangeStart as TIMESTAMP) is null or e.event_date >= cast(:rangeStart as TIMESTAMP)) " +
            "and (cast(:rangeEnd as TIMESTAMP) is null or e.event_date <= cast(:rangeEnd  as TIMESTAMP)) ", nativeQuery = true)
    List<Event> getAllEventsByAdmin(@Param("users") List<Long> users,
                                    @Param("states") List<String> states,
                                    @Param("categories") List<Long> categories,
                                    @Param("rangeStart") LocalDateTime rangeStart,
                                    @Param("rangeEnd") LocalDateTime rangeEnd,
                                    Pageable pageable);

    @Query(value = "select * from events as e where e.state = 'PUBLISHED' " +
            "and (cast(:text as TEXT) is null or e.annotation ilike concat('%',cast(:text as TEXT),'%') " +
            "or e.description ilike concat('%',cast(:text as TEXT),'%')) " +
            "and (cast(:categories as BIGINT) is null or e.category_id in (cast(:categories as BIGINT))) " +
            "and (cast(:paid as BOOLEAN) is null or e.paid = cast(:paid as BOOLEAN)) " +
            "and (e.event_date between cast(:rangeStart as TIMESTAMP) and cast(:rangeEnd as TIMESTAMP))", nativeQuery = true)
    List<Event> getPublicEvents(@Param("text") String text,
                                @Param("categories") List<Long> categories,
                                @Param("paid") Boolean paid,
                                @Param("rangeStart") LocalDateTime rangeStart,
                                @Param("rangeEnd") LocalDateTime rangeEnd,
                                Pageable pageable);

    boolean existsByCategoryId(Long categoryId);

}
