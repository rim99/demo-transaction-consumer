package org.example.transaction.consumer.config;

import com.google.inject.AbstractModule;
import io.helidon.config.Config;
import io.helidon.dbclient.DbClient;
import org.example.transaction.consumer.adapter.TransactionAggregationRepositoryImpl;
import org.example.transaction.consumer.adapter.TransactionRecordPostgresRepository;
import org.example.transaction.consumer.adapter.redis.RedisStorage;
import org.example.transaction.consumer.port.TransactionAggregationHttpService;
import org.example.transaction.consumer.port.TransactionAggregationRepository;
import org.example.transaction.consumer.port.TransactionRecordRepository;
import org.example.transaction.consumer.service.TransactionAggregationHttpServiceImpl;

import javax.inject.Provider;
import javax.inject.Singleton;

public class AppModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(DbClient.class).toProvider(DBclientProvider.class).in(Singleton.class);
        bind(RedisStorage.class).toProvider(RedisStorageProvider.class).in(Singleton.class);
        bind(TransactionRecordRepository.class).to(TransactionRecordPostgresRepository.class).in(Singleton.class);
        bind(TransactionAggregationRepository.class).to(TransactionAggregationRepositoryImpl.class).in(Singleton.class);
        bind(TransactionAggregationHttpService.class).to(TransactionAggregationHttpServiceImpl.class).in(Singleton.class);
    }

    static class DBclientProvider implements Provider<DbClient> {
        @Override
        public DbClient get() {
            Config dbConfig = Config.create().get("db");
            return DbClient.builder(dbConfig).build();
        }
    }

    static class RedisStorageProvider implements Provider<RedisStorage> {
        @Override
        public RedisStorage get() {
            Config redisConfig = Config.create().get("redis");
            String host = redisConfig.get("host").asString().get();
            Integer port = redisConfig.get("port").asInt().get();
            return RedisStorage.Builder.build(host, port);
        }
    }
}
