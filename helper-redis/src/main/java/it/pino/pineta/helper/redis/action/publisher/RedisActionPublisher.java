package it.pino.pineta.helper.redis.action.publisher;

import com.alibaba.fastjson2.JSON;
import it.pino.pineta.helper.redis.ConnectionProvider;
import it.pino.pineta.helper.redis.action.RedisAction;
import it.pino.pineta.helper.redis.action.instance.RedisInstance;
import it.pino.pineta.helper.redis.action.instance.space.Namespace;
import it.pino.pineta.helper.redis.action.message.RedisActionMessage;
import it.pino.pineta.helper.redis.channel.RedisChannel;
import it.pino.pineta.helper.redis.exception.PublishFailureException;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;

public abstract class RedisActionPublisher implements ActionPublisher {

    private final @NotNull ConnectionProvider connectionProvider;
    private final @NotNull ExecutorService executorService;
    private final @NotNull RedisChannel channel;
    private final @NotNull RedisInstance sender;

    protected RedisActionPublisher(final @NotNull ConnectionProvider connectionProvider,
                                   final @NotNull ExecutorService executorService,
                                   final @NotNull RedisChannel channel,
                                   final @NotNull RedisInstance sender)
    {
        this.connectionProvider = connectionProvider;
        this.executorService = executorService;
        this.channel = channel;
        this.sender = sender;
    }


    @Override
    public @NotNull <T extends RedisAction> CompletionStage<Boolean> publish(@NotNull T action) {
        return CompletableFuture.supplyAsync(() -> {
            try (var connection = this.connectionProvider.getConnection()) {
                final var namespace = Namespace.of(action.getClass().getSimpleName());
                final var actionMessage = new RedisActionMessage<>(sender, action, namespace);
                final var jsonMessage = JSON.toJSONString(actionMessage);
                connection.async().publish(channel.getName(), jsonMessage);
                return true;
            } catch (Exception ex) {
                throw new PublishFailureException(ex, action);
            }
        }, executorService);
    }

    @Override
    public @NotNull <T extends RedisAction> CompletionStage<Boolean> publish(@NotNull T action, @NotNull RedisInstance target) {
        return CompletableFuture.supplyAsync(() -> {
            try (var connection = this.connectionProvider.getConnection()) {
                final var namespace = Namespace.of(action.getClass().getSimpleName());
                final var actionMessage = new RedisActionMessage<>(sender, target, action, namespace);
                final var jsonMessage = JSON.toJSONString(actionMessage);
                connection.async().publish(channel.getName(), jsonMessage);
                return true;
            } catch (Exception ex) {
                throw new PublishFailureException(ex, action);
            }
        }, executorService);
    }

}
