package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemAllFieldsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.entity.Comment;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static java.time.LocalDateTime.MAX;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemIntegrationTests {
    private final UserService userService;
    private final EntityManager entityManager;
    private final ItemService itemService;
    private final BookingService bookingService;
    private static User user;
    private static User secondUser;
    private Item item;
    private ItemDto itemDto;
    private static Pageable page;

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

        item = Item.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

        user = User.builder()
                .name("name")
                .email("email@email.ru")
                .build();

        secondUser = User.builder()
                .name("second")
                .email("second@email.ru")
                .build();
    }

    @Test
    void shouldSaveItem() {
        userService.save(user);
        itemService.save(itemDto, 1L);

        TypedQuery<Item> query = entityManager.createQuery("select i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", 1L).getSingleResult();
        assertThat(item.getId(), notNullValue());
    }

    @Test
    void shouldUpdateItem() {
        userService.save(user);
        itemService.save(itemDto, 1L);

        Item update = Item.builder().name("updatedName").build();
        itemService.update(update, 1L, 1L);

        TypedQuery<Item> query = entityManager.createQuery("select i from Item i where i.id = :id", Item.class);
        Item updatedItem = query.setParameter("id", 1L).getSingleResult();

        assertThat(updatedItem.getName(), equalTo("updatedName"));
    }

    @Test
    void shouldSaveComment() {
        userService.save(user);
        userService.save(secondUser);
        itemService.save(itemDto, 1L);
        bookingService.save(1L, LocalDateTime.now(), MAX, 2L);

        String text = "text";
        itemService.saveComment(1L, 2L, text);

        TypedQuery<Comment> query = entityManager.createQuery(
                "select c from Comment c where c.id = :id", Comment.class);

        Comment comment = query.setParameter("id", 1L).getSingleResult();
        item.setId(1L);
        assertThat(comment.getId(), notNullValue());
        assertThat(comment.getText(), equalTo(text));
    }

    @Test
    void shouldFindById() {
        userService.save(user);
        itemService.save(itemDto, 1L);
        ItemAllFieldsDto itemAllFieldsDto = itemService.findById(1L, 1L);

        TypedQuery<Item> query = entityManager.createQuery("select i from Item i where i.id = :id", Item.class);

        Item itemFromBd = query.setParameter("id", 1L).getSingleResult();

        assertThat(itemAllFieldsDto.getName(), equalTo(itemFromBd.getName()));
        assertThat(itemAllFieldsDto.getDescription(), equalTo(itemFromBd.getDescription()));
        assertThat(itemAllFieldsDto.getAvailable(), equalTo(itemFromBd.getAvailable()));
    }

    @Test
    void shouldFindItemsByUserId() {
        userService.save(user);
        itemService.save(itemDto, 1L);

        Collection<ItemAllFieldsDto> items = itemService.findItemsByUserId(1L, page);

        assertThat(items.size(), equalTo(1));
    }

    @Test
    void shouldSearchByText() {
        userService.save(user);
        itemDto.setDescription("toSearch");
        itemService.save(itemDto, 1L);

        List<ItemAllFieldsDto> items = (List<ItemAllFieldsDto>)
                itemService.searchByText("toSearch", 1L, page);

        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getDescription(), equalTo("toSearch"));
    }
}
