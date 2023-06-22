package ru.practicum.shareit.user.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private long id;

    private String name;

    private String email;
}
