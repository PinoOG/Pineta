package it.pino.pineta.helper.redis.action.publisher;

import it.pino.pineta.helper.redis.action.RedisAction;
import it.pino.pineta.helper.redis.action.instance.RedisInstance;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletionStage;

public interface ActionPublisher {

    @NotNull <T extends RedisAction> CompletionStage<Boolean> publish(final @NotNull T action);

    @NotNull <T extends RedisAction> CompletionStage<Boolean> publish(final @NotNull T action, final RedisInstance target);


}
