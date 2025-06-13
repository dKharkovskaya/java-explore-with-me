package ru.practicum.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.explore.StatsDtoInput;
import ru.practicum.explore.StatsDtoOutput;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsClient {

    private final RestTemplate restTemplate;

    private final String statsServerUrl;

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serviceUrl, RestTemplateBuilder builder) {
        this.statsServerUrl = serviceUrl;
        this.restTemplate = builder.rootUri(serviceUrl).build();
    }

    // Отправляет информацию о посещении
    public void hit(StatsDtoInput dto) {
        HttpEntity<StatsDtoInput> requestEntity = new HttpEntity<>(dto);
        restTemplate.exchange(statsServerUrl + "/hit", HttpMethod.POST, requestEntity, String.class).getBody();
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
                start,
                end,
                unique
        );

        return Arrays.asList(response.getBody());
    }
}
