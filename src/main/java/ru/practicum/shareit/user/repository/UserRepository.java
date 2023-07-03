package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Collection<User> findUsersByEmail(String email);
}
