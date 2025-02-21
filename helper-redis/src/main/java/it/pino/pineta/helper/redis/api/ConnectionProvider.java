package it.pino.pineta.helper.redis.api;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;

public interface ConnectionProvider {

    StatefulRedisConnection<String, String> getConnection() throws Exception;

    StatefulRedisPubSubConnection<String, String> getConnectionPubSub();
}
