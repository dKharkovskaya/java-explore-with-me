package ru.practicum.explore.dto.event;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explore.enums.RequestState;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventStatusUpdateRequest {
    @NotNull
    private List<Long> requestIds;
    @NotNull
    private RequestState status;
}
