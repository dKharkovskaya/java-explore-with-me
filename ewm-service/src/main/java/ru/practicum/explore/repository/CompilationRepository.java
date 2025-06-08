package ru.practicum.explore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore.model.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    Page<Compilation> findByPinned(boolean pinned, Pageable pageable);

    List<Compilation> findByTitleIgnoreCase(String title);
}
