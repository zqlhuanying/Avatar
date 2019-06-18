package com.personal.java8;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author qianliao.zhuang
 */
public final class EnumUtils {
    public EnumUtils() {}

    /**
     * 根据关键字获取对应的枚举
     * @param enumClass the enum class
     * @param function the supplier of key.
     *                 一般是枚举类中的Code，Type等唯一可以进行区分的函数
     * @param expected the expected key
     * @return the expected enum
     */
    public static <E extends Enum<E>, T> E getEnum(final Class<E> enumClass,
                                                   final Function<E, T> function,
                                                   final T expected) {
        if (expected == null) {
            return null;
        }
        E[] values = enumClass.getEnumConstants();
        return Arrays.stream(values)
                .filter(v -> Objects.equals(function.apply(v), expected))
                .findFirst()
                .orElse(null);
    }

    /**
     * 根据关键字获取对应的枚举
     * 假如找不到，抛异常
     * @param enumClass the enum class
     * @param function the supplier of key.
     *                 一般是枚举类中的Code，Type等唯一可以进行区分的函数
     * @param expected the expected key
     * @return the expected enum
     */
    public static <E extends Enum<E>, T> E getEnumOrException(final Class<E> enumClass,
                                                              final Function<E, T> function,
                                                              final T expected) {
        E e = getEnum(enumClass, function, expected);
        if (e == null) {
            throw new IllegalArgumentException(
                    String.format("Expected value[%s] is not supported by enum[%s]",
                            expected, enumClass.getSimpleName())
            );
        }
        return e;
    }
}
