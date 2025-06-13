package ru.practicum.explore.error.exception;


public class BadRequest extends RuntimeException {
    public BadRequest(String message) {
        super(message);
    }
}
