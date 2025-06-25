package it.pino.pineta.helper.redis.action.publisher.message;

import com.alibaba.fastjson2.annotation.JSONField;
import it.pino.pineta.helper.redis.action.RedisAction;
import it.pino.pineta.helper.redis.action.instance.RedisInstance;
import it.pino.pineta.helper.redis.action.instance.space.Namespace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class RedisActionMessage<T extends RedisAction> implements ActionMessage<T>{

    @JSONField(name = "namespace")
    private final @NotNull Namespace namespace;
    @JSONField(name = "unique-id")
    private final @NotNull UUID uniqueID;
    @JSONField(name = "sender")
    private final @NotNull RedisInstance sender;
    @JSONField(name = "receiver")
    private final @Nullable RedisInstance receiver;
    @JSONField(name = "action")
    private final @NotNull T action;

    private RedisActionMessage(@NotNull Namespace namespace, @NotNull UUID uniqueID, @NotNull RedisInstance sender, @Nullable RedisInstance receiver, @NotNull T action) {
        this.namespace = namespace;
        this.uniqueID = uniqueID;
        this.sender = sender;
        this.receiver = receiver;
        this.action = action;
    }

    public RedisActionMessage(@NotNull RedisInstance sender, @Nullable RedisInstance receiver, @NotNull T action, @NotNull Namespace namespace) {
        this(namespace, UUID.randomUUID(), sender, receiver, action);
    }

    public RedisActionMessage(@NotNull RedisInstance sender, @NotNull T action, @NotNull Namespace namespace) {
        this(namespace, UUID.randomUUID(), sender, null, action);
    }


    @Override
    public @NotNull Namespace getNamespace() {
        return this.namespace;
    }

    @Override
    public @NotNull UUID getUniqueID() {
        return this.uniqueID;
    }

    @Override
    public @NotNull RedisInstance getSender() {
        return this.sender;
    }

    @Override
    public @Nullable RedisInstance getReceiver() {
        return this.receiver;
    }

    @Override
    public @NotNull T getAction() {
        return this.action;
    }
}
