package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.aspect.ToLog;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.util.OffsetBasedPageRequest;

import java.util.Collection;

import static ru.practicum.shareit.util.Constant.*;

@RestController
@RequestMapping(path = "/requests")
@ToLog
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequest saveItemRequest(@RequestBody ItemRequestRequestDto dto,
                                       @RequestHeader(USER_ID_HEADER) long userId) {
        return itemRequestService.saveItemRequest(dto.getDescription(), userId);
    }

    @GetMapping
    public Collection<ItemRequestResponseDto> findOwnItemRequests(@RequestHeader(USER_ID_HEADER) long userId) {
        return itemRequestService.findOwnItemRequests(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestResponseDto> findAllItemRequests(@RequestHeader(USER_ID_HEADER) long userId,
                                                                  @RequestParam(defaultValue = PAGE_DEFAULT_FROM) Short from,
                                                                  @RequestParam(defaultValue = PAGE_DEFAULT_SIZE) Short size) {

        Pageable page = new OffsetBasedPageRequest(from, size, Sort.by("created").descending());
        return itemRequestService.findAllItemRequests(userId, page);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto findItemRequestById(@RequestHeader(USER_ID_HEADER) long userId,
                                                      @PathVariable long requestId) {
        return itemRequestService.findItemRequestsById(userId, requestId);
    }
}
