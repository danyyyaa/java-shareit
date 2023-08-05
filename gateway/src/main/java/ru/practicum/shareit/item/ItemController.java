package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.aspect.ToLog;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;

import static ru.practicum.shareit.util.Constant.*;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Validated
@ToLog
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public Object saveItem(@Validated(Create.class) @RequestBody ItemDto itemDto,
                           @RequestHeader(USER_ID_HEADER) long userId) {
        return itemClient.saveItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public Object updateItem(@Validated(Update.class) @RequestBody ItemDto itemDto,
                             @RequestHeader(USER_ID_HEADER) long userId,
                             @PathVariable long itemId) {
        return itemClient.updateItem(itemDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public Object findItemById(@RequestHeader(USER_ID_HEADER) long userId,
                               @PathVariable long itemId) {
        return itemClient.findItemById(userId, itemId);
    }

    @GetMapping
    public Object findItemsByUserId(@RequestHeader(USER_ID_HEADER) long userId,
                                    @RequestParam(defaultValue = PAGE_DEFAULT_FROM) @PositiveOrZero Short from,
                                    @RequestParam(defaultValue = PAGE_DEFAULT_SIZE) @Positive Short size) {
        return itemClient.findItemsByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public Object searchByText(@RequestParam(name = "text") String text,
                               @RequestHeader(USER_ID_HEADER) long userId,
                               @RequestParam(defaultValue = PAGE_DEFAULT_FROM) @PositiveOrZero Short from,
                               @RequestParam(defaultValue = PAGE_DEFAULT_SIZE) @Positive Short size) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemClient.searchByText(text, userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public Object saveComment(@PathVariable long itemId, @RequestHeader(USER_ID_HEADER) long userId,
                              @RequestBody CommentRequestDto commentRequestDto) {
        return itemClient.saveComment(itemId, userId, commentRequestDto);
    }
}
