package org.weixin.framework.common.toolkit.future;

import java.util.concurrent.CompletableFuture;

/**
 *
 */
public class CompletableRunnable implements Runnable {

    private final Runnable runnable;

    private CompletableFuture<Void> completableFuture;

    public CompletableRunnable(Runnable runnable) {
        this.runnable = runnable;
        this.completableFuture = new CompletableFuture<>();
    }

    public CompletableRunnable(Runnable runnable, CompletableFuture<Void> completableFuture) {
        this.runnable = runnable;
        this.completableFuture = completableFuture;
    }

    @Override
    public void run() {
        try {
            runnable.run();
            completableFuture.complete(null);
        } catch (Exception e) {
            completableFuture.completeExceptionally(e);
        }
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public CompletableFuture<Void> getCompletableFuture() {
        return completableFuture;
    }

    public void setCompletableFuture(CompletableFuture<Void> completableFuture) {
        this.completableFuture = completableFuture;
    }
}
