package it.pino.pineta.helper.redis.utils;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.event.DefaultEventPublisherOptions;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import io.lettuce.core.resource.Delay;
import org.apache.commons.pool2.impl.BaseObjectPoolConfig;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public final class RedisDefaults {

    private RedisDefaults() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static final class PoolBuilder {

        private final GenericObjectPoolConfig<StatefulRedisConnection<String, String>> config;

        private int maxIdle = 5;
        private int total = 10;
        private int minIdle = 3;

        private PoolBuilder(GenericObjectPoolConfig<StatefulRedisConnection<String, String>> config) {
            this.config = config;
        }

        public static PoolBuilder create(){
            return new PoolBuilder(new GenericObjectPoolConfig<>());
        }

        public PoolBuilder maxIdle(final int maxIdle){
            this.maxIdle = maxIdle;
            return this;
        }

        public PoolBuilder total(final int total){
            this.total = total;
            return this;
        }

        public PoolBuilder minIdle(final int minIdle){
            this.minIdle = minIdle;
            return this;
        }

        public PoolBuilder applyRecommendedTests(){
            this.config.setTestOnBorrow(true);
            this.config.setTestOnReturn(true);
            this.config.setTestWhileIdle(true);
            return this;
        }

        public PoolBuilder set(final Consumer<BaseObjectPoolConfig<StatefulRedisConnection<String, String>>> consumer){
            consumer.accept(config);
            return this;
        }

        public GenericObjectPoolConfig<StatefulRedisConnection<String, String>> build(){
            config.setMaxIdle(maxIdle);
            config.setMaxTotal(total);
            config.setMinIdle(minIdle);
            return config;
        }
    }

    public static final ClientResources CLIENT_RESOURCES = DefaultClientResources.builder()
            .ioThreadPoolSize(Runtime.getRuntime().availableProcessors())
            .computationThreadPoolSize(Runtime.getRuntime().availableProcessors())
            .commandLatencyPublisherOptions(DefaultEventPublisherOptions.create())
            .reconnectDelay(Delay.decorrelatedJitter(100, 1000, 2, TimeUnit.MILLISECONDS))
            .build();

    public static final TimeoutOptions TIMEOUT_OPTIONS = TimeoutOptions.builder()
            .timeoutCommands(true)
            .fixedTimeout(Duration.ofSeconds(5))
            .build();

    public static final ClientOptions CLIENT_OPTIONS = ClientOptions.builder()
            .autoReconnect(true)
            .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
            .timeoutOptions(TIMEOUT_OPTIONS)
            .build();
}
