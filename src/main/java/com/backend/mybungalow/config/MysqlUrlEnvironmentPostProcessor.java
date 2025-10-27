package com.backend.mybungalow.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

/**
 * Fixes Railway-style MYSQL_URL environment values that start with "mysql://" by
 * converting them to a JDBC URL ("jdbc:mysql://...") and injecting the corrected
 * value as `spring.datasource.url` before DataSource auto-configuration runs.
 */
public class MysqlUrlEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(MysqlUrlEnvironmentPostProcessor.class);

    private static final String PROP_SPRING_DATASOURCE_URL = "spring.datasource.url";
    private static final String ENV_SPRING_DATASOURCE_URL = "SPRING_DATASOURCE_URL";
    private static final String ENV_MYSQL_URL = "MYSQL_URL";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // Check current spring.datasource.url and SPRING_DATASOURCE_URL first
        String existing = environment.getProperty(PROP_SPRING_DATASOURCE_URL);
        String springEnv = environment.getProperty(ENV_SPRING_DATASOURCE_URL);

        if (springEnv != null && !springEnv.isBlank()) {
            // Explicit SPRING_DATASOURCE_URL provided in env — ensure it starts with jdbc, otherwise normalize
            String normalized = normalizeMysqlUrl(springEnv.trim());
            if (normalized != null) {
                addProperty(environment, PROP_SPRING_DATASOURCE_URL, normalized);
            } else {
                addProperty(environment, PROP_SPRING_DATASOURCE_URL, springEnv.trim());
            }
            return;
        }

        if (existing != null && !existing.isBlank()) {
            // spring.datasource.url already present (possibly from application.properties). If it starts with jdbc, nothing to do.
            if (existing.startsWith("jdbc:")) {
                return;
            }
            // If it looks like mysql://, normalize and override
            String normalized = normalizeMysqlUrl(existing.trim());
            if (normalized != null) {
                addProperty(environment, PROP_SPRING_DATASOURCE_URL, normalized);
                return;
            }
            // otherwise leave as-is (let Spring fail so user can see the exact misconfiguration)
            return;
        }

        // Then try MYSQL_URL / MYSQL_PUBLIC_URL which Railway often provides in the form mysql://user:pass@host:port/db
        String mysqlUrl = environment.getProperty(ENV_MYSQL_URL);
        if (mysqlUrl == null || mysqlUrl.isBlank()) {
            mysqlUrl = environment.getProperty("MYSQL_PUBLIC_URL");
        }
        if (mysqlUrl != null && !mysqlUrl.isBlank()) {
            String fixed = normalizeMysqlUrl(mysqlUrl.trim());
            if (fixed != null) {
                addProperty(environment, PROP_SPRING_DATASOURCE_URL, fixed);
                // Log masked host/db for observability
                try {
                    String mask = maskedJdbcInfo(fixed);
                    log.info("Using datasource: {}", mask);
                } catch (Exception e) {
                    // don't fail startup if masking fails
                    log.debug("Could not mask datasource url", e);
                }
            }
        }
    }

    private String maskedJdbcInfo(String jdbcUrl) {
        // Expect jdbc:mysql://[user[:pass]@]host[:port]/db[?...]
        String url = jdbcUrl;
        if (url.startsWith("jdbc:")) {
            url = url.substring(5);
        }
        // Remove credentials if present
        String afterSlashes = url;
        if (afterSlashes.startsWith("//")) {
            afterSlashes = afterSlashes.substring(2);
        }
        if (afterSlashes.contains("@")) {
            afterSlashes = afterSlashes.substring(afterSlashes.indexOf('@') + 1);
        }
        // Now afterSlashes begins with host[:port]/db...
        String hostAndPath = afterSlashes;
        // Trim query params
        int q = hostAndPath.indexOf('?');
        if (q > -1) hostAndPath = hostAndPath.substring(0, q);
        return "jdbc:mysql://" + hostAndPath;
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
