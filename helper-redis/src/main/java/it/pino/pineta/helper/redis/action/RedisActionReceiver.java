package it.pino.pineta.helper.redis.action;

import com.alibaba.fastjson2.JSONObject;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import it.pino.pineta.helper.redis.api.action.Action;
import it.pino.pineta.helper.redis.api.action.ActionListener;
import it.pino.pineta.helper.redis.api.action.ActionReceiver;

import java.util.concurrent.ExecutorService;

public abstract class RedisActionReceiver<T extends RedisAction<T>> implements ActionReceiver {

    private final String channel;
    private final ActionListener listener;
    private final Action<T> action;
    private final StatefulRedisPubSubConnection<String, String> pubSubConnection;
    private final RedisPubSubAsyncCommands<String, String> asyncCommands;

    protected RedisActionReceiver(final String channel,
                                  final StatefulRedisPubSubConnection<String, String> pubSubConnection,
                                  final RedisPubSubAsyncCommands<String, String> asyncCommands,
                                  final ExecutorService executor,
                                  final Action<T> action)
    {
        this.channel = channel;
        this.pubSubConnection = pubSubConnection;
        this.asyncCommands = asyncCommands;

        this.listener = (ch, message) -> {
            if (!ch.equals(this.channel)) return;

            executor.execute(() -> handle(message));
        };
        this.action = action;
    }


    protected void handle(final String message){
        final JSONObject jsonObject = JSONObject.parseObject(message);
        final T actionObj = action.deserialize(jsonObject);
        actionObj.doSomething();
    }

    @Override
    public void subscribe() {
        pubSubConnection.addListener(listener);
        asyncCommands.subscribe(channel);
    }

    @Override
    public void unsubscribe() {
        pubSubConnection.removeListener(listener);
        asyncCommands.unsubscribe(channel);
    }
}
