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
package com.netflix.conductor.oracle.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.file.Paths;
import java.time.Duration;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.testcontainers.containers.OracleContainer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.conductor.oracle.config.OracleProperties;
import com.zaxxer.hikari.HikariDataSource;

public class OracleDAOTestUtil {

    private final HikariDataSource dataSource;
    private final OracleProperties properties = mock(OracleProperties.class);
    private final ObjectMapper objectMapper;

    public OracleDAOTestUtil(OracleContainer oracleContainer, ObjectMapper objectMapper) {

    	this.objectMapper = objectMapper;

        this.dataSource = new HikariDataSource();
		
        this.dataSource.setJdbcUrl("jdbc:oracle:thin:@//"+ oracleContainer.getHost() + ":" + oracleContainer.getOraclePort()  + "/" + oracleContainer.getSid());
        
        this.dataSource.setUsername("junit_user");
        this.dataSource.setPassword("junit_user");
        
        
        this.dataSource.setAutoCommit(false);

    	when(properties.getTaskDefCacheRefreshInterval()).thenReturn(Duration.ofSeconds(60));

        flywayMigrate(dataSource);
    
    }

    private void flywayMigrate(DataSource dataSource) {
        
        Flyway flyway = new Flyway();
        flyway.setLocations(Paths.get("db", "migration_oracle").toString());
        flyway.setDataSource(dataSource);
        flyway.setPlaceholderReplacement(false);
        flyway.migrate();
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }

    public OracleProperties getTestProperties() {
        return properties;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

}
