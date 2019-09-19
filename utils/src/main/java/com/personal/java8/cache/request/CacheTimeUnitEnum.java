package com.personal.java8.cache.request;

import lombok.Getter;

import java.util.concurrent.TimeUnit;

/**
 * @author huanying
 */
public enum CacheTimeUnitEnum {
    /**
     * 1秒
     */
    ONE_SECONDS(1, TimeUnit.SECONDS),
    /**
     * 5秒
     */
    FIVE_SECONDS(5, TimeUnit.SECONDS),
    /**
     * 10秒
     */
    TEN_SECONDS(10, TimeUnit.SECONDS),
    /**
     * 30秒
     */
    THIRTY_SECONDS(30, TimeUnit.SECONDS),
    /**
     * 1分钟
     */
    ONE_MINUTE(1, TimeUnit.MINUTES),
    /**
     * 5分钟
     */
    FIVE_MINUTE(5, TimeUnit.MINUTES),
    /**
     * 10分钟
     */
    TEN_MINUTE(10, TimeUnit.MINUTES),
    /**
     * 30分钟
     */
    THIRTY_MINUTE(30, TimeUnit.MINUTES),
    /**
     * 1小时
     */
    ONE_HOUR(1, TimeUnit.HOURS);

    @Getter
    private long duration;
    @Getter
    private TimeUnit unit;

    CacheTimeUnitEnum(long duration, TimeUnit unit) {
        this.duration = duration;
        this.unit = unit;
    }
}
