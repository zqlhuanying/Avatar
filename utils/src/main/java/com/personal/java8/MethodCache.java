package com.personal.java8;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.UncheckedExecutionException;
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
import java.util.stream.IntStream;

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
                    .setMethodType(lookup0(cacheKey.getClazz(), cacheKey.getMethodName(), cacheKey.getParameterTypes()));

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
        Class<?>[] parameterTypes = new Class<?>[params.length];
        for (int i = 0; i < params.length; i++) {
            parameterTypes[i] = params[i].getClass();
        }
        MethodHandle methodHandle = lookup(o.getClass(), methodName, parameterTypes).bindTo(o);
        return invokeWithArguments(methodHandle, params);
    }

    public Object invokeSetter(Object o, String fieldName, Object param) {
        MethodHandle methodHandle = lookupSetter(o.getClass(), fieldName, param.getClass()).bindTo(o);
        return invokeWithArguments(methodHandle, param);
    }

    public Object invokeGetter(Object o, String fieldName) {
        MethodHandle methodHandle = lookupGetter(o.getClass(), fieldName).bindTo(o);
        return invokeWithArguments(methodHandle);
    }

    public Object invokeStatic(Class<?> clazz, String methodName, Object... params) {
        Class<?>[] parameterTypes = new Class<?>[params.length];
        for (int i = 0; i < params.length; i++) {
            parameterTypes[i] = params[i].getClass();
        }
        MethodHandle methodHandle = lookupStatic(clazz, methodName, parameterTypes);
        return invokeWithArguments(methodHandle, params);
    }

    public MethodHandle lookup(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        MethodCacheKey key = new MethodCacheKey()
                .setClazz(clazz)
                .setMethodName(methodName)
                .setParameterTypes(parameterTypes);
        return getMethodHandle(key, LOOK_UP_METHOD_TYPE.andThen(VIRTUAL));
    }

    public MethodHandle lookupGetter(Class<?> clazz, String fieldName) {
        String methodName = "get" + capitalize(fieldName);
        return lookup(clazz, methodName);
    }

    public MethodHandle lookupSetter(Class<?> clazz, String fieldName, Class<?> parameter) {
        String methodName = "set" + capitalize(fieldName);
        return lookup(clazz, methodName, parameter);
    }

    public MethodHandle lookupStatic(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        MethodCacheKey key = new MethodCacheKey()
                .setClazz(clazz)
                .setMethodName(methodName)
                .setParameterTypes(parameterTypes);
        return getMethodHandle(key, LOOK_UP_METHOD_TYPE.andThen(STATIC));
    }

    private MethodHandle getMethodHandle(MethodCacheKey cacheKey, Function<MethodCacheKey, MethodHandle> supplier) {
        try {
            return METHOD_CACHE.get(cacheKey, () -> supplier.apply(cacheKey));
        } catch (ExecutionException e) {
            throw new UncheckedExecutionException(e);
        }
    }

    private static MethodType lookup0(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        Method method = exactLookup(clazz, methodName, parameterTypes);
        if (method == null) {
            method = fuzzyLookup(clazz, methodName, parameterTypes);
        }
        checkNotNull(method, "class[%s] has no such method[%s]", clazz, methodName);

        Class<?>[] argsType = method.getParameterTypes();
        Class<?> returnType = method.getReturnType();
        return MethodType.methodType(returnType, argsType);
    }

    private static Method exactLookup(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            return clazz.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            // swallow
        }
        return null;
    }

    private static Method fuzzyLookup(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        return Arrays.stream(clazz.getMethods())
                .filter(m -> m.getName().equals(methodName))
                .filter(m -> m.getParameterCount() == parameterTypes.length)
                .filter(m ->
                    IntStream.range(0, m.getParameterCount())
                            .allMatch(i -> m.getParameterTypes()[i].isAssignableFrom(parameterTypes[i]))
                )
                .findFirst()
                .orElse(null);
    }

    private Object invokeWithArguments(MethodHandle methodHandle, Object... params) {
        try {
            return methodHandle.invokeWithArguments(params);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    private String capitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        if (name.length() > 1
                && Character.isUpperCase(name.charAt(0))){
            return name;
        }
        char[] chars = name.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    @Data
    @Accessors(chain = true)
    private static class MethodCacheKey {
        private Class<?> clazz;
        private String methodName;
        private MethodType methodType;
        private Class<?>[] parameterTypes;
    }
}
