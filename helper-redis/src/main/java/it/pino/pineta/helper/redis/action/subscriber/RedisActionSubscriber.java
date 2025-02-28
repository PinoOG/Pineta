package it.pino.pineta.helper.redis.action.subscriber;

import com.alibaba.fastjson2.JSONObject;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import it.pino.pineta.helper.redis.ConnectionProvider;
import it.pino.pineta.helper.redis.action.RedisAction;
import it.pino.pineta.helper.redis.action.instance.RedisInstance;
import it.pino.pineta.helper.redis.action.instance.space.Namespace;
import it.pino.pineta.helper.redis.action.subscriber.message.RedisActionMessage;
import it.pino.pineta.helper.redis.action.subscriber.registration.ActionSubscriberRegistration;
import it.pino.pineta.helper.redis.action.subscriber.registration.handler.ActionHandler;
import it.pino.pineta.helper.redis.action.subscriber.listener.RedisActionListener;
import it.pino.pineta.helper.redis.channel.RedisChannel;
import it.pino.pineta.helper.redis.exception.IncomingActionException;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public abstract class RedisActionSubscriber implements ActionSubscriber {

    private final @NotNull StatefulRedisPubSubConnection<String, String> pubSubConnection;

    private final @NotNull RedisPubSubAsyncCommands<String, String> asyncCommands;

    private final @NotNull RedisChannel listeningChannel;

    private final @NotNull RedisActionListener listener;

    private final @NotNull RedisInstance currentInstance;

    private final @NotNull Map<Namespace, ActionSubscriberRegistration<?>> registrations;

    protected RedisActionSubscriber(final @NotNull ConnectionProvider connectionProvider,
                                    final @NotNull RedisChannel listeningChannel,
                                    final @NotNull ExecutorService executor,
                                    final @NotNull RedisInstance currentInstance)
    {
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
