package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemAllFieldsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.entity.Comment;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    private User user;
    private Item item;
    private Booking booking;
    private Comment comment;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .build();

        item = Item.builder()
                .id(1L)
                .name("Test Item")
                .owner(user)
                .build();

        booking = Booking.builder()
                .id(1L)
                .item(item)
                .status(Status.APPROVED)
                .build();

        comment = Comment.builder()
                .id(1L)
                .item(item)
                .author(user)
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("description")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldSaveItem() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.save(any())).thenReturn(item);

        Item savedItem = itemService.save(itemDto, user.getId());

        assertNotNull(savedItem);
        assertThat(savedItem.getName(), equalTo(item.getName()));
        assertThat(savedItem.getOwner(), equalTo(user));

        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenOwnerNotFoundInSave() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");

        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        long userId = user.getId();
        assertThrows(NotFoundException.class, () -> itemService.save(itemDto, userId));

        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRepository, never()).save(any());
    }

    @Test
    void shouldSetItemRequestWhenRequestIdProvidedInSave() {
        long userId = 1L;
        long itemRequestId = 1L;
        String itemName = "Test Item";

        ItemDto itemDto = new ItemDto();
        itemDto.setName(itemName);
        itemDto.setRequestId(itemRequestId);

        Item item = new Item();
        item.setName(itemName);
        item.setOwner(user);
        item.setItemRequest(itemRequest);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(itemRequestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(Mockito.any(Item.class))).thenReturn(item);

        Item savedItem = itemService.save(itemDto, userId);

        assertNotNull(savedItem);
        assertEquals(itemName, savedItem.getName());
        assertEquals(user, savedItem.getOwner());
        assertEquals(itemRequest, savedItem.getItemRequest());

        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, times(1)).findById(itemRequestId);
        verify(itemRepository, times(1)).save(Mockito.any(Item.class));
    }


    @Test
    void shouldUpdateItemInUpdate() {
        Item updatedItem = new Item();
        updatedItem.setId(item.getId());
        updatedItem.setName("Updated Item");

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(updatedItem);

        Item result = itemService.update(updatedItem, item.getId(), user.getId());

        assertNotNull(result);
        assertThat(result.getName(), equalTo(updatedItem.getName()));

        verify(itemRepository, times(1)).findById(item.getId());
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenItemNotFoundInUpdate() {
        Item updatedItem = new Item();
        updatedItem.setId(item.getId());
        updatedItem.setName("Updated Item");

        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        long itemId = item.getId();
        long userId = user.getId();
        assertThrows(NotFoundException.class, () -> itemService.update(updatedItem, itemId, userId));

        verify(itemRepository, times(1)).findById(item.getId());
        verify(itemRepository, never()).save(any());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserNotOwnerInUpdate() {
        Item updatedItem = new Item();
        updatedItem.setId(item.getId());
        updatedItem.setName("Updated Item");

        User differentUser = new User();
        differentUser.setId(2L);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        long itemId = item.getId();
        long userId = differentUser.getId();
        assertThrows(NotFoundException.class, () -> itemService.update(updatedItem, itemId, userId));

        verify(itemRepository, times(1)).findById(item.getId());
        verify(itemRepository, never()).save(any());
    }

    @Test
    void shouldReturnItemDtoInFindById() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ItemAllFieldsDto result = itemService.findById(user.getId(), item.getId());

        assertNotNull(result);
        assertThat(result.getId(), equalTo(item.getId()));
        assertThat(result.getName(), equalTo(item.getName()));

        verify(itemRepository, times(1)).findById(item.getId());
    }

    @Test
    void ShouldThrowNotFoundExceptionWhenItemNotFoundInFindById() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        long itemId = item.getId();
        long userId = user.getId();
        assertThrows(NotFoundException.class, () -> itemService.findById(userId, itemId));

        verify(itemRepository, times(1)).findById(item.getId());
    }

    @Test
    void shouldReturnItemsDtoInSearchByText() {
        String searchText = "Test";
        List<Item> items = Collections.singletonList(item);

        when(itemRepository.findItemsByText(searchText, Pageable.unpaged())).thenReturn(items);

        Collection<ItemAllFieldsDto> result = itemService.searchByText(searchText, user.getId(), Pageable.unpaged());

        assertNotNull(result);
        assertThat(result, hasSize(1));
        assertThat(result.iterator().next().getId(), equalTo(item.getId()));
        assertThat(result.iterator().next().getName(), equalTo(item.getName()));

        verify(itemRepository, times(1)).findItemsByText(searchText, Pageable.unpaged());
    }

    @Test
    void shouldReturnEmptyListWhenSearchTextIsBlankInSearchByText() {
        String searchText = "";

        Collection<ItemAllFieldsDto> result = itemService.searchByText(searchText, user.getId(), Pageable.unpaged());

        assertNotNull(result);
        assertThat(result, empty());

        verify(itemRepository, never()).findItemsByText(searchText, Pageable.unpaged());
    }

    @Test
    void shouldReturnItemsDtoWhenFindItemsByUserId() {
        List<Item> items = Collections.singletonList(item);

        when(itemRepository.findAllByOwnerId(user.getId(), Pageable.unpaged())).thenReturn(items);

        Collection<ItemAllFieldsDto> result = itemService.findItemsByUserId(user.getId(), Pageable.unpaged());

        assertNotNull(result);
        assertThat(result, hasSize(1));
        assertThat(result.iterator().next().getId(), equalTo(item.getId()));
        assertThat(result.iterator().next().getName(), equalTo(item.getName()));

        verify(itemRepository, times(1)).findAllByOwnerId(user.getId(), Pageable.unpaged());
    }

    @Test
    void shouldSaveComment() {
        long itemId = 1L;
        long userId = 1L;
        String text = "Test comment";

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(bookingRepository.findByBooker(user, Sort.unsorted())).thenReturn(Collections.singletonList(booking));

        List<Booking> bookings = Collections.singletonList(booking);
        when(bookingRepository.findBookingByItemIdAndStatusNotInAndStartBefore(eq(itemId),
                eq(List.of(Status.REJECTED)), any())).thenReturn(bookings);

        when(commentRepository.save(any())).thenReturn(comment);

        CommentResponseDto result = itemService.saveComment(itemId, userId, text);

        assertNotNull(result);
        assertThat(result.getId(), equalTo(comment.getId()));
        assertThat(result.getText(), equalTo(comment.getText()));

        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository, times(1)).findByBooker(user, Sort.unsorted());
        verify(bookingRepository, times(1)).findBookingByItemIdAndStatusNotInAndStartBefore(eq(itemId),
                eq(List.of(Status.REJECTED)), any());
        verify(commentRepository, times(1)).save(any());
    }


    @Test
    void _ShouldThrowValidationExceptionWhenTextIsBlankWhenSaveComment() {
        long itemId = 1L;
        long userId = 1L;
        String text = "";

        assertThrows(ValidationException.class, () -> itemService.saveComment(itemId, userId, text));

        verify(userRepository, never()).findById(userId);
        verify(itemRepository, never()).findById(itemId);
        verify(bookingRepository, never()).findByBooker(user, Sort.unsorted());
        verify(commentRepository, never()).save(any());
    }

    @Test
    void ShouldThrowValidationExceptionWhenUserHasNoBookingsInSaveComment() {
        long itemId = 1L;
        long userId = 1L;
        String text = "Test comment";

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findByBooker(user, Sort.unsorted())).thenReturn(Collections.emptyList());

        assertThrows(ValidationException.class, () -> itemService.saveComment(itemId, userId, text));

        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository, times(1)).findByBooker(user, Sort.unsorted());
        verify(commentRepository, never()).save(any());
    }

    @Test
    void ShouldThrowValidationExceptionWhenNoBookingsForItemInSaveComment() {
        long itemId = 1L;
        long userId = 1L;
        String text = "Test comment";

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findByBooker(eq(user), Mockito.any(Sort.class))).thenReturn(Collections.emptyList());

        assertThrows(ValidationException.class, () -> itemService.saveComment(itemId, userId, text));

        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository, times(1)).findByBooker(eq(user), Mockito.any(Sort.class));
        verify(bookingRepository, never()).findBookingByItemIdAndStatusNotInAndStartBefore(
                anyLong(), anyList(), Mockito.any(LocalDateTime.class));
        verify(commentRepository, never()).save(any());
    }

    @Test
    void ShouldThrowNotFoundExceptionWhenItemRequestNotFoundInSave() {
        long ownerId = 1L;
        long nonExistingItemRequestId = 1L;

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setRequestId(nonExistingItemRequestId);

        User owner = new User();
        owner.setId(ownerId);

        when(userRepository.findById(any())).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.save(itemDto, ownerId));

        verify(userRepository, times(1)).findById(ownerId);
        verify(itemRequestRepository, times(1)).findById(nonExistingItemRequestId);
        verify(itemRepository, never()).save(Mockito.any(Item.class));
    }

    @Test
    void shouldUpdateItemDescriptionWhenDescriptionIsNotNullOrNotBlank() {
        long itemId = 1L;
        long userId = 1L;

        String updatedDescription = "Updated Description";

        Item item = new Item();
        item.setId(itemId);
        item.setName("Test Item");
        item.setDescription(updatedDescription);
        item.setAvailable(true);
        User owner = new User();
        owner.setId(userId);
        item.setOwner(owner);

        Item existingItem = new Item();
        existingItem.setId(itemId);
        existingItem.setName("Test Item");
        existingItem.setDescription("Old Description");
        existingItem.setAvailable(true);
        existingItem.setOwner(owner);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

        Item result = itemService.update(item, itemId, userId);

        verify(itemRepository, times(1)).findById(itemId);
        verify(itemRepository, times(1)).save(existingItem);

        assertEquals(updatedDescription, result.getDescription());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserNotFound() {
        long itemId = 1L;
        long userId = 1L;
        String text = "Test comment";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.saveComment(itemId, userId, text));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenItemNotFound() {
        long itemId = 1L;
        long userId = 1L;
        String text = "Test comment";

        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.saveComment(itemId, userId, text));
        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).findById(any());
    }

    @Test
    void shouldThrowValidationExceptionWhenNoBookingsExistForComment() {
        long itemId = 1L;
        long userId = 1L;
        String text = "Test Comment";

        when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item));
        when(bookingRepository.findByBooker(any(), (Sort) any())).thenReturn(List.of(booking));

        assertThrows(ValidationException.class, () -> itemService.saveComment(itemId, userId, text));
    }
}
