package it.pino.pineta.helper.redis.action.request.registration.handler;

import it.pino.pineta.helper.redis.action.callback.RedisCallback;

import java.util.function.Supplier;

public interface RequestSupplier<T extends RedisCallback> extends Supplier<T> {
}
