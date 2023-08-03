package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.aspect.ToLog;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.Constant.*;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@ToLog
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> saveItemRequest(@Valid @RequestBody ItemRequestRequestDto dto,
                                                  @RequestHeader(USER_ID_HEADER) long userId) {
        return itemRequestClient.saveItemRequest(dto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findOwnItemRequests(@RequestHeader(USER_ID_HEADER) long userId) {
        return itemRequestClient.findOwnItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllItemRequests(@RequestHeader(USER_ID_HEADER) long userId,
                                                      @RequestParam(defaultValue = PAGE_DEFAULT_FROM) @PositiveOrZero Short from,
                                                      @RequestParam(defaultValue = PAGE_DEFAULT_SIZE) @Positive Short size) {
        return itemRequestClient.findAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findItemRequestById(@RequestHeader(USER_ID_HEADER) long userId,
                                                      @PathVariable long requestId) {
        return itemRequestClient.findItemRequestsById(userId, requestId);
    }
}
