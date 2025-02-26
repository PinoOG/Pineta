package it.pino.pineta.helper.redis.action.subscriber.registration;

import com.alibaba.fastjson2.JSONObject;
import it.pino.pineta.helper.redis.action.RedisAction;
import it.pino.pineta.helper.redis.action.subscriber.registration.handler.ActionHandler;
import org.jetbrains.annotations.NotNull;

public final class ActionSubscriberRegistration<T extends RedisAction> {

    private final @NotNull Class<T> actionClass;
    private final @NotNull ActionHandler<T> handler;

    public ActionSubscriberRegistration(final @NotNull Class<T> clazz, final @NotNull ActionHandler<T> handler) {
        this.actionClass = clazz;
        this.handler = handler;
    }

    public void handlePayload(final @NotNull JSONObject payload){
        final var action = JSONObject.parseObject(payload.getString("action"), actionClass);
        handler.handle(action);
    }

    public @NotNull Class<T> getActionClass() {
        return actionClass;
    }

    public @NotNull ActionHandler<T> getHandler() {
        return handler;
    }
}
