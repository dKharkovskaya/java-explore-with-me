package ru.practicum.explore.model;

import lombok.*;

import javax.persistence.Embeddable;
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
