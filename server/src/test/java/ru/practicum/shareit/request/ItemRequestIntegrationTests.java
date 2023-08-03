package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestIntegrationTests {
    private final EntityManager entityManager;
    private final ItemRequestService itemRequestService;
    private final ItemService itemService;
    private final UserService userService;
    private static Pageable page;
    private static User user;
    private ItemDto itemDto;

    @BeforeAll
    static void beforeAll() {
        page = PageRequest.of(0, 5);
    }

    @BeforeEach
    void init() {
        itemDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

        user = User.builder()
                .name("name")
                .email("email@email.ru")
                .build();
    }

    @Test
    void shouldSaveItemRequest() {
        userService.save(user);
        ItemRequest itemRequestToSave = itemRequestService.saveItemRequest("description", 1L);

        TypedQuery<ItemRequest> query = entityManager
                .createQuery("select ir from ItemRequest ir where ir.id = :id", ItemRequest.class);

        ItemRequest itemRequestFromBd = query.setParameter("id", 1L).getSingleResult();

        assertThat(itemRequestFromBd.getDescription(), equalTo(itemRequestToSave.getDescription()));
    }

    @Test
    void shouldFindOwnItemRequests() {
        userService.save(user);
        itemRequestService.saveItemRequest("description", 1L);

        List<ItemRequestResponseDto> itemRequests =
                (List<ItemRequestResponseDto>) itemRequestService.findOwnItemRequests(1L);

        assertThat(itemRequests.size(), equalTo(1));
    }

    @Test
    void shouldFindItemRequestById() {
        userService.save(user);
        itemRequestService.saveItemRequest("description", 1L);

        itemDto.setRequestId(1L);
        itemService.save(itemDto, 1L);

        ItemRequestResponseDto itemRequestResponseDto = itemRequestService.findItemRequestsById(1L, 1L);

        assertThat(itemRequestResponseDto.getDescription(), equalTo("description"));
        assertThat(itemRequestResponseDto.getId(), equalTo(1L));
    }

    @Test
    void shouldFindAllItemRequests() {
        User secondUser = User.builder()
                .name("second")
                .email("email1@email.ru")
                .build();

        userService.save(user);
        itemRequestService.saveItemRequest("description", 1L);

        userService.save(secondUser);

        itemDto.setRequestId(1L);
        itemService.save(itemDto, 1L);

        itemService.save(itemDto, 1L);
        List<ItemRequestResponseDto> itemRequests =
                (List<ItemRequestResponseDto>) itemRequestService.findAllItemRequests(2L, page);

        assertThat(itemRequests.size(), equalTo(1));
    }
}
