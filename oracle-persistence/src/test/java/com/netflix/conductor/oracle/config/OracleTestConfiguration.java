package com.netflix.conductor.oracle.config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.file.Paths;
import java.time.Duration;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
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
	
	@Bean
	@DependsOn("oracleContainer")
	public DataSource dataSource(@Autowired OracleContainer oracleContainer) {
		HikariDataSource dataSource = new HikariDataSource();
		
        dataSource.setJdbcUrl("jdbc:oracle:thin:@//"+ oracleContainer.getHost() + ":" + oracleContainer.getOraclePort()  + "/" + oracleContainer.getSid());
        
        dataSource.setUsername("junit_user");
        dataSource.setPassword("junit_user");
        
        //dataSource.setAutoCommit(false);

    	when(properties.getTaskDefCacheRefreshInterval()).thenReturn(Duration.ofSeconds(60));

    	dataSource.setMaximumPoolSize(100);
        flywayMigrate(dataSource);
        
        return dataSource;
	}
	
	private void flywayMigrate(DataSource dataSource) {
        
        Flyway flyway = new Flyway();
        flyway.setLocations(Paths.get("db", "migration_oracle").toString());
        flyway.setDataSource(dataSource);
        flyway.setPlaceholderReplacement(false);
        flyway.migrate();
    }
	
	@Bean
	public OracleProperties oracleProperties() {
		return properties;
	}
}
