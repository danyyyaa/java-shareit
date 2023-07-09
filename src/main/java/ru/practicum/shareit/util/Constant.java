package ru.practicum.shareit.util;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Booking;

import java.util.Comparator;

@UtilityClass
public class Constant {

    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    public static final Sort SORT_BY_START_DATE_DESC = Sort.by(Sort.Direction.DESC, "start");

    public static final String ERROR_RESPONSE = "error";

    public static final Comparator<Booking> orderByStartDateAsc = (a, b) -> {
        if (a.getStart().isAfter(b.getStart())) {
            return 1;
        } else if (a.getStart().isBefore(b.getStart())) {
            return -1;
        } else {
            return 0;
        }
    };

    public static final Comparator<Booking> orderByStartDateDesc = (a, b) -> {
        if (a.getStart().isAfter(b.getStart())) {
            return -1;
        } else if (a.getStart().isBefore(b.getStart())) {
            return 1;
        } else {
            return 0;
        }
    };
}
