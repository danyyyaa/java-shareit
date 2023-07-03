package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;


    @Override
    public Item createItem(Item item, long ownerId) {
        User owner = userRepository.findById(ownerId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь %s не найден.", ownerId)));

        item.setOwner(owner);

        return itemRepository.createItem(item, ownerId);
    }

    @Override
    public Item updateItem(Item item, long itemId, long userId) {
        Item updatedItem = itemRepository.getItemById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Предмет не найден: %s", item)));

        if (updatedItem.getOwner().getId() != userId) {
            throw new NotFoundException(
                    String.format("У пользователя %d нету предмета %s.", userId, item));
        }

        if (item.getName() != null && !item.getName().isBlank()) {
            updatedItem.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            updatedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }

        return updatedItem;
    }

    @Override
    public Item getItemById(long userId, long itemId) {
        return itemRepository.getItemById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Item %s не найден.", itemId)));
    }

    @Override
    public Collection<Item> getItemsByUserId(long userId) {
        return itemRepository.getItemsByUserId(userId);
    }

    @Override
    public Collection<Item> searchByText(String text, long userId) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.searchByText(text, userId);
    }
}
