package ru.pixelmongo.pixelmongo.configs.database;

import java.io.IOException;
import java.util.HashMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@PropertySource({"classpath:database.properties"})
@EnableJpaRepositories(
    basePackages = PrimaryDatabaseConfig.REPO_PACKAGE,
    entityManagerFactoryRef = "primaryEntityManager",
    transactionManagerRef = "primaryTransactionManager")
public class PrimaryDatabaseConfig {

    public static final String REPO_PACKAGE = "ru.pixelmongo.pixelmongo.repositories.primary";
    public static final String MODEL_PACKAGE = "ru.pixelmongo.pixelmongo.model.dao.primary";
    public static final String PROP_PREFIX = "spring.datasource";

    @Autowired
    private Environment env;

    @Autowired
    private ApplicationContext ctx;

    @Primary
    @Bean
    @ConfigurationProperties(prefix=PROP_PREFIX)
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean
    public DataSourceInitializer primaryDataSourceInitializer(@Qualifier("primaryDataSource") DataSource datasource) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();

        try {
            String schemaLoc = env.getProperty(PROP_PREFIX+".schema");
            if(schemaLoc != null) {
                Resource resource = ctx.getResource(schemaLoc);
                if(resource.exists() && resource.contentLength() > 0)
                    populator.addScript(resource);
            }
            String dataLoc = env.getProperty(PROP_PREFIX+".data");
            if(dataLoc != null) {
                Resource resource = ctx.getResource(dataLoc);
                if(resource.exists() && resource.contentLength() > 0)
                    populator.addScript(resource);
            }
        }catch(IOException ex) {
            throw new RuntimeException(ex);
        }

        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(datasource);
        initializer.setDatabasePopulator(populator);
        return initializer;
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean primaryEntityManager() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(primaryDataSource());
        em.setPackagesToScan(MODEL_PACKAGE);
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<String, Object>();
        properties.put("hibernate.hbm2ddl.auto", env.getProperty(PROP_PREFIX+".hibernate.hbm2ddl.auto"));
        properties.put("hibernate.dialect", env.getProperty(PROP_PREFIX+".hibernate.dialect"));
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Primary
    @Bean
    public PlatformTransactionManager primaryTransactionManager() {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(primaryEntityManager().getObject());
        return tm;
    }

}
