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
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestsMapper;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

        List<Long> requestsId = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        Map<Long, List<Item>> itemsMap = itemRepository.findItemByItemRequestIdIn(requestsId)
                .stream()
                .collect(Collectors.groupingBy(item -> item.getItemRequest().getId()));

        return requests.stream()
                .map(itemRequest -> {
                    List<ItemGetOwnItemRequestDto> items = itemsMap
                            .getOrDefault(itemRequest.getId(), Collections.emptyList())
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

        List<Long> requestsId = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        Map<Long, List<Item>> itemsMap = itemRepository.findItemByItemRequestIdIn(requestsId)
                .stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.groupingBy(item -> item.getItemRequest().getId()));

        return requests.stream()
                .map(itemRequest -> {
                    List<ItemGetOwnItemRequestDto> items = itemsMap
                            .getOrDefault(itemRequest.getId(), Collections.emptyList())
                            .stream()
                            .map(ItemMapper::mapFromItemToItemGetOwnItemRequestDto)
                            .collect(Collectors.toList());

                    return ItemRequestsMapper
                            .mapToItemRequestResponseDtoWithItemId(itemRequest, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestResponseDto findItemRequestsById(long userId, long requestId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь %s не найден.", userId)));

        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException(String.format("Бронирование %s не найдено.", user.getId())));

        List<ItemGetOwnItemRequestDto> items = itemRepository.findItemByItemRequestIdIn(List.of(requestId))
                .stream()
                .map(ItemMapper::mapFromItemToItemGetOwnItemRequestDto)
                .collect(Collectors.toList());


        return ItemRequestsMapper.mapToItemRequestResponseDtoWithItemId(itemRequest, items);
    }
}
