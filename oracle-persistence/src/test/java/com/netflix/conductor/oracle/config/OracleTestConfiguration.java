package com.netflix.conductor.oracle.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.utility.DockerImageName;

import com.zaxxer.hikari.HikariDataSource;

@TestConfiguration
public class OracleTestConfiguration {
	
	public OracleContainer oracleContainer;

	@Bean
	public HikariDataSource hikariDataSource() {
		
		System.setProperty("oracle.jdbc.timezoneAsRegion","false");
    	System.setProperty("oracle.jdbc.fanEnabled", "false");
    	
		oracleContainer = new OracleContainer(DockerImageName.parse(
	   			 //"oracleinanutshell/oracle-xe-11g"));
   			"conductorboot/oracle:11g-xe"));
  			 //"conductorboot/oracle:18.4.0-xe-slim")); // To be enabled once Github Actions supports Oracle 18 XE based CICD
		oracleContainer
		.withStartupTimeoutSeconds(900)
		.withConnectTimeoutSeconds(900)
		//.withPassword("Str0ngPassw0rd")  // To be enabled once Github Actions supports Oracle 18 XE based CICD
		.withInitScript("INIT_SCRIPT.sql");
		
		oracleContainer.start();
		
		HikariDataSource hikariDataSource = new HikariDataSource();
		
		hikariDataSource.setJdbcUrl("jdbc:oracle:thin:@//"+ oracleContainer.getHost() + ":" + oracleContainer.getOraclePort() + "/XE");
        
		hikariDataSource.setUsername("junit_user");
		hikariDataSource.setPassword("junit_user");
        
        
		hikariDataSource.setAutoCommit(false);
        
        //Prevent DB from getting exhausted during rapid testing
		hikariDataSource.setMaximumPoolSize(24);

        return hikariDataSource;
    
    }
}
