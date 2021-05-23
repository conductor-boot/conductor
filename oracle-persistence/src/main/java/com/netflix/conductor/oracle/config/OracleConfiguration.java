/*
 * Copyright 2020 Netflix, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.netflix.conductor.oracle.config;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.conductor.dao.ExecutionDAO;
import com.netflix.conductor.dao.MetadataDAO;
import com.netflix.conductor.dao.QueueDAO;
import com.netflix.conductor.oracle.dao.OracleExecutionDAO;
import com.netflix.conductor.oracle.dao.OracleMetadataDAO;
import com.netflix.conductor.oracle.dao.OracleQueueDAO;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(OracleProperties.class)
@ConditionalOnProperty(name = "conductor.db.type", havingValue = "oracle")
// Import the DataSourceAutoConfiguration when oracle database is selected.
// By default the datasource configuration is excluded in the main module.
public class OracleConfiguration {
	
	@Autowired
	public DataSource dataSource;

	@Primary
	@Bean(initMethod = "migrate")
	@DependsOn("dataSource")
	Flyway flyway() {
	    Flyway flyway = new Flyway();
	    flyway.setLocations("classpath:db/migration_oracle");
	    flyway.setDataSource(dataSource);
	    return flyway;
	}

    @Bean
    @DependsOn({"flyway", "flywayInitializer"})
    public MetadataDAO oracleMetadataDAO(ObjectMapper objectMapper, DataSource dataSource, OracleProperties properties) {
        return new OracleMetadataDAO(objectMapper, dataSource, properties);
    }

    @Bean
    @DependsOn({"flyway", "flywayInitializer"})
    public ExecutionDAO oracleExecutionDAO(ObjectMapper objectMapper, DataSource dataSource) {
        return new OracleExecutionDAO(objectMapper, dataSource);
    }

    @Bean
    @DependsOn({"flyway", "flywayInitializer"})
    public QueueDAO oracleQueueDAO(ObjectMapper objectMapper, DataSource dataSource) {
        return new OracleQueueDAO(objectMapper, dataSource);
    }
}
