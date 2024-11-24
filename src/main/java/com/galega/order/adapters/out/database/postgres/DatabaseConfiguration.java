package com.galega.order.adapters.out.database.postgres;

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
    dataSource.setDriverClassName("org.postgresql.Driver");

    String dbHost = System.getenv("DB_HOST");
    String dbUser = System.getenv("DB_USER");
    String dbPassword = System.getenv("DB_PASSWORD");

    String jdbcUrl = String.format("jdbc:postgresql://%s:5432/galega", dbHost);

    dataSource.setUrl(jdbcUrl);
    dataSource.setUsername(dbUser);
    dataSource.setPassword(dbPassword);

    return dataSource;
  }


}
