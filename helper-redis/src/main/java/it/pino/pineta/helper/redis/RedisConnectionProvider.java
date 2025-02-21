package it.pino.pineta.helper.redis;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.support.ConnectionPoolSupport;
import it.pino.pineta.helper.redis.api.ConnectionProvider;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public abstract class RedisConnectionProvider implements Comparable<RedisConnectionProvider>, ConnectionProvider {

    private final RedisURI redisURI;
    private final ClientResources clientResources;
    private final ObjectPool<StatefulRedisConnection<String, String>> pool;

    private RedisClient redisClient;
    private ClientOptions clientOptions;

    protected RedisConnectionProvider(final RedisURI.Builder uriBuilder, final ClientResources clientResources, final GenericObjectPoolConfig<StatefulRedisConnection<String, String>> poolConfig) {
        this.redisURI = uriBuilder.build();
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
        this.redisClient.getResources().shutdown();
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
