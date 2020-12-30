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
        basePackages = "com.github.ep2p.eleuth.repository.file",
        entityManagerFactoryRef = "h2fileEntityManager",
        transactionManagerRef = "h2fileTransactionManager")
public class H2FileDbConfiguration {

    @Bean("h2fileDataSource")
    public DataSource h2fileDataSource() {
        return DataSourceBuilder
                .create()
                .url("jdbc:h2:file:/opt/data")
                .driverClassName("org.h2.Driver")
                .build();
    }

    @Bean("h2fileEntityManager")
    @DependsOn("h2fileDataSource")
    public LocalContainerEntityManagerFactoryBean h2fileEntityManager(@Qualifier("h2InMemoryDataSource") DataSource h2fileDataSource) {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(h2fileDataSource);
        em.setPackagesToScan("com.github.ep2p.eleuth.model.entity.file");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "none");
        properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean("h2fileTransactionManager")
    @DependsOn("h2fileEntityManager")
    public PlatformTransactionManager h2fileTransactionManager(@Qualifier("h2fileEntityManager") LocalContainerEntityManagerFactoryBean h2fileEntityManager) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(h2fileEntityManager.getObject());
        return transactionManager;
    }

}
