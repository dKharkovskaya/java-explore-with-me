package ru.practicum.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.explore.StatsDtoInput;
import ru.practicum.explore.StatsDtoOutput;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StatsClient {

    private final RestTemplate restTemplate;

    @Value("${stats-server.url}")
    private String statsServerUrl;

    @Autowired
    public StatsClient(
            @Value("${stats-server.url}") String serviceUrl,
            RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void hit(StatsDtoInput dto) {
        HttpEntity<StatsDtoInput> requestEntity = new HttpEntity<>(dto);
        restTemplate.postForObject(statsServerUrl + "/hit", requestEntity, String.class);
    }

    // Получает статистику за период
    public List<StatsDtoOutput> getStats(LocalDateTime start, LocalDateTime end,
                                         List<String> uris, Boolean unique) {
        String uri = "/stats?start={start}&end={end}&unique={unique}";

        if (uris != null && !uris.isEmpty()) {
            StringBuilder sb = new StringBuilder(uri);
            for (int i = 0; i < uris.size(); i++) {
                sb.append("&uris=").append(uris.get(i));
            }
            uri = sb.toString();
        }

        return Arrays.asList(Objects.requireNonNull(restTemplate.getForObject(
                uri,
                StatsDtoOutput[].class,
                start.format(DateTimeFormatter.ISO_DATE_TIME),
                end.format(DateTimeFormatter.ISO_DATE_TIME),
                unique
        )));
    }
}
