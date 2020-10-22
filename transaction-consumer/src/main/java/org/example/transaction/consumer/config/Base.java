package org.example.transaction.consumer.config;

import dagger.Binds;
import dagger.Lazy;
import dagger.Provides;
import io.helidon.config.Config;
import io.helidon.dbclient.DbClient;
import org.apache.logging.log4j.core.pattern.AbstractStyleNameConverter;
import org.example.transaction.consumer.adapter.TransactionAggregationRepositoryImpl;
import org.example.transaction.consumer.adapter.TransactionRecordPostgresRepository;
import org.example.transaction.consumer.adapter.redis.RedisStorage;
import org.example.transaction.consumer.entity.mapper.AggregationItemListSerializer;
import org.example.transaction.consumer.entity.mapper.TransactionMessageDeserializer;
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
        return RedisStorage.Builder.build();
    }

    @Binds
    abstract TransactionRecordRepository transactionRecordRepository(TransactionRecordPostgresRepository repository);

    @Binds
    abstract TransactionAggregationRepository transactionAggregationRepository(TransactionAggregationRepositoryImpl repository);

    @Binds
    abstract TransactionAggregationHttpService transactionAggregationHttpService(TransactionAggregationHttpServiceImpl service);

}
