package com.personal;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import jdk.nashorn.internal.runtime.ParserException;
import org.joda.time.format.DateTimeFormat;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * @author qianliao.zhuang
 */
public final class FunctionUtils {
    private FunctionUtils() {}

    private static final Function<String, Long> STRING_TO_LONG_FUNCTION = new Function<String, Long>() {
        @Override
        public Long apply(String input) {
            return Long.parseLong(input);
        }
    };

    private static final Function<String, Integer> STRING_TO_INTEGER_FUNCTION = new Function<String, Integer>() {
        @Override
        public Integer apply(String input) {
            return Integer.parseInt(input);
        }
    };

    private static final Function<String, Boolean> STRING_TO_BOOLEAN_FUNCTION = new Function<String, Boolean>() {
        @Override
        public Boolean apply(String input) {
            return Boolean.valueOf(input);
        }
    };

    private static final Function<String, Map<String, String>> STRING_TO_MAP = new Function<String, Map<String, String>>() {
        private final Splitter.MapSplitter mapSplitter = Splitter.on(",").withKeyValueSeparator("=");
        @Override
        public Map<String, String> apply(String input) {
            return mapSplitter.split(input);
        }
    };

    public static Function<String, Long> stringToLong() {
        return STRING_TO_LONG_FUNCTION;
    }

    public static Function<String, Integer> stringToInteger() {
        return STRING_TO_INTEGER_FUNCTION;
    }

    public static Function<String, Boolean> stringToBoolean() {
        return STRING_TO_BOOLEAN_FUNCTION;
    }

    public static Function<String, Date> stringToDate() {
        return StringToDateFunction.INSTANCE;
    }

    public static Function<String, Map<String, String>> stringToMap() {
        return STRING_TO_MAP;
    }

    /**
     * Function 将数字转成字符串
     * @param <F>: Number
     * @return: Function
     */
    @SuppressWarnings("unchecked")
    public static <F extends Number> Function<F, String> numberToString() {
        return (Function<F, String>) NumberToStringFunction.INSTANCE;
    }

    private enum NumberToStringFunction implements Function<Number, String> {
        /**
         * NumberToString Instance
         */
        INSTANCE;

        @Override
        public String apply(Number input) {
            return String.valueOf(input);
        }
    }

    private enum StringToDateFunction implements Function<String, Date>{
        /**
         * Date Parser Instance
         */
        INSTANCE;

        private static final String LONG_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
        private static final String MEDIUM_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
        private static final String SHORT_DATE_FORMAT = "yyyy-MM-dd";

        @Override
        public Date apply(String input) {
            return parse(input);
        }

        private Date parse(String input) throws ParserException {
            input = input.trim();
            int length = input.length();

            if (length == LONG_DATE_FORMAT.length()) {
                return parse(input, LONG_DATE_FORMAT);
            }

            if (length == MEDIUM_DATE_FORMAT.length()) {
                return parse(input, MEDIUM_DATE_FORMAT);
            }

            return parse(input, SHORT_DATE_FORMAT);
        }

        private Date parse(String input, String format) throws ParserException {
            return DateTimeFormat
                    .forPattern(format)
                    .withLocale(Locale.US)
                    .parseDateTime(input)
                    .toDate();
        }
    }
}
