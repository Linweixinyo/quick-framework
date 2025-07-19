package org.weixin.framework.common.toolkit.future;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public class CompletableCallable<V> implements Callable<V> {

    private final Callable<V> callable;

    private CompletableFuture<V> completableFuture;

    public CompletableCallable(Callable<V> callable) {
        this.callable = callable;
        this.completableFuture = new CompletableFuture<>();
    }

    public CompletableCallable(Callable<V> callable, CompletableFuture<V> completableFuture) {
        this.callable = callable;
        this.completableFuture = completableFuture;
    }

    @Override
    public V call() {
        try {
            V result = callable.call();
            completableFuture.complete(result);
            return result;
        } catch (Exception e) {
            completableFuture.completeExceptionally(e);
        }
        return null;
    }

    public Callable<V> getCallable() {
        return callable;
    }

    public CompletableFuture<V> getCompletableFuture() {
        return completableFuture;
    }

    public void setCompletableFuture(CompletableFuture<V> completableFuture) {
        this.completableFuture = completableFuture;
    }
}
