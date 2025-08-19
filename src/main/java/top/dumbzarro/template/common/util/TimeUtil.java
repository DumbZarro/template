package top.dumbzarro.template.common.util;

import io.micrometer.common.util.StringUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class TimeUtil {
    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    public static final Duration Duration_15_minutes = Duration.ofMinutes(15);

    public static String formatDate(LocalDate date, String format) {
        if (Objects.isNull(date)) {
            return null;
        }
        return date.format(DateTimeFormatter.ofPattern(TimeUtil.YYYY_MM_DD_HH_MM_SS));
    }

    public static LocalDate parseDateByFormat(String text, String format) {
        if (StringUtils.isEmpty(text) || StringUtils.isEmpty(format)) {
            return null;
        }
        return LocalDate.parse(text, DateTimeFormatter.ofPattern(format));
    }
}
