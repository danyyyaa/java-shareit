package ru.practicum.shareit.booking;

import lombok.NonNull;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.enums.BookingTimeState;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b " +
            "from Booking b " +
            "join fetch b.booker " +
            "join fetch b.item i " +
            "join fetch i.owner " +
            "where b.id = :id " +
            "order by b.start desc")
    @NonNull Optional<Booking> findById(@Param("id") @NonNull Long id);

    @Query("select b " +
            "from Booking b " +
            "join fetch b.booker " +
            "join fetch b.item " +
            "where b.booker = :user ")
    List<Booking> findByBooker(@Param("user") User booker, Sort start);

    @Query("select b " +
            "from Booking b " +
            "join fetch b.booker " +
            "join fetch b.item " +
            "where b.booker = :user " +
            "   and b.start < :time " +
            "   and b.end > :time ")
    List<Booking> findByBookerCurrent(
            @Param("user") User booker,
            @Param("time") LocalDateTime currentTime,
            Sort start);

    @Query("select b " +
            "from Booking b " +
            "join fetch b.booker " +
            "join fetch b.item " +
            "where b.booker = :user " +
            "   and b.end < :time ")
    List<Booking> findByBookerPast(@Param("user") User booker, @Param("time") LocalDateTime currentTime, Sort start);

    @Query("select b " +
            "from Booking b " +
            "join fetch b.booker " +
            "join fetch b.item " +
            "where b.booker = :user " +
            "   and b.start > :time ")
    List<Booking> findByBookerFuture(@Param("user") User booker, @Param("time") LocalDateTime currentTime, Sort start);

    @Query("select b " +
            "from Booking b " +
            "join fetch b.booker " +
            "join fetch b.item " +
            "where b.booker = :user " +
            "   and b.status = :status ")
    List<Booking> findByBookerAndStatus(@Param("user") User booker, @Param("status") BookingTimeState status, Sort start);

    @Query("select b " +
            "from Booking b " +
            "join fetch b.booker " +
            "join fetch b.item i " +
            "where i.owner = :user ")
    List<Booking> findByItemOwner(@Param("user") User itemOwner, Sort start);

    @Query("select b " +
            "from Booking b " +
            "join fetch b.booker " +
            "join fetch b.item i " +
            "where i.owner = :user " +
            "   and b.start < :time " +
            "   and b.end > :time ")
    List<Booking> findByItemOwnerCurrent(@Param("user") User itemOwner, @Param("time") LocalDateTime currentTime, Sort start);

    @Query("select b " +
            "from Booking b " +
            "join fetch b.booker " +
            "join fetch b.item i " +
            "where i.owner = :user " +
            "   and b.end < :time ")
    List<Booking> findByItemOwnerPast(@Param("user") User itemOwner, @Param("time") LocalDateTime currentTime, Sort start);

    @Query("select b " +
            "from Booking b " +
            "join fetch b.booker " +
            "join fetch b.item i " +
            "where i.owner = :user " +
            "   and b.start > :time ")
    List<Booking> findByItemOwnerFuture(@Param("user") User itemOwner, @Param("time") LocalDateTime currentTime, Sort start);

    @Query("select b " +
            "from Booking b " +
            "join fetch b.booker " +
            "join fetch b.item i " +
            "where i.owner = :user " +
            "   and b.status = :status ")
    List<Booking> findByItemOwnerAndStatus(@Param("user") User itemOwner, @Param("status") BookingTimeState status, Sort start);
}
