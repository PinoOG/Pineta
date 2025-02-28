package it.pino.pineta.helper.redis.action.subscriber;

import it.pino.pineta.helper.redis.action.RedisAction;
import it.pino.pineta.helper.redis.action.subscriber.registration.ActionSubscriberRegistration;
import it.pino.pineta.helper.redis.action.subscriber.registration.handler.ActionHandler;
import org.jetbrains.annotations.NotNull;

public interface ActionSubscriber {

    <T extends RedisAction> void registerHandler(final @NotNull Class<T> clazz, final @NotNull ActionHandler<T> handler);

    <T extends RedisAction> void registerHandler(final @NotNull ActionSubscriberRegistration<T> registration);
}
