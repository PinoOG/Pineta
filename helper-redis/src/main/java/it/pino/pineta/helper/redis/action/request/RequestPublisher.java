package it.pino.pineta.helper.redis.action.request;

import it.pino.pineta.helper.redis.action.callback.RedisCallback;
import it.pino.pineta.helper.redis.action.request.registration.RequestSubscriberRegistration;
import it.pino.pineta.helper.redis.action.request.registration.handler.RequestSupplier;
import it.pino.pineta.helper.redis.action.instance.RedisInstance;
import it.pino.pineta.helper.redis.action.request.response.Response;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

public interface RequestPublisher {

    @Blocking
    <C extends RedisCallback> CompletionStage<Response<C>> waitRequest(final Class<C> callback, final int timeout, final TimeUnit timeoutUnit);

    @Blocking
    <C extends RedisCallback> CompletionStage<Response<C>> waitRequest(final Class<C> callback, final RedisInstance target, final int timeout, final TimeUnit timeoutUnit);

    <T extends RedisCallback> void registerSupplier(@NotNull Class<T> clazz, @NotNull RequestSupplier<T> supplier);

    <T extends RedisCallback> void registerSupplier(@NotNull RequestSubscriberRegistration<T> registration);
}
