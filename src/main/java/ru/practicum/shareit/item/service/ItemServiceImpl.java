package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemValidationException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserService userService;

    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) {
        UserDto owner = userService.getUserById(userId);
        itemDto.setOwner(UserMapper.mapToUser(owner));

        Optional<Item> createdItem = itemRepository.createItem(ItemMapper.mapToItem(itemDto));
        assert createdItem.isPresent();
        return ItemMapper.mapToItemDto(createdItem.get());
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        Item item = itemRepository.getItemById(itemId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Предмет не найден: %s", itemDto)));

        if (item.getOwner().getId() != userId) {
            throw new EntityNotFoundException(
                    String.format("У пользователя %d нету предмета %s.", userId, itemDto));
        }

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        Optional<Item> updatedItem = itemRepository.updateItem(item, itemId, userId);
        assert updatedItem.isPresent();
        return ItemMapper.mapToItemDto(updatedItem.get());
    }

    @Override
    public ItemDto getItemById(long userId, long itemId) {
        Item item = itemRepository.getItemById(itemId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Item %s не найден.", itemId)));
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public Collection<ItemDto> getItemsByUserId(long userId) {
        return itemRepository.getItemsByUserId(userId)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> searchByText(String text, long userId) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }

        return itemRepository
                .searchByText(text, userId)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }
}
