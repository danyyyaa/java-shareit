package ru.practicum.shareit.booking;

import lombok.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b " +
            "from Booking b " +
            "join fetch b.booker " +
            "join fetch b.item i " +
            "join fetch i.owner " +
            "where b.id = :id " +
            "order by b.start desc")
    @NonNull Optional<Booking> findById(@Param("id") long id);

    @Query("select b " +
            "from Booking b " +
            "join fetch b.booker " +
            "join fetch b.item " +
            "where b.booker = :user ")
    List<Booking> findByBooker(@Param("user") User booker, Pageable page);

    @Query("select b " +
            "from Booking b " +
            "join fetch b.booker " +
            "join fetch b.item " +
            "where b.booker = :user ")
    List<Booking> findByBooker(@Param("user") User booker, Sort sort);

    @Query("select b " +
            "from Booking b " +
            "join fetch b.booker " +
            "join fetch b.item " +
            "where b.booker = :user " +
            "   and b.start < :time " +
            "   and b.end > :time ")
    List<Booking> findByBookerCurrent(
            @Param("user") User booker,
            @Param("time") LocalDateTime currentTime, Pageable page);

    @Query("select b " +
            "from Booking b " +
            "join fetch b.booker " +
            "join fetch b.item " +
            "where b.booker = :user " +
            "   and b.end < :time ")
    List<Booking> findByBookerPast(@Param("user") User booker,
                                   @Param("time") LocalDateTime currentTime, Pageable page);

    @Query("select b " +
            "from Booking b " +
            "join fetch b.booker " +
            "join fetch b.item " +
            "where b.booker = :user " +
            "   and b.start > :time ")
    List<Booking> findByBookerFuture(@Param("user") User booker,
                                     @Param("time") LocalDateTime currentTime, Pageable page);

    @Query("select b " +
            "from Booking b " +
            "join fetch b.booker " +
            "join fetch b.item " +
            "where b.booker = :user " +
            "   and b.status = :status ")
    List<Booking> findByBookerAndStatus(@Param("user") User booker,
                                        @Param("status") Status status, Pageable page);

    @Query("select b " +
            "from Booking b " +
            "join fetch b.booker " +
            "join fetch b.item i " +
            "where i.owner = :user ")
    List<Booking> findByItemOwner(@Param("user") User itemOwner, Pageable page);

    @Query("select b " +
            "from Booking b " +
            "join fetch b.booker " +
            "join fetch b.item i " +
            "where i.owner = :user " +
            "   and b.start < :time " +
            "   and b.end > :time ")
    List<Booking> findByItemOwnerCurrent(@Param("user") User itemOwner,
                                         @Param("time") LocalDateTime currentTime, Pageable page);

    @Query("select b " +
            "from Booking b " +
            "join fetch b.booker " +
            "join fetch b.item i " +
            "where i.owner = :user " +
            "   and b.end < :time ")
    List<Booking> findByItemOwnerPast(@Param("user") User itemOwner,
                                      @Param("time") LocalDateTime currentTime, Pageable page);

    @Query("select b " +
            "from Booking b " +
            "join fetch b.booker " +
            "join fetch b.item i " +
            "where i.owner = :user " +
            "   and b.start > :time ")
    List<Booking> findByItemOwnerFuture(@Param("user") User itemOwner,
                                        @Param("time") LocalDateTime currentTime, Pageable page);

    @Query("select b " +
            "from Booking b " +
            "join fetch b.booker " +
            "join fetch b.item i " +
            "where i.owner = :user " +
            "   and b.status = :status ")
    List<Booking> findByItemOwnerAndStatus(@Param("user") User itemOwner,
                                           @Param("status") Status status, Pageable page);

    @Query("select b " +
            "from Booking b " +
            "join fetch b.item i " +
            "where i = :items " +
            "   and i.owner.id = :ownerId" +
            "   and b.status = :status " +
            "order by b.start")
    List<Booking> findBookingsByItemId(@Param("items") List<Item> items,
                                       @Param("ownerId") long ownerId, @Param("status") Status status);

    List<Booking> findBookingByItemIdAndStatusNotInAndStartBefore(
            long itemId, List<Status> statuses, LocalDateTime start);

    List<Booking> findBookingsByItemIn(List<Item> items);
}
