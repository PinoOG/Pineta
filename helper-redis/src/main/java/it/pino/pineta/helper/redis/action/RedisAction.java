package it.pino.pineta.helper.redis.action;

import it.pino.pineta.helper.redis.api.action.Action;

public abstract class RedisAction<T> extends Action<T> {

    protected abstract void doSomething();
}
