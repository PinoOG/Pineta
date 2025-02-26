package it.pino.pineta.helper.redis.channel;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class RedisChannel {

    @NotNull
    private final String channel;

    private RedisChannel(@NotNull String channel) {
        this.channel = channel;
    }

    public static RedisChannel of(@NotNull String name) {
        String[] split = name.split(":");
        if (split.length < 2) {
            throw new IllegalArgumentException("Channel must be in the format of 'channel:action'");
        }
        return new RedisChannel(name);
    }

    @NotNull
    public String getName() {
        return channel;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RedisChannel that = (RedisChannel) o;
        return Objects.equals(channel, that.channel);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(channel);
    }
}
