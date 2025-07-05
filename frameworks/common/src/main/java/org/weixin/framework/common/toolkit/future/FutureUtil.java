package org.weixin.framework.common.toolkit.future;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public final class FutureUtil {

    public static <T, R> CompletableFuture<List<R>> splitExecuteTask(List<T> requestParams, int splitNum, Function<List<T>, R> function, ExecutorService threadPoolExecutor) {
        if (CollectionUtil.isEmpty(requestParams)) {
            return success(Collections.emptyList());
        }
        List<List<T>> splitRequestParams = CollectionUtil.split(requestParams, splitNum);
        if (splitRequestParams.size() == 1) {
            return CompletableFuture.supplyAsync(() -> Collections.singletonList(function.apply(splitRequestParams.get(0))), threadPoolExecutor);
        }
        List<CompletableFuture<R>> splitFutures = splitRequestParams.stream()
                .map(requestParam -> CompletableFuture.supplyAsync(() -> function.apply(requestParam), threadPoolExecutor))
                .collect(Collectors.toList());
        return sequenceNonNull(splitFutures);
    }

    public static <T, R> CompletableFuture<List<R>> splitExecuteTask(List<T> requestParams, int splitNum, Function<List<T>, R> function,
                                                                     long timeout, TimeUnit timeUnit, ExecutorService threadPoolExecutor) {
        if (CollectionUtil.isEmpty(requestParams)) {
            return success(Collections.emptyList());
        }
        List<List<T>> splitRequestParams = CollectionUtil.split(requestParams, splitNum);
        if (splitRequestParams.size() == 1) {
            return CompletableFuture.supplyAsync(() -> Collections.singletonList(function.apply(splitRequestParams.get(0))), threadPoolExecutor);
        }
        List<CompletableFuture<R>> splitFutures = splitRequestParams.stream()
                .map(requestParam -> CompletableFuture.supplyAsync(() -> function.apply(requestParam), threadPoolExecutor))
                .collect(Collectors.toList());
        return sequenceNonNull(splitFutures, timeout, timeUnit);
    }


    public static <T> CompletableFuture<List<T>> sequenceNonNull(Collection<CompletableFuture<T>> completableFutures) {
        return CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture<?>[0]))
                .thenApply(v -> completableFutures.stream()
                        .map(CompletableFuture::join)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
                );
    }


    public static <T> CompletableFuture<List<T>> sequenceNonNull(Collection<CompletableFuture<T>> completableFutures, long timeout, TimeUnit timeUnit) {
        return CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture<?>[0]))
                .thenApply(v -> completableFutures.stream()
                        .map(CompletableFuture::join)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
                );
    }

    /**
     * 设置CF状态为失败
     */
    public static <T> CompletableFuture<T> failed(Throwable ex) {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        completableFuture.completeExceptionally(ex);
        return completableFuture;
    }

    /**
     * 设置CF状态为成功
     */
    public static <T> CompletableFuture<T> success(T result) {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        completableFuture.complete(result);
        return completableFuture;
    }

    /**
     * 提取真正的异常
     */
    public static Throwable extractRealException(Throwable throwable) {
        if (throwable instanceof CompletionException || throwable instanceof ExecutionException) {
            if (throwable.getCause() != null) {
                return throwable.getCause();
            }
        }
        return throwable;
    }

}