package it.pino.pineta.helper.redis.action.request.message;

import it.pino.pineta.helper.redis.action.callback.RedisCallback;
import it.pino.pineta.helper.redis.action.instance.RedisInstance;
import it.pino.pineta.helper.redis.action.instance.space.Namespace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface RequestMessage<T extends RedisCallback> {

    @NotNull RequestType getRequestType();

    @NotNull Namespace getNamespace();

    /**
     * Gets the unique identifier ofAction this message.
     *
     * @return the unique identifier
     */
    @NotNull UUID getUniqueID();

    /**
     * Gets the sender ofAction this message.
     *
     * @return the sender
     */
    @NotNull RedisInstance getSender();

    /**
     * Nullability ofAction the receiver is intended for actions that should be broadcasted to all instances.
     * @return the receiver
     */
    @Nullable RedisInstance getReceiver();

    /**
     * Gets the action.
     *
     * @return the action
     */
    T getCallback();
}
