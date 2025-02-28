package it.pino.pineta.helper.redis.action.instance.space;

import com.alibaba.fastjson2.annotation.JSONField;
import it.pino.pineta.helper.redis.action.RedisAction;
import it.pino.pineta.helper.redis.action.callback.RedisCallback;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class Namespace {

    @JSONField(name = "class")
    private final @NotNull String value;

    public static Namespace ofAction(final @NotNull Class<? extends RedisAction> clazz){
        return new Namespace(clazz.getSimpleName());
    }

    public static Namespace ofCallback(final @NotNull Class<? extends RedisCallback> clazz){
        return new Namespace(clazz.getSimpleName());
    }

    private Namespace(final @NotNull String value) {
        this.value = value;
    }


    public @NotNull String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Namespace namespace = (Namespace) o;
        return Objects.equals(value, namespace.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
