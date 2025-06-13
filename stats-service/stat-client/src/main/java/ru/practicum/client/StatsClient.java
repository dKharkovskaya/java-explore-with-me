package ru.practicum.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.explore.StatsDtoInput;
import ru.practicum.explore.StatsDtoOutput;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsClient {

    private final RestTemplate restTemplate;

    @Value("${stats-server.url}")
    private String statsServerUrl;

    public void hit(StatsDtoInput dto) {
        HttpEntity<StatsDtoInput> entity = new HttpEntity<>(dto);
        restTemplate.postForObject(statsServerUrl + "/hit", entity, String.class);
    }

    public List<StatsDtoOutput> getStats(LocalDateTime start, LocalDateTime end,
                                         List<String> uris, Boolean unique) {
        String uri = "http://localhost:9090/stats";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri)
                .queryParam("start", "{start}")
                .queryParam("end", "{end}")
                .queryParam("unique", "{unique}");

        if (uris != null && !uris.isEmpty()) {
            for (String u : uris) {
                builder.queryParam("uris", u);
            }
        }

        ResponseEntity<StatsDtoOutput[]> response = restTemplate.getForEntity(
                builder.build().toUriString(), // <-- теперь это абсолютный URL
                StatsDtoOutput[].class,
                start.format(StatsDtoInput.DATE_TIME_FORMATTER),
                end.format(StatsDtoInput.DATE_TIME_FORMATTER),
                unique
        );

        return Arrays.asList(response.getBody());
    }
}