package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemGetOwnItemRequestDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestsMapper;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequest saveItemRequest(String description, long userId) {
        if (description == null || description.isBlank()) {
            throw new ValidationException("Описание не может быть пустым");
        }

        User requestor = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь %s не найден.", userId)));

        ItemRequest itemRequest = ItemRequest.builder()
                .description(description)
                .created(LocalDateTime.now())
                .requestor(requestor)
                .build();

        return itemRequestRepository.save(itemRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemRequestResponseDto> findOwnItemRequests(long userId) {
        User requestor = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь %s не найден.", userId)));

        List<ItemRequest> requests = itemRequestRepository.findItemRequestsByRequestorId(requestor.getId());
        Map<ItemRequest, List<Item>> map = findItemsToItemRequests(requests);

        return requests.stream()
                .map(itemRequest -> {
                    List<ItemGetOwnItemRequestDto> items = map
                            .getOrDefault(itemRequest, Collections.emptyList())
                            .stream()
                            .map(ItemMapper::mapFromItemToItemGetOwnItemRequestDto)
                            .collect(Collectors.toList());

                    return ItemRequestsMapper
                            .mapToItemRequestResponseDtoWithItemId(itemRequest, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemRequestResponseDto> findAllItemRequests(long userId, Pageable page) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь %s не найден.", userId)));

        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdNot(user.getId(), page);
        Map<ItemRequest, List<Item>> map = findItemsToItemRequests(requests);
        return mapToDto(map);
    }

    @Override
    public ItemRequestResponseDto findItemRequestsById(long userId, long requestId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь %s не найден.", userId)));

        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException(String.format("Бронирование %s не найдено.", user.getId())));

        Map<ItemRequest, List<Item>> map = findItemsToItemRequests(List.of(itemRequest));
        return mapToDto(map).get(0);
    }

    private List<ItemRequestResponseDto> mapToDto(Map<ItemRequest, List<Item>> map) {
        return map.entrySet().stream()
                .map(entry -> {
                    List<ItemGetOwnItemRequestDto> itemDtos = Optional.ofNullable(entry.getValue())
                            .orElse(Collections.emptyList())
                            .stream()
                            .map(ItemMapper::mapFromItemToItemGetOwnItemRequestDto)
                            .collect(Collectors.toList());
                    return ItemRequestsMapper.mapToItemRequestResponseDtoWithItemId(entry.getKey(), itemDtos);
                })
                .collect(Collectors.toList());
    }

    private Map<ItemRequest, List<Item>> findItemsToItemRequests(List<ItemRequest> requests) {
        return itemRepository.findItemByItemRequestIn(requests)
                .stream()
                .collect(Collectors.groupingBy(
                        Item::getItemRequest,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list != null ? list : Collections.emptyList()
                        )
                ));
    }
}
