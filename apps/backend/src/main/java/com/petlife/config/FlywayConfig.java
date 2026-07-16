package com.petlife.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class FlywayConfig implements BeanPostProcessor {

    private boolean migrated = false;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DataSource && !migrated) {
            Flyway.configure()
                    .baselineOnMigrate(true)
                    .locations("classpath:db/migration")
                    .dataSource((DataSource) bean)
                    .load()
                    .migrate();
            migrated = true;
        }
        return bean;
    }
}
