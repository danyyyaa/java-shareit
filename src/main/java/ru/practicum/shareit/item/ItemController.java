package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemAllFieldsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.shareit.util.Constant.USER_ID_HEADER;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto saveItem(@Validated(Create.class) @RequestBody ItemDto itemDto,
                              @RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Получена сущность: {}", itemDto);
        Item item = itemService.save(ItemMapper.mapToItem(itemDto), userId);
        return ItemMapper.mapToItemDto(item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@Validated(Update.class) @RequestBody ItemDto itemDto,
                              @RequestHeader(USER_ID_HEADER) long userId,
                              @PathVariable long itemId) {
        log.info("Получен запрос на обновления предмета {}", itemId);
        Item item = itemService.update(ItemMapper.mapToItem(itemDto), itemId, userId);
        return ItemMapper.mapToItemDto(item);
    }

    @GetMapping("/{itemId}")
    public ItemDto findItemById(@RequestHeader(USER_ID_HEADER) long userId,
                               @PathVariable long itemId) {
        log.info("Получен запрос на поулчение предмета {}", itemId);
        return itemService.findById(userId, itemId);
    }

    @GetMapping
    public Collection<ItemAllFieldsDto> findItemsByUserId(@RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Получен запрос на получение всех предметов пользователя {}", userId);
        return itemService.findItemsByUserId(userId);
    }

    @GetMapping("/search")
    public Collection<ItemAllFieldsDto> searchByText(@RequestParam(name = "text") String text,
                                            @RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Получен запрос на поиск предметов по тексту: {}", text);
        return itemService.searchByText(text, userId);
                /*.stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList())*/
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto saveComment(@PathVariable long itemId, @RequestHeader(USER_ID_HEADER) long userId,
                                          @RequestBody CommentRequestDto commentRequestDto) {
        log.info("Получен запрос на сохранение комментария: {}", commentRequestDto);
        return itemService.saveComment(itemId, userId, commentRequestDto.getText());
    }
}
