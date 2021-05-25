/*
 *  Copyright 2021 Netflix, Inc.
 *  <p>
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  the License. You may obtain a copy of the License at
 *  <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p>
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations under the License.
 */
package com.netflix.conductor.oracle.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.List;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.OracleContainer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.conductor.common.config.TestObjectMapperConfiguration;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.dao.ExecutionDAO;
import com.netflix.conductor.dao.ExecutionDAOTest;
import com.netflix.conductor.oracle.config.OracleTestConfiguration;
import com.zaxxer.hikari.HikariDataSource;

@ContextConfiguration(classes = {TestObjectMapperConfiguration.class, OracleTestConfiguration.class})
@RunWith(SpringRunner.class)
public class OracleExecutionDAOTest extends ExecutionDAOTest {

    private OracleExecutionDAO executionDAO;

    @Autowired
    private ObjectMapper objectMapper;

    @Rule
    public TestName name = new TestName();

    @Autowired
    public OracleContainer oracleContainer;
    
    public HikariDataSource dataSource;

    @SuppressWarnings("resource")
	@Before
    public void setup() {
    	
    	dataSource = new HikariDataSource();
		
        dataSource.setJdbcUrl("jdbc:oracle:thin:@//"+ oracleContainer.getHost() + ":" + oracleContainer.getOraclePort()  + "/" + oracleContainer.getSid());
        
        dataSource.setUsername("junit_user");
        dataSource.setPassword("junit_user");
        dataSource.setMaximumPoolSize(8);
        dataSource.setAutoCommit(false);
        
        flywayMigrate();
    	
    	executionDAO = new OracleExecutionDAO(objectMapper, dataSource);
    }
    
    private void flywayMigrate() {
		
		try {
			Flyway flyway = Flyway.class.getConstructor().newInstance();
			flyway.getClass().getMethod("setLocations", String.class).invoke(flyway, Paths.get("db", "migration_oracle").toString());
			flyway.getClass().getMethod("setDataSource", DataSource.class).invoke(flyway, dataSource);
			flyway.getClass().getMethod("migrate").invoke(flyway);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @Test
    public void testPendingByCorrelationId() {

        WorkflowDef def = new WorkflowDef();
        def.setName("pending_count_correlation_jtest");

        Workflow workflow = createTestWorkflow();
        workflow.setWorkflowDefinition(def);

        generateWorkflows(workflow, 10);

        List<Workflow> bycorrelationId = getExecutionDAO()
            .getWorkflowsByCorrelationId("pending_count_correlation_jtest", "corr001", true);
        assertNotNull(bycorrelationId);
        assertEquals(10, bycorrelationId.size());
    }

    @Override
    public ExecutionDAO getExecutionDAO() {
        return executionDAO;
    }
}
