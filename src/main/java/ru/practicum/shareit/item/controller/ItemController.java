package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    /*private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> addItem(@RequestBody ItemDto itemDto,
                                           @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.addItem(itemDto, userId);
    }

    @PatchMapping("/itemId")
    public ResponseEntity<ItemDto> editItem(@RequestBody ItemDto itemDto, @PathVariable long itemId) {
        return itemService.editItem(itemDto, itemId);
    }*/
}
