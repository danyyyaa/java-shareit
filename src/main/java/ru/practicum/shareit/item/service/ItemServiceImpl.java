package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ItemValidationException("Имя не может быть пустым.");
        }

        if (itemDto.getAvailable() == null) {
            throw new ItemValidationException("Available не может быть пустым");
        }

        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ItemValidationException("Описание не может быть пустым");
        }

        UserDto owner = userService.getUserById(userId);
        itemDto.setOwner(UserMapper.mapToUser(owner));

        Optional<Item> createdItem = itemRepository.createItem(ItemMapper.mapToItem(itemDto));
        assert createdItem.isPresent();
        return ItemMapper.mapToItemDto(createdItem.get());
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        Item item = itemRepository.getItemById(itemId).orElseThrow(() ->
                new ItemNotFoundException(String.format("Item %s не найден.", itemId)));

        if (item.getOwner().getId() != userId) {
            throw new UserNotFoundException(
                    String.format("У пользователя %d нету предмета %d.", userId, itemId));
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
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
                new ItemNotFoundException(String.format("Item %s не найден.", itemId)));
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public Collection<ItemDto> getItemsByUserId(long userId) {
        return itemRepository
                .getItems()
                .stream()
                .filter(item -> item.getOwner().getId() == userId)
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> searchByText(String text, long userId) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }

        return itemRepository
                .getItems()
                .stream()
                .filter(i -> i.getAvailable() &&
                        (i.getDescription().toLowerCase().contains(text.toLowerCase()) ||
                                i.getName().toLowerCase().contains(text.toLowerCase()))
                )
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }
}
