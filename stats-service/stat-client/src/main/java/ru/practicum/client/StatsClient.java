package ru.practicum.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.explore.StatsDtoInput;
import ru.practicum.explore.StatsDtoOutput;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class StatsClient {

    private final RestTemplate restTemplate;

    public StatsClient(@Value("${stats.service.url}") String serviceUrl, RestTemplateBuilder builder) {
        this.restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serviceUrl))
                .build();
    }

    // Отправляет информацию о посещении
    public void hit(StatsDtoInput dto) {
        restTemplate.postForObject("/hit", dto, Void.class);
    }

    // Получает статистику за период
    public List<StatsDtoOutput> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        String uri = "/stats?start={start}&end={end}&unique={unique}";

        if (uris != null && !uris.isEmpty()) {
            StringBuilder sb = new StringBuilder(uri);
            for (int i = 0; i < uris.size(); i++) {
                sb.append("&uris=").append(uris.get(i));
            }
            uri = sb.toString();
        }

        ResponseEntity<StatsDtoOutput[]> response = restTemplate.getForEntity(
                uri,
                StatsDtoOutput[].class,
                start.format(StatsDtoInput.DATE_TIME_FORMATTER),
                end.format(StatsDtoInput.DATE_TIME_FORMATTER),
                unique
        );

        return Arrays.asList(response.getBody());
    }
}
