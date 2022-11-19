package ru.practicum.shareit.exception;

public class MessageFailedException extends IllegalArgumentException {

    public MessageFailedException(String message) {
        super(message);
    }
}
