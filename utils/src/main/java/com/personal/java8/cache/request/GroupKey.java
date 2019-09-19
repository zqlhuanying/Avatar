package com.personal.java8.cache.request;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

/**
 * @author huanying
 */
@Getter
@Setter
public class GroupKey {

    private static final String SEPARATOR = "_";

    private CacheTimeUnitEnum timeUnit;
    private String key;

    public GroupKey(String key) {
        this(CacheTimeUnitEnum.ONE_SECONDS, key);
    }

    public GroupKey(CacheTimeUnitEnum timeUnit, String key) {
        this.timeUnit = timeUnit;
        this.key = key;
    }

    public static GroupKey simpleGenerate(CacheTimeUnitEnum timeUnit, String key, String... otherKeys) {
        List<String> k = Lists.newArrayList(key);
        k.addAll(Arrays.asList(otherKeys));

        String finalKey = String.join(SEPARATOR, k);
        return new GroupKey(timeUnit, finalKey);
    }
}
