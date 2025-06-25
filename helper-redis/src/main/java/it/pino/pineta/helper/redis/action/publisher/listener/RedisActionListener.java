package it.pino.pineta.helper.redis.action.publisher.listener;

import io.lettuce.core.pubsub.RedisPubSubListener;

public interface RedisActionListener extends RedisPubSubListener<String, String> {

    @Override
    void message(final String channel, final String message);

    @Override
    default void message(String pattern, String channel, String message) {
    }

    @Override
    default void subscribed(String channel, long count) {
    }

    @Override
    default void psubscribed(String pattern, long count) {
    }

    @Override
    default void unsubscribed(String channel, long count) {
    }

    @Override
    default void punsubscribed(String pattern, long count) {
    }
}
