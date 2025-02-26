package it.pino.pineta.helper.redis.action.subscriber.registration.handler;


import it.pino.pineta.helper.redis.action.RedisAction;

public interface ActionHandler<T extends RedisAction> {

    void handle(final T action);

}
