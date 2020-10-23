package org.example.transaction.consumer.config;

import dagger.Binds;
import dagger.Provides;
import io.helidon.config.Config;
import io.helidon.dbclient.DbClient;
import org.example.transaction.consumer.adapter.TransactionAggregationRepositoryImpl;
import org.example.transaction.consumer.adapter.TransactionRecordPostgresRepository;
import org.example.transaction.consumer.adapter.redis.RedisStorage;
import org.example.transaction.consumer.port.TransactionAggregationHttpService;
import org.example.transaction.consumer.port.TransactionAggregationRepository;
import org.example.transaction.consumer.port.TransactionRecordRepository;
import org.example.transaction.consumer.service.TransactionAggregationHttpServiceImpl;

import javax.inject.Singleton;

@dagger.Module
abstract class Base {

    @Provides @Singleton
    static DbClient dbClient() {
        Config dbConfig = Config.create().get("db");
        return DbClient.builder(dbConfig).build();
    }

    @Provides @Singleton
    static RedisStorage redisStorage() {
        Config redisConfig = Config.create().get("redis");
        String host = redisConfig.get("host").asString().get();
        Integer port = redisConfig.get("port").asInt().get();
        return RedisStorage.Builder.build(host, port);
    }

    @Binds
    abstract TransactionRecordRepository transactionRecordRepository(TransactionRecordPostgresRepository repository);

    @Binds
    abstract TransactionAggregationRepository transactionAggregationRepository(TransactionAggregationRepositoryImpl repository);

    @Binds
    abstract TransactionAggregationHttpService transactionAggregationHttpService(TransactionAggregationHttpServiceImpl service);

}
