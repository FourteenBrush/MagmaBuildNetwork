package io.github.FourteenBrush.MagmaBuildNetwork.user;

import java.util.UUID;

@FunctionalInterface
public interface UserFactory {

    User newUser(UUID uuid);
}
