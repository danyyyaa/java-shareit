package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.FindItemByIdDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.Create;

import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.shareit.util.Constant.USER_ID_HEADER;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto saveItem(@Validated(Create.class) @RequestBody ItemDto itemDto,
                              @RequestHeader(USER_ID_HEADER) long userId) {
        Item item = itemService.save(ItemMapper.mapToItem(itemDto), userId);
        return ItemMapper.mapToItemDto(item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @RequestHeader(USER_ID_HEADER) long userId,
                              @PathVariable long itemId) {
        Item item = itemService.update(ItemMapper.mapToItem(itemDto), itemId, userId);
        return ItemMapper.mapToItemDto(item);
    }

    @GetMapping("/{itemId}")
    public ItemDto findItemById(@RequestHeader(USER_ID_HEADER) long userId,
                               @PathVariable long itemId) {
        return itemService.findById(userId, itemId);
    }

    @GetMapping
    public Collection<FindItemByIdDto> findItemsByUserId(@RequestHeader(USER_ID_HEADER) long userId) {
        return itemService.findItemsByUserId(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchByText(@RequestParam(name = "text") String text,
                                            @RequestHeader(USER_ID_HEADER) long userId) {
        return itemService.searchByText(text, userId)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }
}
