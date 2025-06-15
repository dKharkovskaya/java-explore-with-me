package ru.practicum.explore.error.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.explore.error.exception.BadRequest;
import ru.practicum.explore.error.model.ErrorResp;

@RestControllerAdvice
public class Error {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResp handleNotFoundException(final BadRequest e) {
        return new ErrorResp(e.getMessage());
    }
}
