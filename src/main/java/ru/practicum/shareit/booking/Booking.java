package ru.practicum.shareit.booking;

import lombok.*;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@ToString
@Table(name = "bookings", schema = "public")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date")
    private LocalDateTime start;

    @Column(name = "end_date")
    private LocalDateTime end;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    @ToString.Exclude
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booker_id")
    @ToString.Exclude
    private User booker;

    @Enumerated(EnumType.STRING)
    private Status status;
}
