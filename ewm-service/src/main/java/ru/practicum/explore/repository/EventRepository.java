package ru.practicum.explore.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explore.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    Optional<Event> findByIdAndInitiatorId(Long id, Long userId);

    @Query("SELECT e FROM Event e " +
            "WHERE (LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) OR " +
            "LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) AND " +
            "(:categories IS NULL OR e.category.id IN (:categories)) AND " +
            "(:paid IS NULL OR e.paid = :paid) AND " +
            "e.eventDate BETWEEN :rangeStart AND :rangeEnd AND " +
            "e.state = 'PUBLISHED'")
    List<Event> findPublishedEventsWithFilters(
            @Param("text") String text,
            @Param("categories") List<Long> categories,
            @Param("paid") Boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd);

    Optional<Event> findByIdAndState(Long id, String state);

    @Query("SELECT e FROM Event e WHERE e.initiator.id = ?1")
    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    @Query("SELECT COUNT(r) FROM Request r WHERE r.event.id = ?1 AND r.status = 'CONFIRMED'")
    Long findConfirmedRequests(Long eventId);

    @Query("SELECT e FROM Event e " +
            "WHERE (:users IS NULL OR e.initiator.id IN (:users)) " +
            "AND (:states IS NULL OR e.state IN (:states)) " +
            "AND (:categories IS NULL OR e.category.id IN (:categories)) " +
            "AND (:rangeStart IS NULL OR e.eventDate >= :rangeStart) " +
            "AND (:rangeEnd IS NULL OR e.eventDate <= :rangeEnd)")
    List<Event> findByFilters(
            @Param("users") List<Long> users,
            @Param("states") List<String> states,
            @Param("categories") List<Long> categories,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            Pageable pageable);

    List<Event> findAllByIdIn(List<Long> list);

    @Query("select e from Event e where e.publishedOn is not null " +
            "and (?1 is null or lower(e.annotation) like %?1% or lower(e.description) like %?1%) " +
            "and (?2 is null or e.category in ?2) " +
            "and (?3 is null or e.paid = ?3) " +
            "and (?4 is null or ?4 = true or ?4 = false) " +
            "and (cast(?5 as date) is null or e.eventDate >= ?5) " +
            "and (cast(?6 as date) is null or e.eventDate <= ?6) " +
            "order by e.eventDate desc")
    List<Event> getEventsPublicOrderByEventDate(String text, List<Long> categories, Boolean paid, Boolean onlyAvailable, LocalDateTime dateFrom, LocalDateTime dateTo, Pageable pageable);

    @Query("select e from Event e where e.publishedOn is not null " +
            "and (?1 is null or lower(e.annotation) like %?1% or lower(e.description) like %?1%) " +
            "and (?2 is null or e.category in ?2) " +
            "and (?3 is null or e.paid = ?3) " +
            "and (?4 is null or ?4 = true or ?4 = false) " +
            "and (cast(?5 as date) is null or e.eventDate >= ?5) " +
            "and (cast(?6 as date) is null or e.eventDate <= ?6) " +
            "order by e.views desc")
    List<Event> getEventsPublicOrderByViews(String text, List<Long> categories, Boolean paid, Boolean onlyAvailable, LocalDateTime dateFrom, LocalDateTime dateTo, Pageable pageable);

    @Query("select e from Event e " +
            "where (?1 is null or e.initiator in ?1) " +
            "and (?2 is null or e.category in ?2) " +
            "and (?3 is null or e.state in ?3) " +
            "and (cast(?4 as date) is null or e.eventDate >= ?4) " +
            "and (cast(?5 as date) is null or e.eventDate <= ?5) " +
            "order by e.id desc")
    List<Event> getEventsAdmin(List<Long> users, List<Long> categories, List<String> states, LocalDateTime dateFrom, LocalDateTime dateTo, Pageable pageable);

    @Query("select e from Event e where e.initiator = ?1 order by e.eventDate desc")
    List<Event> getEventsPrivate(Long userId, Pageable pageable);

    @Query("select e from Event e where e.initiator = ?1 and e.id = ?2")
    Event getEventPrivate(Long userId, Long eventId);
}
