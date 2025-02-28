package it.pino.pineta.helper.redis.action.request.message;

import com.alibaba.fastjson2.annotation.JSONField;
import it.pino.pineta.helper.redis.action.callback.RedisCallback;
import it.pino.pineta.helper.redis.action.instance.RedisInstance;
import it.pino.pineta.helper.redis.action.instance.space.Namespace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public final class RedisRequestMessage<T extends RedisCallback> implements RequestMessage<T> {

    @JSONField(name = "request-type")
    private final @NotNull RequestType requestType;
    @JSONField(name = "namespace")
    private final @NotNull Namespace namespace;
    @JSONField(name = "unique-id")
    private final @NotNull UUID uniqueID;
    @JSONField(name = "sender")
    private final @NotNull RedisInstance sender;
    @JSONField(name = "receiver")
    private final @Nullable RedisInstance receiver;
    @JSONField(name = "callback")
    private final @Nullable T callback;

    RedisRequestMessage(final @NotNull RequestType requestType,
                               final @NotNull Namespace namespace,
                               final @NotNull UUID uniqueID,
                               final @NotNull RedisInstance sender,
                               final @Nullable RedisInstance receiver,
                               final @Nullable T callback)
    {
        this.requestType = requestType;
        this.namespace = namespace;
        this.uniqueID = uniqueID;
        this.sender = sender;
        this.receiver = receiver;
        this.callback = callback;
    }


    @Override
    public @NotNull RequestType getRequestType() {
        return this.requestType;
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
    public @Nullable T getCallback() {
        return this.callback;
    }


    // Make a Builder of this class
    public static final class Builder<T extends RedisCallback> {
        private RequestType requestType;
        private Namespace namespace;
        private UUID uniqueID;
        private RedisInstance sender;
        private RedisInstance receiver;
        private T callback;

        public Builder<T> setRequestType(final @NotNull RequestType requestType) {
            this.requestType = requestType;
            return this;
        }

        public Builder<T> setNamespace(final @NotNull Namespace namespace) {
            this.namespace = namespace;
            return this;
        }

        public Builder<T> setUniqueID(final @NotNull UUID uniqueID) {
            this.uniqueID = uniqueID;
            return this;
        }

        public Builder<T> setSender(final @NotNull RedisInstance sender) {
            this.sender = sender;
            return this;
        }

        public Builder<T> setReceiver(final @Nullable RedisInstance receiver) {
            this.receiver = receiver;
            return this;
        }

        public Builder<T> setCallback(final @Nullable T callback) {
            this.callback = callback;
            return this;
        }

        public RedisRequestMessage<T> build() {
            Objects.requireNonNull(requestType, "RequestType cannot be null");
            Objects.requireNonNull(namespace, "Namespace cannot be null");
            Objects.requireNonNull(sender, "Sender cannot be null");

            if(uniqueID == null) this.uniqueID = UUID.randomUUID();

            return new RedisRequestMessage<>(requestType, namespace, uniqueID, sender, receiver, callback);
        }
    }
}
