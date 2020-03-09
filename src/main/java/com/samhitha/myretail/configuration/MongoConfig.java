package com.samhitha.myretail.configuration;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${mongo.username}")
    private String mongoUsername;

    @Value("${mongo.password}")
    private String mongoPassword;

    @Value("${mongo.connectionString}")
    private String connectionString;

    @Value("${mongo.database}")
    private String mongoDatabase;

    @Override
    protected String getDatabaseName() {
        return mongoDatabase;
    }

    @Override
    @Bean
    public MongoClient mongoClient() {
        String connectionStringWithCredentials = String.format(connectionString,mongoUsername,mongoPassword);
        return MongoClients.create(connectionStringWithCredentials);
    }
}
