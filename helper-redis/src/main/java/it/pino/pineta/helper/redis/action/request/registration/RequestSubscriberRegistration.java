package it.pino.pineta.helper.redis.action.request.registration;

import com.alibaba.fastjson2.JSONObject;
import it.pino.pineta.helper.redis.action.callback.RedisCallback;
import it.pino.pineta.helper.redis.action.request.message.RedisRequestMessage;
import it.pino.pineta.helper.redis.action.request.registration.handler.RequestSupplier;
import it.pino.pineta.helper.redis.action.request.message.RequestType;
import it.pino.pineta.helper.redis.action.instance.RedisInstance;
import it.pino.pineta.helper.redis.action.request.response.RequestResponse;

import java.util.Optional;

public final class RequestSubscriberRegistration<T extends RedisCallback> {

    private final Class<T> callbackClass;
    private final RequestSupplier<T> supplier;

    public RequestSubscriberRegistration(final Class<T> clazz, final RequestSupplier<T> supplier) {
        this.callbackClass = clazz;
        this.supplier = supplier;
    }

    public Optional<RedisRequestMessage<T>> handleRequest(final RedisRequestMessage<?> incomingMessage, final RedisInstance currentInstance){
        final var namespace = incomingMessage.getNamespace();
        final var sender = incomingMessage.getSender();
        final var uuid = incomingMessage.getUniqueID();

        final var callback = supplier.get();
        if(callback == null) return Optional.empty();

        final var builder = new RedisRequestMessage.Builder<T>()
                .setRequestType(RequestType.RESPONSE)
                .setNamespace(namespace)
                .setUniqueID(uuid)
                .setSender(currentInstance)
                .setReceiver(sender)
                .setCallback(callback);

        return Optional.of(builder.build());
    }

    public RequestResponse<T> getResponse(final RedisRequestMessage<?> callbackMessage, final String message){
        final var payload = JSONObject.parseObject(message);
        final var callback = JSONObject.parseObject(payload.getString("callback"), callbackClass);
        return new RequestResponse<>(callbackMessage.getUniqueID(), callbackMessage.getSender(), callback);
    }

    public Class<T> getCallbackClass() {
        return callbackClass;
    }
}
