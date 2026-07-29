package com.ironhold.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * Data-access wiring for Ironhold.
 *
 * The application talks to the database through two JdbcTemplates. The primary
 * one runs under the application account and is used by the seed loader and any
 * internal maintenance query. The public inmate lookup runs under a separate,
 * reduced-privilege account ("ironhold_lookup") that can only read the records
 * that feature is meant to serve, so a floor-facing search can never reach staff
 * credentials, internal notices, or anything on the host filesystem.
 */
@Configuration
public class DataAccessConfig {

    public static final String LOOKUP_USER = "ironhold_lookup";
    public static final String LOOKUP_PASSWORD = "Lk_r0_2091!";

    @Bean
    @Primary
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public JdbcTemplate searchJdbcTemplate() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        // Attach to the same in-memory database the application account keeps
        // alive. DB_CLOSE_DELAY is left off here: it is a database-level setting
        // only an administrator may apply, and this account is not one.
        dataSource.setUrl("jdbc:h2:mem:ironhold;IFEXISTS=TRUE");
        dataSource.setUsername(LOOKUP_USER);
        dataSource.setPassword(LOOKUP_PASSWORD);
        return new JdbcTemplate(dataSource);
    }
}
