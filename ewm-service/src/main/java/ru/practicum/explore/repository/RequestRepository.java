package ru.practicum.explore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.explore.enums.RequestState;
import ru.practicum.explore.model.Request;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByRequesterId(Long requesterId);

    List<Request> findAllByEventId(Long eventId);

    long countByEventIdAndStatus(Long eventId, RequestState status);

    Boolean existsByRequesterIdAndEventId(Long userId, Long eventId);

}
