package it.pino.pineta.helper.redis.action.publisher;

import it.pino.pineta.helper.redis.action.RedisAction;
import it.pino.pineta.helper.redis.action.instance.RedisInstance;
import it.pino.pineta.helper.redis.action.publisher.registration.ActionSubscriberRegistration;
import it.pino.pineta.helper.redis.action.publisher.registration.handler.ActionHandler;
import org.jetbrains.annotations.NotNull;

public interface ActionPublisher {

    <T extends RedisAction> boolean publish(final @NotNull T action);

    <T extends RedisAction> boolean publish(final @NotNull T action, final RedisInstance target);

    <T extends RedisAction> void registerHandler(final @NotNull Class<T> clazz, final @NotNull ActionHandler<T> handler);

    <T extends RedisAction> void registerHandler(final @NotNull ActionSubscriberRegistration<T> registration);
}
