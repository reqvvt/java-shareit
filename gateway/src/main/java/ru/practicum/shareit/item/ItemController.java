package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.markers.Create;
import ru.practicum.shareit.markers.Update;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/items")
@Validated
public class ItemController {
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader(X_SHARER_USER_ID) Integer userId,
                                         @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                         @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("getAll {}, from={}, size={}", userId, from, size);
        return itemClient.getAll(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@RequestHeader(X_SHARER_USER_ID) Integer userId,
                                          @PathVariable Integer itemId) {
        log.info("getById {}, itemId={}", userId, itemId);
        return itemClient.getById(userId, itemId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Validated({Create.class}) @RequestBody ItemDto itemDto,
                           @RequestHeader(X_SHARER_USER_ID) Integer userId) {
        log.info("create {}, userId={}", itemDto, userId);
        return itemClient.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> edit(@Validated({Update.class}) @RequestBody ItemDto itemDto,
                              @RequestHeader(X_SHARER_USER_ID) Integer userId, @PathVariable Integer itemId) {
        log.info("edit {}, userId={}, itemId={}", itemDto, userId, itemId);
        return itemClient.edit(itemDto, userId, itemId);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> delete(@PathVariable Integer itemId) {
        log.info("delete {}", itemId);
        return itemClient.delete(itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text, @RequestHeader(X_SHARER_USER_ID) Integer userId,
                                    @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                    @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("search {}, userId={}, from={}, size={}", text, userId, from, size);
        return itemClient.search(text, userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(X_SHARER_USER_ID) Integer userId,
                                             @PathVariable Integer itemId, @Valid @NotNull @RequestBody CommentDto commentDto) {
        log.info("comment {}, itemId={}, commentDto={}", userId, itemId, commentDto);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
