package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;

    @GetMapping
    public List<ItemDtoInfo> getAllItems(@RequestHeader(X_SHARER_USER_ID) Integer userId) {
        log.info("Вызван метод getAllItems() в ItemController");
        return itemService.getAllItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoInfo getItemById(@RequestHeader(X_SHARER_USER_ID) Integer userId, @PathVariable Integer itemId) {
        log.info("Вызван метод getItemById() в ItemController");
        return itemService.getItemById(itemId, userId);
    }

    @PostMapping
    public ItemDto addItem(@RequestBody @Valid ItemDto itemDto, @RequestHeader(X_SHARER_USER_ID) Integer userId) {
        log.info("Вызван метод addItem() в ItemController");
        return itemService.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto, @RequestHeader(X_SHARER_USER_ID) Integer userId,
                              @PathVariable Integer itemId) {
        log.info("Вызван метод updateItem() в ItemController");
        return itemService.updateItem(itemDto, userId, itemId);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<HttpStatus> removeItem(@PathVariable Integer itemId) {
        log.info("Вызван метод removeItem() в ItemController");
        itemService.removeItem(itemId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text, @RequestHeader(X_SHARER_USER_ID) Integer userId) {
        log.info("Вызван метод searchItem() в ItemController");
        return itemService.searchItem(text, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(X_SHARER_USER_ID) Integer userId, @PathVariable Integer itemId,
                                 @Valid @RequestBody CommentDto commentDto) {
        log.info("Вызван метод addComment() в ItemController");
        return itemService.addComment(userId, itemId, commentDto);
    }
}
