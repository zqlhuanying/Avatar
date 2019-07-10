package com.personal.java8;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author huanying
 */
@Service
public class MethodCache {

    private static final Cache<MethodCacheKey, MethodHandle> METHOD_CACHE = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build();

    private static final UnaryOperator<MethodCacheKey> LOOK_UP_METHOD_TYPE = cacheKey ->
            new MethodCacheKey()
                    .setClazz(cacheKey.getClazz())
                    .setMethodName(cacheKey.getMethodName())
                    .setMethodType(lookup(cacheKey.getClazz(), cacheKey.getMethodName(), cacheKey.getArgs()));

    private static final Function<MethodCacheKey, MethodHandle> VIRTUAL = cacheKey -> {
        try {
            return MethodHandles.lookup().findVirtual(cacheKey.getClazz(), cacheKey.getMethodName(), cacheKey.getMethodType());
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    };

    private static final Function<MethodCacheKey, MethodHandle> STATIC = cacheKey -> {
        try {
            return MethodHandles.lookup().findStatic(cacheKey.getClazz(), cacheKey.getMethodName(), cacheKey.getMethodType());
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    };

    public Object invoke(Object o, String methodName, Object... params) {
        MethodCacheKey key = new MethodCacheKey()
                .setClazz(o.getClass())
                .setMethodName(methodName)
                .setArgs(params.length);
        MethodHandle methodHandle = getMethodHandle(key, LOOK_UP_METHOD_TYPE.andThen(VIRTUAL)).bindTo(o);
        return invokeWithArguments(methodHandle, params);
    }

    public Object invokeStatic(Class<?> clazz, String methodName, Object... params) {
        MethodCacheKey key = new MethodCacheKey()
                .setClazz(clazz)
                .setMethodName(methodName)
                .setArgs(params.length);
        MethodHandle methodHandle = getMethodHandle(key, LOOK_UP_METHOD_TYPE.andThen(STATIC));
        return invokeWithArguments(methodHandle, params);
    }

    private static MethodType lookup(Class<?> clazz, String methodName, int args) {
        Method method = Arrays.stream(clazz.getMethods())
                .filter(m -> m.getName().equals(methodName))
                .filter(m -> m.getParameterCount() == args)
                .findFirst()
                .orElse(null);
        checkNotNull(method, "class[%s] has no such method[%s]", clazz, methodName);

        Class<?>[] argsType = method.getParameterTypes();
        Class<?> returnType = method.getReturnType();
        return MethodType.methodType(returnType, argsType);
    }

    private MethodHandle getMethodHandle(MethodCacheKey cacheKey, Function<MethodCacheKey, MethodHandle> supplier) {
        try {
            return METHOD_CACHE.get(cacheKey, () -> supplier.apply(cacheKey));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private Object invokeWithArguments(MethodHandle methodHandle, Object... params) {
        try {
            return methodHandle.invokeWithArguments(params);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    @Data
    @Accessors(chain = true)
    private static class MethodCacheKey {
        private Class clazz;
        private String methodName;
        private MethodType methodType;
        private int args;
    }
}
