package it.pino.pineta.helper.redis.action.request;

import com.alibaba.fastjson2.JSONObject;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import it.pino.pineta.helper.redis.ConnectionProvider;
import it.pino.pineta.helper.redis.action.callback.RedisCallback;
import it.pino.pineta.helper.redis.action.instance.RedisInstance;
import it.pino.pineta.helper.redis.action.instance.space.Namespace;
import it.pino.pineta.helper.redis.action.request.message.RedisRequestMessage;
import it.pino.pineta.helper.redis.action.request.message.RequestType;
import it.pino.pineta.helper.redis.action.request.registration.RequestSubscriberRegistration;
import it.pino.pineta.helper.redis.action.request.registration.handler.RequestSupplier;
import it.pino.pineta.helper.redis.action.request.response.Response;
import it.pino.pineta.helper.redis.action.publisher.listener.RedisActionListener;
import it.pino.pineta.helper.redis.channel.RedisChannel;
import it.pino.pineta.helper.redis.exception.IncomingActionException;
import it.pino.pineta.helper.redis.exception.PublishFailureException;
import it.pino.pineta.helper.redis.exception.RequestTimeoutException;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

public abstract class RedisRequestPublisher implements RequestPublisher {

    private final @NotNull ConnectionProvider connectionProvider;

    private final @NotNull StatefulRedisPubSubConnection<String, String> pubSubConnection;

    private final @NotNull RedisPubSubAsyncCommands<String, String> asyncCommands;

    private final @NotNull RedisChannel listeningChannel;

    private final @NotNull RedisActionListener listener;

    private final @NotNull RedisInstance currentInstance;

    private final @NotNull Map<Namespace, RequestSubscriberRegistration<?>> registrations = new ConcurrentHashMap<>();

    @SuppressWarnings("rawtypes")
    private final @NotNull Map<UUID, CompletionStage> pendingCallbacks = new ConcurrentHashMap<>();

    protected RedisRequestPublisher(final @NotNull ConnectionProvider connectionProvider,
                                    final @NotNull RedisChannel listeningChannel,
                                    final @NotNull ExecutorService executor,
                                    final @NotNull RedisInstance currentInstance) {
        this.connectionProvider = connectionProvider;
        this.pubSubConnection = connectionProvider.getConnectionPubSub();
        this.asyncCommands = pubSubConnection.async();
        this.listeningChannel = listeningChannel;
        this.currentInstance = currentInstance;

        this.listener = (channel, message) -> {
            if (!RedisChannel.of(channel).equals(listeningChannel)) return;

            executor.execute(() -> handle(message));
        };
    }

    private void handle(final String message) {
        try {
            final var callbackMessage = JSONObject.parseObject(message, RedisRequestMessage.class);
            final var requestType = callbackMessage.getRequestType();
            final var receiver = callbackMessage.getReceiver();
            final var sender = callbackMessage.getSender();
            final var uuid = callbackMessage.getUniqueID();

            if (requestType == RequestType.RESPONSE) {
                if (receiver == null || !receiver.equals(currentInstance)) return;
                if (pendingCallbacks.get(uuid) == null) return;
                handleResponse(callbackMessage, message);

            } else if (requestType == RequestType.REQUEST) {
                if (sender.equals(currentInstance)) return;
                handleRequest(callbackMessage);
            }
        } catch (Exception ex) {
            throw new IncomingActionException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    private void handleResponse(final @NotNull RedisRequestMessage<?> callbackMessage, final @NotNull String message) {
        final var namespace = callbackMessage.getNamespace();
        final var registration = registrations.get(namespace);
        if (registration == null) return;

        final var id = callbackMessage.getUniqueID();
        final var stage = pendingCallbacks.get(id);
        if (stage != null) {
            final var response = registration.getResponse(callbackMessage, message);
            stage.toCompletableFuture().complete(response);
            pendingCallbacks.remove(id);
        }
    }

    private void handleRequest(final @NotNull RedisRequestMessage<?> callbackMessage) {
        final var namespace = callbackMessage.getNamespace();
        final var registration = registrations.get(namespace);
        if (registration == null) return; // Maybe log this?

        registration.handleRequest(callbackMessage, currentInstance).ifPresent(response -> {
            final var jsonMessage = JSONObject.toJSONString(response);
            asyncCommands.publish(listeningChannel.getName(), jsonMessage);
        });
    }

    public void subscribe() {
        pubSubConnection.addListener(listener);
        asyncCommands.subscribe(listeningChannel.getName());
    }

    public void unsubscribe() {
        pubSubConnection.removeListener(listener);
        asyncCommands.unsubscribe(listeningChannel.getName());
        pendingCallbacks.clear();
    }

    @Override
    public <C extends RedisCallback> CompletionStage<Response<C>> waitRequest(final Class<C> callback, final int timeout, final TimeUnit timeoutUnit) {
        return waitRequest(callback, null, timeout, timeoutUnit);
    }

    @Override
    public <C extends RedisCallback> CompletionStage<Response<C>> waitRequest(final Class<C> callback, final RedisInstance target, final int timeout, final TimeUnit timeoutUnit) {

        final var namespace = Namespace.ofCallback(callback);

        final var callbackMessage = new RedisRequestMessage.Builder<C>()
                .setRequestType(RequestType.REQUEST).setNamespace(namespace).setSender(currentInstance).setReceiver(target)
                .build();

        final var id = callbackMessage.getUniqueID();
        final var stage = new CompletableFuture<Response<C>>();
        try (final var connection = this.connectionProvider.getConnection()) {

            final var jsonMessage = JSONObject.toJSONString(callbackMessage);
            pendingCallbacks.put(id, stage);

            connection.async().publish(listeningChannel.getName(), jsonMessage);
        } catch (Exception ex) {
            return CompletableFuture.failedStage(new PublishFailureException(ex));
        }

        return stage.orTimeout(timeout, timeoutUnit).exceptionally(ex -> {
            pendingCallbacks.remove(id);
            throw new RequestTimeoutException("Request did not receive a response in " + timeout + " " + timeoutUnit.toString().toLowerCase());
        });
    }

    @Override
    public <T extends RedisCallback> void registerSupplier(final @NotNull Class<T> clazz, final @NotNull RequestSupplier<T> supplier) {
        final var registration = new RequestSubscriberRegistration<>(clazz, supplier);
        registerSupplier(registration);
    }

    @Override
    public <T extends RedisCallback> void registerSupplier(final @NotNull RequestSubscriberRegistration<T> registration) {
        final var namespace = Namespace.ofCallback(registration.getCallbackClass());
        registrations.put(namespace, registration);
    }
}
