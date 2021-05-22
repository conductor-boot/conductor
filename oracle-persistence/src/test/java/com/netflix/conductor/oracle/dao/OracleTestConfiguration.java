package com.netflix.conductor.oracle.dao;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.OracleContainer;

@TestConfiguration
public class OracleTestConfiguration {

	@SuppressWarnings("resource")
	@Bean
    public static OracleContainer oracleContainer() {
		return new OracleContainer("phx.ocir.io/toddrsharp/oracle-db/oracle/database:18.4.0-xe")
        .withEnv("ORACLE_PWD", "Str0ngPassw0rd")
        .withEnv("ORACLE_PASSWORD", "Str0ngPassw0rd")
        .withStartupTimeoutSeconds(900)
        .withConnectTimeoutSeconds(900)
        .withPassword("Str0ngPassw0rd");
	}
            
}
