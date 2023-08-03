package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.aspect.ToLog;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.Constant.*;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Validated
@ToLog
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> saveItem(@Validated(Create.class) @RequestBody ItemDto itemDto,
                                           @RequestHeader(USER_ID_HEADER) long userId) {
        return itemClient.saveItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@Validated(Update.class) @RequestBody ItemDto itemDto,
                                             @RequestHeader(USER_ID_HEADER) long userId,
                                             @PathVariable long itemId) {
        return itemClient.updateItem(itemDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItemById(@RequestHeader(USER_ID_HEADER) long userId,
                                               @PathVariable long itemId) {
        return itemClient.findItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findItemsByUserId(@RequestHeader(USER_ID_HEADER) long userId,
                                                    @RequestParam(defaultValue = PAGE_DEFAULT_FROM) @PositiveOrZero Short from,
                                                    @RequestParam(defaultValue = PAGE_DEFAULT_SIZE) @Positive Short size) {
        return itemClient.findItemsByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchByText(@RequestParam(name = "text") String text,
                                               @RequestHeader(USER_ID_HEADER) long userId,
                                               @RequestParam(defaultValue = PAGE_DEFAULT_FROM) @PositiveOrZero Short from,
                                               @RequestParam(defaultValue = PAGE_DEFAULT_SIZE) @Positive Short size) {
        return itemClient.searchByText(text, userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> saveComment(@PathVariable long itemId, @RequestHeader(USER_ID_HEADER) long userId,
                                              @RequestBody CommentRequestDto commentRequestDto) {
        return itemClient.saveComment(itemId, userId, commentRequestDto);
    }
}
