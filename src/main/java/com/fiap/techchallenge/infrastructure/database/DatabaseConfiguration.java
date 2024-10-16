package com.fiap.techchallenge.infrastructure.server.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfiguration {

  private final Environment env;

  public DatabaseConfiguration(Environment env) {
    this.env = env;
  }

  @Bean
  public DataSource dataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();

    String host = env.getProperty("database.host");
    String databaseName = env.getProperty("database.name");
    String dataSourceUrl = "jdbc:postgresql://postgres:5432/galega_burguer";

    dataSource.setDriverClassName("org.postgresql.Driver");
    dataSource.setUrl(dataSourceUrl);
    dataSource.setUsername(env.getProperty("spring.datasource.username"));
    dataSource.setPassword(env.getProperty("spring.datasource.password"));
    return dataSource;
  }

}
