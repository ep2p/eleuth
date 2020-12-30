package com.github.ep2p.eleuth.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.github.ep2p.eleuth.repository.memory",
        entityManagerFactoryRef = "h2InMemoryEntityManager",
        transactionManagerRef = "h2InMemoryTransactionManager")
public class H2InMemoryConfiguration {

    @Bean("h2InMemoryDataSource")
    public DataSource h2InMemoryDataSource() {
        return DataSourceBuilder
                .create()
                .url("dbc:h2:mem:eleuth")
                .driverClassName("org.h2.Driver")
                .build();
    }

    @Bean("h2InMemoryEntityManager")
    @DependsOn("h2InMemoryDataSource")
    public LocalContainerEntityManagerFactoryBean h2InMemoryEntityManager(@Qualifier("h2InMemoryDataSource") DataSource h2InMemoryDataSource) {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(h2InMemoryDataSource);
        em.setPackagesToScan("com.github.ep2p.eleuth.model.entity.memory");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "none");
        properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean("h2InMemoryTransactionManager")
    @DependsOn("h2InMemoryEntityManager")
    public PlatformTransactionManager h2InMemoryTransactionManager(@Qualifier("h2InMemoryEntityManager") LocalContainerEntityManagerFactoryBean h2InMemoryEntityManager) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(h2InMemoryEntityManager.getObject());
        return transactionManager;
    }

}
