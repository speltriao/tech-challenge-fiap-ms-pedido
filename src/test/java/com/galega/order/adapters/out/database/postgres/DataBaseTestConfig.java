package com.galega.order.adapters.out.database.postgres;

import org.flywaydb.core.Flyway;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import javax.sql.DataSource;

@TestConfiguration
public class DataBaseTestConfig {

	@Bean("testDataSource")
	public DataSource testDataSource() {
		PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15")
				.withDatabaseName("galega_test")
				.withUsername("postgres")
				.withPassword("postgres")
				.waitingFor(Wait.forListeningPort());

		postgresContainer.start();

		return DataSourceBuilder.create()
				.url(postgresContainer.getJdbcUrl())
				.username(postgresContainer.getUsername())
				.password(postgresContainer.getPassword())
				.driverClassName("org.postgresql.Driver")
				.build();
	}

	@Bean("testFlyway")
	public Flyway testFlyway(DataSource testDataSource) {
		Flyway flyway = Flyway.configure()
				.dataSource(testDataSource)
				.cleanDisabled(false)
				.load();

		flyway.migrate();
		return flyway;
	}
}
