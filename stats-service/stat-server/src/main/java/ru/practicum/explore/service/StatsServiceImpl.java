package ru.practicum.explore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.StatsDtoOutput;
import ru.practicum.explore.model.Stats;
import ru.practicum.explore.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
//@RequiredArgsConstructor
@Transactional
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    public StatsServiceImpl(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    @Override
    public Stats hit(Stats stats) {
        return statsRepository.save(stats);
    }

    @Override
    public List<StatsDtoOutput> getStatsUnique(LocalDateTime start, LocalDateTime end) {
        return statsRepository.findAllUniqueId(start, end);
    }

    @Override
    public List<StatsDtoOutput> getStatsAll(LocalDateTime start, LocalDateTime end) {
        return statsRepository.findAllNotUniqueId(start, end);
    }

    @Override
    public Optional<StatsDtoOutput> getStatsByUri(LocalDateTime start, LocalDateTime end, String uri, Boolean unique) {
        return unique ?
                statsRepository.findByUriAndUniqueId(uri, start, end) :
                statsRepository.findByUriAndNotUniqueId(uri, start, end);
    }
}
