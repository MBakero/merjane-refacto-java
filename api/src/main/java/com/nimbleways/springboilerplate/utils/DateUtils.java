package com.nimbleways.springboilerplate.utils;

import java.time.LocalDate;

public class DateUtils {

    private DateUtils() {}

    public static boolean isDateInBetween(LocalDate start, LocalDate end) {
        return LocalDate.now().isAfter(start) && LocalDate.now().isBefore(end);
    }

    public static boolean isLeadTimeExcedeDeadline(Integer leadTime, LocalDate deadline) {
        return LocalDate.now().plusDays(leadTime).isAfter(deadline);
    }
}
