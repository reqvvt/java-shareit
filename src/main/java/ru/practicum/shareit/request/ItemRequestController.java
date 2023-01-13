package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.markers.Create;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader(X_SHARER_USER_ID) Integer userId,
                                 @Validated(Create.class) @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Вызван метод create() в ItemRequestController");
        return itemRequestService.create(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDtoOut> getAllByRequester(@RequestHeader(X_SHARER_USER_ID) Integer userId) {
        log.info("Вызван метод getAllByUser() в ItemRequestController");
        return itemRequestService.getAll(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoOut> getAllByOtherUsers(@RequestHeader(X_SHARER_USER_ID) Integer userId,
                                                      @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                      @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Вызван метод getAllOtherUser() в ItemRequestController");
        return itemRequestService.getAllByOtherUsers(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoOut getById(@RequestHeader(X_SHARER_USER_ID) Integer userId, @PathVariable Integer requestId) {
        log.info("Вызван метод getById() в ItemRequestController");
        return itemRequestService.getById(userId, requestId);
    }
}
