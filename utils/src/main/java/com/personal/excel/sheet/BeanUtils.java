package com.personal.excel.sheet;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.concurrent.TimeUnit;

/**
 * @author qianliao.zhuang
 */
@Slf4j
public final class BeanUtils {

    private static final LoadingCache<MethodCacheKey, MethodHandle> METHOD_CACHE = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build(new CacheLoader<MethodCacheKey, MethodHandle>() {
                @Override
                public MethodHandle load(MethodCacheKey key) throws Exception {
                    return MethodHandles.lookup().findVirtual(key.getClazz(), key.getMethodName(), key.getMethodType());
                }
            });

    public static Object doInvoke(Class<?> clazz, String methodName, MethodType methodType,
                                     Object o, Object params) {
        try {
            MethodCacheKey key = new MethodCacheKey().setClazz(clazz).setMethodName(methodName).setMethodType(methodType);
            MethodHandle method = METHOD_CACHE.get(key);
            if (isGetter(methodName)) {
                return method.invokeWithArguments(o);
            }
            return method.invokeWithArguments(o, params);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            // can not be happened in runtime lessly
            log.error("no such method: {}. class: {}", methodName, clazz.getName(), e);
            throw new RuntimeException("no such method");
        } catch (Throwable e) {
            // can not be happened in runtime lessly
            log.error("invoke method: {} failed! Class: {}", methodName, clazz.getName(), e);
            throw new RuntimeException("invoke method failed");
        }
    }

    public static <T> T newInstance(Class<T> pojo) {
        try {
            return pojo.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("create class: {} instance failed!", pojo.getName(), e);
        }
        throw new RuntimeException("create class instance failed");
    }

    private static boolean isGetter(String methodName) {
        return methodName.startsWith("get");
    }

    @Accessors(chain = true)
    @Data
    private static class MethodCacheKey {
        private Class clazz;
        private String methodName;
        private MethodType methodType;
    }
}
