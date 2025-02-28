package it.pino.pineta.helper.redis;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.support.ConnectionPoolSupport;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.jetbrains.annotations.NotNull;

public final class RedisConnectionProvider implements ConnectionProvider {

    private final @NotNull RedisURI redisURI;
    private final @NotNull ClientResources clientResources;
    private final @NotNull ObjectPool<StatefulRedisConnection<String, String>> pool;

    private RedisClient redisClient;
    private ClientOptions clientOptions;

    public RedisConnectionProvider(final @NotNull RedisURI redisURI,
                                   final @NotNull ClientResources clientResources,
                                   final @NotNull GenericObjectPoolConfig<StatefulRedisConnection<String, String>> poolConfig)
    {
        this.redisURI = redisURI;
        this.clientResources = clientResources;
        this.pool = ConnectionPoolSupport.createGenericObjectPool(() -> redisClient.connect(), poolConfig);
    }

    public void init() {
        redisClient = RedisClient.create(clientResources, redisURI);
        if (clientOptions != null) redisClient.setOptions(clientOptions);
    }

    public void shutdown() throws Exception {
        this.pool.clear();
        this.pool.close();
        this.redisClient.shutdown();
    }

    @Override
    public StatefulRedisConnection<String, String> getConnection() throws Exception{
        return pool.borrowObject();
    }

    @Override
    public StatefulRedisPubSubConnection<String, String> getConnectionPubSub() {
        return redisClient.connectPubSub(StringCodec.UTF8, redisURI);
    }
}
