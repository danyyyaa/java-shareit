package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
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
class UserIntegrationTests {
    private final UserService userService;
    private final EntityManager entityManager;

    private static User user;

    @BeforeEach
    void init() {
        user = User.builder()
                .name("name")
                .email("email@email.ru")
                .build();
    }

    @Test
    void shouldSaveUser() {
        userService.save(user);

        TypedQuery<User> query = entityManager.createQuery("select u from User u where u.id = :id", User.class);
        User userFromDb = query.setParameter("id", 1L).getSingleResult();
        assertThat(user.getName(), equalTo(userFromDb.getName()));
        assertThat(user.getEmail(), equalTo(userFromDb.getEmail()));
    }

    @Test
    void shouldUpdateUser() {
        userService.save(user);

        User update = User.builder().name("updatedName").build();
        userService.update(update, 1L);

        TypedQuery<User> query = entityManager.createQuery("select u from User u where u.id = :id", User.class);
        User updatedUser = query.setParameter("id", 1L).getSingleResult();
        assertThat(updatedUser.getName(), equalTo("updatedName"));
    }

    @Test
    void shouldFindByIdUser() {
        userService.save(user);
        User newUser = userService.findById(1L);

        user.setId(1L);
        assertThat(user, equalTo(newUser));
    }

    @Test
    void shouldFindAllUsers() {
        userService.save(user);

        List<User> users = (List<User>) userService.findAll();

        user.setId(1L);
        assertThat(users.get(0), equalTo(user));
    }

    @Test
    void shouldDeleteByIdUser() {
        userService.save(user);
        assertThat(userService.findAll().size(), equalTo(1));

        userService.deleteById(1);
        assertThat(userService.findAll().size(), equalTo(0));
    }
}
