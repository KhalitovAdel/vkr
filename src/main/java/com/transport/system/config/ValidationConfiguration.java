package com.transport.system.config;

import org.hibernate.cfg.ValidationSettings;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory;

/**
 * Ensures Hibernate Validator uses {@link SpringConstraintValidatorFactory} so custom
 * {@link jakarta.validation.ConstraintValidator} implementations can use constructor injection
 * (e.g. repositories) during JPA pre-persist / pre-update validation.
 *
 * <p>Hibernate ORM otherwise builds its own {@code ValidatorFactory} and uses
 * {@code DefaultConstraintValidatorFactory}, which cannot construct validators that only have a
 * Spring-injected constructor (HV000064).
 */
@Configuration
public class ValidationConfiguration {

    @Bean(name = "defaultValidator")
    public LocalValidatorFactoryBean defaultValidator(ApplicationContext applicationContext) {
        LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
        factoryBean.setConstraintValidatorFactory(
            new SpringConstraintValidatorFactory(applicationContext.getAutowireCapableBeanFactory())
        );
        return factoryBean;
    }

    /**
     * Wires the same {@link jakarta.validation.ValidatorFactory} into Hibernate's JPA bootstrap
     * (property {@link ValidationSettings#JAKARTA_VALIDATION_FACTORY}).
     */
    @Bean
    public HibernatePropertiesCustomizer hibernateJakartaValidationFactory(LocalValidatorFactoryBean defaultValidator) {
        return hibernateProperties -> hibernateProperties.put(ValidationSettings.JAKARTA_VALIDATION_FACTORY, defaultValidator);
    }
}
