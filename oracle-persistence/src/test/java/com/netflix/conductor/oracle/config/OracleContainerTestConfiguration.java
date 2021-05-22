package com.netflix.conductor.oracle.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class OracleContainerTestConfiguration {

	@Bean
	public static OracleContainer oracleContainer() {
		OracleContainer oracleContainer = new OracleContainer(DockerImageName.parse(
	   			 "conductorboot/oracle:18.4.0-xe-slim-test"));
		oracleContainer
		.withInitScript("init_test_db.sql")
		.withStartupTimeoutSeconds(900)
		.withConnectTimeoutSeconds(900)
		.withUsername("sys as sysdba")
		.withPassword("Conductor@1234");
		return oracleContainer;
	}
}
