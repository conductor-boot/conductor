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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Duration;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.utility.DockerImageName;

import com.zaxxer.hikari.HikariDataSource;

@TestConfiguration
public class OracleTestConfiguration {
	
	private final OracleProperties properties = mock(OracleProperties.class);
	
	@Bean("oracleContainer")
	public OracleContainer oracleContainer() {
		
		System.setProperty("oracle.jdbc.timezoneAsRegion","false");
    	System.setProperty("oracle.jdbc.fanEnabled", "false");
    	
    	OracleContainer oracleContainer = new OracleContainer(DockerImageName.parse(
	   			 //"oracleinanutshell/oracle-xe-11g"));
   			"conductorboot/oracle:11g-xe"));
  			 //"conductorboot/oracle:18.4.0-xe-slim")); // To be enabled once Github Actions supports Oracle 18 XE based CICD
		oracleContainer
		.withStartupTimeoutSeconds(900)
		.withConnectTimeoutSeconds(900)
		//.withPassword("Str0ngPassw0rd")  // To be enabled once Github Actions supports Oracle 18 XE based CICD
		.withInitScript("INIT_SCRIPT.sql");
		
		starOracleContainer(oracleContainer);
		
		return oracleContainer;
    
    }
	
	private void starOracleContainer(OracleContainer oracleContainer) {
		oracleContainer.start();
	}
	
	/*@Bean
	@DependsOn("oracleContainer")
	public DataSource dataSource(@Autowired OracleContainer oracleContainer) {
		HikariDataSource dataSource = new HikariDataSource();
		
        dataSource.setJdbcUrl("jdbc:oracle:thin:@//"+ oracleContainer.getHost() + ":" + oracleContainer.getOraclePort()  + "/" + oracleContainer.getSid());
        
        dataSource.setUsername("junit_user");
        dataSource.setPassword("junit_user");
        
        dataSource.setAutoCommit(false);

    	when(properties.getTaskDefCacheRefreshInterval()).thenReturn(Duration.ofSeconds(60));

    	dataSource.setMaximumPoolSize(100);
        flywayMigrate(dataSource);
        
        return dataSource;
	}*/
	
	@Bean("executionDataSource")
	@DependsOn("oracleContainer")
	public DataSource executionDataSource(@Autowired OracleContainer oracleContainer) {
		HikariDataSource dataSource = new HikariDataSource();
		
        dataSource.setJdbcUrl("jdbc:oracle:thin:@//"+ oracleContainer.getHost() + ":" + oracleContainer.getOraclePort()  + "/" + oracleContainer.getSid());
        
        dataSource.setUsername("junit_user");
        dataSource.setPassword("junit_user");
        
        dataSource.setAutoCommit(false);

    	when(properties.getTaskDefCacheRefreshInterval()).thenReturn(Duration.ofSeconds(60));

    	dataSource.setMaximumPoolSize(100);
        flywayMigrate(dataSource);
        
        return dataSource;
	}
	
	@Bean("metadataDataSource")
	@DependsOn("oracleContainer")
	public DataSource metadataDataSource(@Autowired OracleContainer oracleContainer) {
		HikariDataSource dataSource = new HikariDataSource();
		
        dataSource.setJdbcUrl("jdbc:oracle:thin:@//"+ oracleContainer.getHost() + ":" + oracleContainer.getOraclePort()  + "/" + oracleContainer.getSid());
        
        dataSource.setUsername("junit_user");
        dataSource.setPassword("junit_user");
        
        dataSource.setAutoCommit(false);

    	when(properties.getTaskDefCacheRefreshInterval()).thenReturn(Duration.ofSeconds(60));

    	dataSource.setMaximumPoolSize(100);
        flywayMigrate(dataSource);
        
        return dataSource;
	}
	
	@Bean("queueDataSource")
	@DependsOn("oracleContainer")
	public DataSource queueDataSource(@Autowired OracleContainer oracleContainer) {
		HikariDataSource dataSource = new HikariDataSource();
		
        dataSource.setJdbcUrl("jdbc:oracle:thin:@//"+ oracleContainer.getHost() + ":" + oracleContainer.getOraclePort()  + "/" + oracleContainer.getSid());
        
        dataSource.setUsername("junit_user");
        dataSource.setPassword("junit_user");
        
        dataSource.setAutoCommit(false);

    	when(properties.getTaskDefCacheRefreshInterval()).thenReturn(Duration.ofSeconds(60));

    	dataSource.setMaximumPoolSize(100);
        flywayMigrate(dataSource);
        
        return dataSource;
	}
	
	private void flywayMigrate(DataSource dataSource) {
		
		Flyway flyway = new Flyway();
		flyway.setDataSource(dataSource);
		flyway.migrate();
	
    }
	
	@Bean
	public OracleProperties oracleProperties() {
		return properties;
	}
}
