package ru.practicum.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.explore.StatsDtoInput;
import ru.practicum.explore.StatsDtoOutput;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.springframework.http.RequestEntity.post;

@Service
public class StatsClient {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RestTemplate restTemplate;

    public StatsClient(@Value("${stats.service.url}") String serviceUrl, RestTemplateBuilder builder) {
        this.restTemplate = builder.rootUri(serviceUrl).build();
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

    public ResponseEntity<Object> save(String app, String uri, String ip) {
        Map<String, Object> parameters = Map.of(
                "app", app,
                "uri", uri,
                "ip", ip,
                "timestamp", LocalDateTime.now().format(formatter)
        );
        return (ResponseEntity<Object>) post("/hit", parameters);
    }
}
