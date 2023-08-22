package ru.practicum.shareit.util;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.Booking;

@UtilityClass
public class Comparator {

    public static final java.util.Comparator<Booking> orderByStartDateAsc = (a, b) -> {
        if (a.getStart().isAfter(b.getStart())) {
            return 1;
        } else if (a.getStart().isBefore(b.getStart())) {
            return -1;
        } else {
            return 0;
        }
    };

    public static final java.util.Comparator<Booking> orderByStartDateDesc = (a, b) -> {
        if (a.getStart().isAfter(b.getStart())) {
            return -1;
        } else if (a.getStart().isBefore(b.getStart())) {
            return 1;
        } else {
            return 0;
        }
    };
}
