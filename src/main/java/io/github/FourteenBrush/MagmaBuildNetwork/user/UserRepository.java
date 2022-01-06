package io.github.FourteenBrush.MagmaBuildNetwork.user;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class UserRepository implements Iterable<User> {
    private final Map<UUID, User> map;
    private final UserFactory factory;

    public UserRepository(Map<UUID, User> map, UserFactory factory) {
        this.map = map;
        this.factory = factory;
    }

    @NotNull
    public User getOrCreate(UUID uuid) {
        return map.computeIfAbsent(uuid, factory::newUser);
    }

    @Nullable
    public User getIfPresent(UUID uuid) {
        return (map.get(uuid));
    }

    @NotNull
    @Override
    public Iterator<User> iterator() {
        return map.values().iterator();
    }
}
