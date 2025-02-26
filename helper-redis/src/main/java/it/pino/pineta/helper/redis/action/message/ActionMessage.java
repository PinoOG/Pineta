package it.pino.pineta.helper.redis.action.message;

import it.pino.pineta.helper.redis.action.RedisAction;
import it.pino.pineta.helper.redis.action.instance.RedisInstance;
import it.pino.pineta.helper.redis.action.instance.space.Namespace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface ActionMessage<T extends RedisAction> {

    @NotNull Namespace getNamespace();

    /**
     * Gets the unique identifier of this message.
     *
     * @return the unique identifier
     */
    @NotNull UUID getUniqueID();

    /**
     * Gets the sender of this message.
     *
     * @return the sender
     */
    @NotNull RedisInstance getSender();

    /**
     * Nullability of the receiver is intended for actions that should be broadcasted to all instances.
     * @return the receiver
     */
    @Nullable RedisInstance getReceiver();

    /**
     * Gets the action.
     *
     * @return the action
     */
    T getAction();
}
