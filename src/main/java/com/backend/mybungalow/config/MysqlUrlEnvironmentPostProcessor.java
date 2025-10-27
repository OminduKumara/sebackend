package com.backend.mybungalow.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.util.Collections;

/**
 * Fixes Railway-style MYSQL_URL environment values that start with "mysql://" by
 * converting them to a JDBC URL ("jdbc:mysql://...") and injecting the corrected
 * value as `spring.datasource.url` before DataSource auto-configuration runs.
 */
public class MysqlUrlEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String PROP_SPRING_DATASOURCE_URL = "spring.datasource.url";
    private static final String ENV_SPRING_DATASOURCE_URL = "SPRING_DATASOURCE_URL";
    private static final String ENV_MYSQL_URL = "MYSQL_URL";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // If spring.datasource.url is already set (via SPRING_DATASOURCE_URL or properties), don't override
        String existing = environment.getProperty(PROP_SPRING_DATASOURCE_URL);
        if (existing != null && !existing.isBlank()) {
            return;
        }

        // Try SPRING_DATASOURCE_URL env first (case-insensitive access via env property names is handled by Spring)
        String springEnv = environment.getProperty(ENV_SPRING_DATASOURCE_URL);
        if (springEnv != null && !springEnv.isBlank()) {
            // Prefer explicit SPRING_DATASOURCE_URL
            addProperty(environment, PROP_SPRING_DATASOURCE_URL, springEnv);
            return;
        }

        // Then try MYSQL_URL which Railway often provides in the form mysql://user:pass@host:port/db
        String mysqlUrl = environment.getProperty(ENV_MYSQL_URL);
        if (mysqlUrl != null && !mysqlUrl.isBlank()) {
            String fixed = normalizeMysqlUrl(mysqlUrl.trim());
            if (fixed != null) {
                addProperty(environment, PROP_SPRING_DATASOURCE_URL, fixed);
            }
        }
    }

    private void addProperty(ConfigurableEnvironment environment, String key, String value) {
        MutablePropertySources sources = environment.getPropertySources();
        MapPropertySource ps = new MapPropertySource("fixed-datasource-url", Collections.singletonMap(key, value));
        // Add with highest precedence
        sources.addFirst(ps);
    }

    private String normalizeMysqlUrl(String url) {
        // If it already starts with jdbc, assume it's correct
        if (url.startsWith("jdbc:")) {
            return url;
        }

        // Common Railway format: mysql://user:pass@host:port/db
        if (url.startsWith("mysql://")) {
            return "jdbc:" + url;
        }

        // Some deployments may provide mysql:... without double slashes
        if (url.startsWith("mysql:")) {
            return "jdbc:" + url;
        }

        // Not recognizable, return null to avoid injecting bad values
        return null;
    }
}
