package it.pino.pineta.helper.redis.action.publisher;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import it.pino.pineta.helper.redis.ConnectionProvider;
import it.pino.pineta.helper.redis.action.RedisAction;
import it.pino.pineta.helper.redis.action.instance.RedisInstance;
import it.pino.pineta.helper.redis.action.instance.space.Namespace;
import it.pino.pineta.helper.redis.action.publisher.message.RedisActionMessage;
import it.pino.pineta.helper.redis.action.publisher.registration.ActionSubscriberRegistration;
import it.pino.pineta.helper.redis.action.publisher.registration.handler.ActionHandler;
import it.pino.pineta.helper.redis.action.publisher.listener.RedisActionListener;
import it.pino.pineta.helper.redis.channel.RedisChannel;
import it.pino.pineta.helper.redis.exception.IncomingActionException;
import it.pino.pineta.helper.redis.exception.PublishFailureException;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public abstract class RedisActionPublisher implements ActionPublisher {

    private final @NotNull ConnectionProvider connectionProvider;

    private final @NotNull StatefulRedisPubSubConnection<String, String> pubSubConnection;

    private final @NotNull RedisPubSubAsyncCommands<String, String> asyncCommands;

    private final @NotNull RedisChannel listeningChannel;

    private final @NotNull RedisActionListener listener;

    private final @NotNull RedisInstance currentInstance;

    private final @NotNull Map<Namespace, ActionSubscriberRegistration<?>> registrations;

    protected RedisActionPublisher(final @NotNull ConnectionProvider connectionProvider,
                                   final @NotNull RedisChannel listeningChannel,
                                   final @NotNull ExecutorService executor,
                                   final @NotNull RedisInstance currentInstance)
    {
        this.connectionProvider = connectionProvider;
        this.pubSubConnection = connectionProvider.getConnectionPubSub();
        this.asyncCommands = pubSubConnection.async();
        this.listeningChannel = listeningChannel;
        this.currentInstance = currentInstance;
        this.registrations = new ConcurrentHashMap<>();

        this.listener = (channel, message) -> {
            if (!RedisChannel.of(channel).equals(listeningChannel)) return;

            executor.execute(() -> handle(message));
        };
    }


    private void handle(final @NotNull String message) {
        try {
            final var json = JSONObject.parseObject(message);
            final var actionMessage = JSONObject.parseObject(message, RedisActionMessage.class);

            final var sender = actionMessage.getSender();
            if (sender.equals(currentInstance)) return;

            final var target = actionMessage.getReceiver();
            if(target != null && !target.equals(currentInstance)) return;

            final var namespace = actionMessage.getNamespace();
            final var registration = registrations.get(namespace);
            if (registration == null) return; // Maybe log this?

            registration.handlePayload(json);

        } catch (Exception ex) {
            throw new IncomingActionException(ex);
        }
    }

    public void subscribe() {
        pubSubConnection.addListener(listener);
        asyncCommands.subscribe(listeningChannel.getName());
    }

    public void unsubscribe() {
        pubSubConnection.removeListener(listener);
        asyncCommands.unsubscribe(listeningChannel.getName());
    }

    @Override
    public <T extends RedisAction> CompletionStage<Long> publish(@NotNull T action) {
        return publish(action, null);
    }

    @Override
    public <T extends RedisAction> CompletionStage<Long> publish(@NotNull T action, RedisInstance target) {
        try (var connection = this.connectionProvider.getConnection()) {
            final var namespace = Namespace.ofAction(action.getClass());
            final var actionMessage = new RedisActionMessage<>(currentInstance, target, action, namespace);
            final var jsonMessage = JSON.toJSONString(actionMessage);
            return connection.async().publish(listeningChannel.getName(), jsonMessage);
        } catch (Exception ex) {
            throw new PublishFailureException(ex, action);
        }
    }

    @Override
    public <T extends RedisAction> void registerHandler(final @NotNull Class<T> clazz, final @NotNull ActionHandler<T> handler) {
        final var registration = new ActionSubscriberRegistration<>(clazz, handler);
        final var namespace = Namespace.ofAction(clazz);
        registrations.put(namespace, registration);
    }

    @Override
    public <T extends RedisAction> void registerHandler(final @NotNull ActionSubscriberRegistration<T> registration) {
        final var namespace = Namespace.ofAction(registration.getActionClass());
        registrations.put(namespace, registration);
    }

}
