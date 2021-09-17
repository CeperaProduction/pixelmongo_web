package ru.pixelmongo.pixelmongo.configs.database;

import java.util.HashMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@PropertySource({"classpath:database.properties"})
@EnableJpaRepositories(
    basePackages = SubDatabaseConfig.REPO_PACKAGE,
    entityManagerFactoryRef = "subEntityManager",
    transactionManagerRef = "subTransactionManager")
public class SubDatabaseConfig {

    public static final String REPO_PACKAGE = "ru.pixelmongo.pixelmongo.repositories.sub";
    public static final String MODEL_PACKAGE = "ru.pixelmongo.pixelmongo.model.dao.sub";
    public static final String PROP_PREFIX = "spring.sub-datasource";

    @Autowired
    private Environment env;

    @Bean
    @ConfigurationProperties(prefix=PROP_PREFIX)
    public DataSource subDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean subEntityManager() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(subDataSource());
        em.setPackagesToScan(MODEL_PACKAGE);
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<String, Object>();
        properties.put("hibernate.hbm2ddl.auto", env.getProperty(PROP_PREFIX+".hibernate.hbm2ddl.auto"));
        properties.put("hibernate.dialect", env.getProperty(PROP_PREFIX+".hibernate.dialect"));
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean
    public PlatformTransactionManager subTransactionManager() {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(subEntityManager().getObject());
        return tm;
    }

}
