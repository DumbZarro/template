package top.dumbzarro.template.common.util;


import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Objects;
import java.util.StringJoiner;

public class StringUtil {

    public final static String ELLIPSIS = "...";

    public static String truncate(String input, int maxCodePoint) {
        if (Objects.isNull(input)) {
            return null;
        }
        int codePointCount = input.codePointCount(0, input.length());
        if (codePointCount <= maxCodePoint) {
            return input;
        }

        int ellipsisCount = ELLIPSIS.codePointCount(0, ELLIPSIS.length());
        if (maxCodePoint <= ellipsisCount) {
            int endEllipsis = ELLIPSIS.offsetByCodePoints(0, maxCodePoint);
            return ELLIPSIS.substring(0, endEllipsis);
        }
        int endIndex = input.offsetByCodePoints(0, maxCodePoint - ellipsisCount);
        return input.substring(0, endIndex) + ELLIPSIS;
    }

    public static String toString(Collection<String> set, String delimiter) {
        if (CollectionUtils.isEmpty(set)) {
            return null;
        }
        StringJoiner joiner = new StringJoiner(delimiter, "[", "]");
        for (String item : set) {
            joiner.add(item);
        }
        return joiner.toString();
    }

}