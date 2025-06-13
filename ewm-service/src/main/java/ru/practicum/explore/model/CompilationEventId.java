package ru.practicum.explore.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CompilationEventId implements Serializable {
    private Long eventId;
    private Long compilationId;
}
