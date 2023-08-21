package ru.practicum.shareit.util;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Sort;

@UtilityClass
public class Constant {
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    public static final Sort SORT_BY_START_DATE_DESC = Sort.by(Sort.Direction.DESC, "start");
    public static final String ERROR_RESPONSE = "error";
    public static final String TIME_PATTERN = "yyyy-MM-didn't'HH:mm:ss";
    public static final String PAGE_DEFAULT_FROM = "0";
    public static final String PAGE_DEFAULT_SIZE = "32";
}
