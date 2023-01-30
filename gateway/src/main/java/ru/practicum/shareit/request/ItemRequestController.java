package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.markers.Create;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(X_SHARER_USER_ID) Integer userId,
                                         @Validated(Create.class) @RequestBody ItemRequestDto itemRequestDto) {
        log.info("create {}, itemRequestDto={}", userId, itemRequestDto);
        return requestClient.create(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUser(@RequestHeader(X_SHARER_USER_ID) Integer userId) {
        log.info("getAllByUser {}", userId);
        return requestClient.getAllByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllOtherUser(@RequestHeader(X_SHARER_USER_ID) Integer userId,
                                                        @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                        @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("getAllOtherUser {}, from={}, size={}", userId, from, size);
        return requestClient.getAllOtherUser(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader(X_SHARER_USER_ID) Integer userId, @PathVariable Integer requestId) {
        log.info("getById {}, requestId={}", userId, requestId);
        return requestClient.getById(userId, requestId);
    }
}
