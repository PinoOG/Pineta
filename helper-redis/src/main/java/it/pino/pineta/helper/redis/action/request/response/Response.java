package it.pino.pineta.helper.redis.action.request.response;

import it.pino.pineta.helper.redis.action.callback.RedisCallback;
import it.pino.pineta.helper.redis.action.instance.RedisInstance;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface Response<T extends RedisCallback> {

    @NotNull UUID uniqueID();

    @NotNull RedisInstance getReplier();

    @NotNull T get();
}
