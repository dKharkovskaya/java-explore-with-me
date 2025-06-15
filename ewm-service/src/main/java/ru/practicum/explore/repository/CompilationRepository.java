package ru.practicum.explore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.explore.model.Compilation;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long> {
}
