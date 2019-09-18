package com.personal.java8;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author huanying
 */
@Slf4j
public class FutureUtils {
    private FutureUtils() {}

    private static final ThreadFactory FUTURE_THREAD_FACTORY = new ThreadFactoryBuilder().setNameFormat("future-pool-%s").build();
    private static final ExecutorService EXECUTOR =
            new ThreadPoolExecutor(
                    20, 30, 1000, TimeUnit.MILLISECONDS,
                    new LinkedBlockingDeque<>(), FUTURE_THREAD_FACTORY
            );

    public static <U> List<U> processFutureNew(List<Supplier<U>> tasks) {
        return processFutureNew(tasks, FutureUtils.<U>defaultExceptionHandler());
    }

    public static <U> List<U> processFutureNew(List<Supplier<U>> tasks,
                                            Function<Throwable, ? extends U> perTaskThrowableFunction) {
        return processFutureAndMerge(tasks, perTaskThrowableFunction, Function.identity());
    }

    public static <R, U> R processFutureAndMerge(List<Supplier<U>> tasks,
                                                 Function<List<U>, R> mergeFunction) {
        return processFutureAndMerge(tasks, FutureUtils.<U>defaultExceptionHandler(), mergeFunction);
    }

    public static <R, U> R processFutureAndMerge(List<Supplier<U>> tasks,
                                                 Function<Throwable, ? extends U> perTaskThrowableFunction,
                                                 Function<List<U>, R> mergeFunction) {
        @SuppressWarnings("unchecked")
        CompletableFuture<U>[] futureTasks = tasks.stream()
                .map(task -> CompletableFuture.supplyAsync(task, EXECUTOR).exceptionally(perTaskThrowableFunction))
                .toArray(CompletableFuture[]::new);

        return CompletableFuture.allOf(futureTasks)
                .handleAsync((aVoid, throwable) -> {
                    List<U> results = Arrays.stream(futureTasks)
                            .map(CompletableFuture::join)
                            .collect(Collectors.toList());
                    return mergeFunction.apply(results);
                }, EXECUTOR)
                .join();
    }

    private static <U> Function<Throwable, ? extends U> defaultExceptionHandler() {
        return throwable -> {throw new CompletionException(throwable);};
    }
}