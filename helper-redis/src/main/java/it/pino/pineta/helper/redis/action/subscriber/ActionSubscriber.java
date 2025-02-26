package it.pino.pineta.helper.redis.action.subscriber;

import it.pino.pineta.helper.redis.action.RedisAction;
import it.pino.pineta.helper.redis.action.subscriber.registration.ActionSubscriberRegistration;
import it.pino.pineta.helper.redis.action.subscriber.registration.handler.ActionHandler;
import org.jetbrains.annotations.NotNull;

public abstract class ActionSubscriber {

    public abstract <T extends RedisAction> void registerSubscriber(final @NotNull Class<T> clazz, final @NotNull ActionHandler<T> handler);

    public abstract <T extends RedisAction> void registerSubscriber(final @NotNull ActionSubscriberRegistration<T> registration);
}
