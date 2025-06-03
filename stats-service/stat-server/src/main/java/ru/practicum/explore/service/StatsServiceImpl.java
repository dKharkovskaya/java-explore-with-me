package ru.practicum.explore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explore.StatsDtoInput;
import ru.practicum.explore.StatsDtoOutput;
import ru.practicum.explore.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    public StatsDtoInput hit(StatsDtoInput statsDtoInput) {
        return statsRepository.save(statsDtoInput);
    }

    @Override
    public List<StatsDtoOutput> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        List<StatsDtoOutput> options = new ArrayList<>();
        if (uris == null || uris.isEmpty()) {
            if (unique) {
                options = statsRepository.findAllUniqueId(start, end);
            } else {
                statsRepository.findAllNotUniqueId(start, end);
            }
        } else {
            for (String uri : uris) {
                Optional<StatsDtoOutput> stat;
                if (unique) {
                    stat = statsRepository.findByUriAndUniqueId(uri,
                            start, end);
                } else {
                    stat = statsRepository.findByUriAndNotUniqueId(uri,
                            start, end);
                }
                if (stat.isPresent()) {
                    options.add(stat.get());
                }
            }
            if (!options.isEmpty()) {
                options = options.stream()
                        .sorted(Comparator.comparing(StatsDtoOutput::getHits)
                                .reversed())
                        .collect(Collectors.toList());
            }
        }
        return options;
    }
}
