package ru.exmo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * Created by Andrash on 28.12.2017.
 */
@Configuration
public class contextConfiguration {
    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://92.63.97.175/crypto");
        dataSource.setUsername("postgres");
        dataSource.setPassword("3121108sa");
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcOperations() {
        return new JdbcTemplate(dataSource());
    }

}
