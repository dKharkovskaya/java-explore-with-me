package ru.practicum.explore.error.model;

import lombok.Getter;

@Getter
public class ErrorResp {
    private final String error;

    public ErrorResp(String error) {
        this.error = error;
    }
}
