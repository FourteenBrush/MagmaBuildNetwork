package io.github.FourteenBrush.MagmaBuildNetwork.utils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class DelegatingStorage<D, I> {
    private final Storage<D, I> storage;
    private final Executor executor;

    public DelegatingStorage(Storage<D, I> storage, Executor executor) {
        this.storage = storage;
        this.executor = executor;
    }

    public void startup() {
        storage.startup();
    }

    public void shutdown() {
        storage.shutdown();
    }

    public CompletableFuture<Void> load(I identifier) {
        return CompletableFuture.runAsync(() -> storage.load(identifier), executor);
    }

    public CompletableFuture<Void> save(D data) {
        return CompletableFuture.runAsync(() -> storage.save(data), executor);
    }
}
