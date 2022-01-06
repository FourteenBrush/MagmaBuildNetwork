package io.github.FourteenBrush.MagmaBuildNetwork.utils;

public interface Storage<D, I> {
    void startup();

    void shutdown();

    void load(I data);

    void save(D data);
}
