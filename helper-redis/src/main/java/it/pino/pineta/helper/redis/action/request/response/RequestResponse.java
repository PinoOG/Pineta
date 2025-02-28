package it.pino.pineta.helper.redis.action.request.response;

import it.pino.pineta.helper.redis.action.callback.RedisCallback;
import it.pino.pineta.helper.redis.action.instance.RedisInstance;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record RequestResponse<T extends RedisCallback>(@NotNull UUID uniqueID,
                                                       @NotNull RedisInstance sender,
                                                       @NotNull T response) implements Response<T> {

    @Override
    public @NotNull RedisInstance getReplier() {
        return sender;
    }

    @Override
    public @NotNull T get() {
        return response;
    }
}
