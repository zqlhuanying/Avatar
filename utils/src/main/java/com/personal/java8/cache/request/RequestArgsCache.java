package com.personal.java8.cache.request;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

/**
 * @author huanying
 * 构建以时间为单位的缓存序列
 */
public class RequestArgsCache {

    private static final Object EMPTY = new Object();
    private static final CacheTimeUnitEnum DEFAULT_CACHE_TIME = CacheTimeUnitEnum.ONE_SECONDS;
    private static final Predicate<CacheTimeUnitEnum> IS_NOT_DEFAULT_CACHE_TIME = (cacheTime) -> cacheTime != DEFAULT_CACHE_TIME;
    private static final LoadingCache<CacheTimeUnitEnum, Cache<String, Object>> TIME_CACHE = CacheBuilder.newBuilder()
            .maximumSize(50)
            .build(new CacheLoader<CacheTimeUnitEnum, Cache<String, Object>>() {
                @Override
                public Cache<String, Object> load(CacheTimeUnitEnum key) throws Exception {
                    return newCache(key);
                }
            });
    private static final RequestArgsCache INSTANCE = new RequestArgsCache();

    public static RequestArgsCache getInstance() {
        return INSTANCE;
    }

    public Object get(GroupKey groupKey, Callable<?> loader) {
        return get(groupKey, loader, true);
    }

    public Object get(GroupKey groupKey, Callable<?> loader, boolean cacheIfAbsent) {
        // 假如不是默认的缓存时间，则优先从默认缓存时间中获取数据
        // 因为获取到数据为Null时，会将结果值添加到该容器中
        if (IS_NOT_DEFAULT_CACHE_TIME.test(groupKey.getTimeUnit())) {
            Object cacheValue = defaultCache().getIfPresent(groupKey.getKey());
            if (cacheValue != null) {
                return unwrap(cacheValue);
            }
        }
        Cache<String, Object> contentCache = TIME_CACHE.getUnchecked(groupKey.getTimeUnit());
        Object cacheValue;
        try {
            cacheValue = contentCache.get(groupKey.getKey(), () -> {
                Object value = wrap(loader.call());
                // 假如获取到Null，则减少缓存时间
                if (value == EMPTY && cacheIfAbsent && IS_NOT_DEFAULT_CACHE_TIME.test(groupKey.getTimeUnit())) {
                    defaultCache().put(groupKey.getKey(), value);
                }
                return value;
            });
        } catch (ExecutionException e) {
            throw new UncheckedExecutionException(e.getCause());
        }
        if (cacheValue == EMPTY) {
            if (!cacheIfAbsent || IS_NOT_DEFAULT_CACHE_TIME.test(groupKey.getTimeUnit())) {
                contentCache.invalidate(groupKey.getKey());
            }
        }
        return unwrap(cacheValue);
    }

    public void invalidate(GroupKey groupKey) {
        Cache<String, Object> contentCache = TIME_CACHE.getUnchecked(groupKey.getTimeUnit());
        contentCache.invalidate(groupKey.getKey());
        if (IS_NOT_DEFAULT_CACHE_TIME.test(groupKey.getTimeUnit())) {
            defaultCache().invalidate(groupKey.getKey());
        }
    }

    private Object wrap(Object value) {
        return value == null ? EMPTY : value;
    }

    private Object unwrap(Object value) {
        return value == EMPTY ? null : value;
    }

    private static Cache<String, Object> defaultCache() {
        return TIME_CACHE.getUnchecked(DEFAULT_CACHE_TIME);
    }

    private static Cache<String, Object> newCache(CacheTimeUnitEnum timeUnit) {
        return CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(timeUnit.getDuration(), timeUnit.getUnit())
                .build();
    }
}
