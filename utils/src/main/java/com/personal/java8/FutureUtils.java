package com.personal.java8;

import com.google.common.collect.Lists;
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
import java.util.function.Consumer;
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
                    150, 160, 1000, TimeUnit.MILLISECONDS,
                    new LinkedBlockingDeque<>(), FUTURE_THREAD_FACTORY
            );

    public static void processRunnable(Runnable task) {
        processRunnable(Lists.newArrayList(task));
    }

    public static void processRunnable(List<Runnable> tasks) {
        processRunnable(tasks, throwable -> {throw new CompletionException(throwable);});
    }

    public static void processRunnable(Runnable task, Consumer<Throwable> throwableFunction) {
        processRunnable(Lists.newArrayList(task), throwableFunction);
    }

    public static void processRunnable(List<Runnable> tasks, Consumer<Throwable> perTaskThrowableFunction) {
        tasks.forEach(
                task -> CompletableFuture.runAsync(task, EXECUTOR)
                        .exceptionally(t -> {perTaskThrowableFunction.accept(t); return null;})
        );
    }

    public static <U> CompletableFuture<U> processFutureWaiting(Supplier<U> task) {
        return processFutureWaiting(Lists.newArrayList(task))[0];
    }

    public static <U> CompletableFuture<U>[] processFutureWaiting(List<Supplier<U>> tasks) {
        return processFutureWaiting(tasks, FutureUtils.<U>defaultExceptionHandler());
    }

    public static <U> CompletableFuture<U> processFutureWaiting(Supplier<U> task,
                                                                Function<Throwable, ? extends U> throwableFunction) {
        return processFutureWaiting(Lists.newArrayList(task), throwableFunction)[0];
    }

    public static <U> CompletableFuture<U>[] processFutureWaiting(List<Supplier<U>> tasks,
                                                                  Function<Throwable, ? extends U> perTaskThrowableFunction) {
        return doProcess(tasks, perTaskThrowableFunction);
    }

    public static <U> U processFuture(Supplier<U> task) {
        return CollectionUtil.getOrDefault(
                processFuture(Lists.newArrayList(task)),
                0,
                null
        );
    }

    public static <U> List<U> processFuture(List<Supplier<U>> tasks) {
        return processFuture(tasks, FutureUtils.<U>defaultExceptionHandler());
    }

    public static <U> U processFuture(Supplier<U> task,
                                      Function<Throwable, ? extends U> throwableFunction) {
        return CollectionUtil.getOrDefault(
                processFuture(Lists.newArrayList(task), throwableFunction),
                0,
                null
        );
    }

    public static <U> List<U> processFuture(List<Supplier<U>> tasks,
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
        return FutureUtils.getAndMerge(FutureUtils.doProcess(tasks, perTaskThrowableFunction), mergeFunction);
    }

    public static <U> U get(CompletableFuture<U> futureTask) {
        @SuppressWarnings("unchecked")
        CompletableFuture<U>[] tasks = new CompletableFuture[]{futureTask};
        return CollectionUtil.getOrDefault(
                get(tasks),
                0,
                null
        );
    }

    public static <U> List<U> get(CompletableFuture<U>[] futureTasks) {
        return getAndMerge(futureTasks, Function.identity());
    }

    public static <R, U> R getAndMerge(CompletableFuture<U>[] futureTasks,
                                       Function<List<U>, R> mergeFunction) {
        return CompletableFuture.allOf(futureTasks)
                .handleAsync((aVoid, throwable) -> {
                    List<U> results = Arrays.stream(futureTasks)
                            .map(CompletableFuture::join)
                            .collect(Collectors.toList());
                    return mergeFunction.apply(results);
                }, EXECUTOR)
                .join();
    }

    @SuppressWarnings("unchecked")
    private static <U> CompletableFuture<U>[] doProcess(List<Supplier<U>> tasks,
                                                        Function<Throwable, ? extends U> perTaskThrowableFunction) {
        return tasks.stream()
                .map(task -> CompletableFuture.supplyAsync(task, EXECUTOR).exceptionally(perTaskThrowableFunction))
                .toArray(CompletableFuture[]::new);
    }

    private static <U> Function<Throwable, ? extends U> defaultExceptionHandler() {
        return throwable -> {throw new CompletionException(throwable);};
    }
}
