package it.pino.pineta.helper.redis.action.instance;

import com.alibaba.fastjson2.annotation.JSONField;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class RedisInstance {

    @JSONField(name = "instance-id")
    private final @NotNull String identifier;

    private RedisInstance(final @NotNull String identifier) {
        this.identifier = identifier;
    }

    public static RedisInstance of(final @NotNull String identifier) {
        return new RedisInstance(identifier);
    }

    public @NotNull String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RedisInstance that = (RedisInstance) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(identifier);
    }
}
