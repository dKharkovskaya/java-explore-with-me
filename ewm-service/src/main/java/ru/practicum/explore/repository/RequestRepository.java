package ru.practicum.explore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.explore.model.Request;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    boolean existsByRequesterIdAndEventId(Long userId, Long eventId);

    /**
     * Находит все заявки пользователя
     */
    List<Request> findAllByRequesterId(Long userId);

    /**
     * Находит заявки по списку ID
     */
    List<Request> findAllByIdIn(List<Long> ids);

    /**
     * Подсчитывает количество подтвержденных заявок на участие в событии
     */
    int countByEventIdAndStatus(Long eventId, String status);
}
