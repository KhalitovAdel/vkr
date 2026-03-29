package com.transport.system.config;

import java.time.Duration;
import org.ehcache.config.builders.*;
import org.ehcache.jsr107.Eh107Configuration;
import org.hibernate.cache.jcache.ConfigSettings;
import org.springframework.boot.cache.autoconfigure.JCacheManagerCustomizer;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.jhipster.config.JHipsterProperties;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(JHipsterProperties jHipsterProperties) {
        var ehcache = jHipsterProperties.getCache().getEhcache();

        jcacheConfiguration = Eh107Configuration.fromEhcacheCacheConfiguration(
            CacheConfigurationBuilder.newCacheConfigurationBuilder(
                Object.class,
                Object.class,
                ResourcePoolsBuilder.heap(ehcache.getMaxEntries())
            )
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(ehcache.getTimeToLiveSeconds())))
                .build()
        );
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cacheManager) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cacheManager);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            createCache(cm, com.transport.system.repository.UserRepository.USERS_BY_LOGIN_CACHE);
            createCache(cm, com.transport.system.repository.UserRepository.USERS_BY_EMAIL_CACHE);
            createCache(cm, com.transport.system.domain.User.class.getName());
            createCache(cm, com.transport.system.domain.Authority.class.getName());
            createCache(cm, com.transport.system.domain.User.class.getName() + ".authorities");
            createCache(cm, com.transport.system.domain.Vehicle.class.getName());
            createCache(cm, com.transport.system.domain.Vehicle.class.getName() + ".trips");
            createCache(cm, com.transport.system.domain.Vehicle.class.getName() + ".events");
            createCache(cm, com.transport.system.domain.Driver.class.getName());
            createCache(cm, com.transport.system.domain.Driver.class.getName() + ".trips");
            createCache(cm, com.transport.system.domain.Route.class.getName());
            createCache(cm, com.transport.system.domain.Route.class.getName() + ".trips");
            createCache(cm, com.transport.system.domain.Route.class.getName() + ".routeStops");
            createCache(cm, com.transport.system.domain.Stop.class.getName());
            createCache(cm, com.transport.system.domain.Stop.class.getName() + ".routeStops");
            createCache(cm, com.transport.system.domain.RouteStop.class.getName());
            createCache(cm, com.transport.system.domain.Trip.class.getName());
            createCache(cm, com.transport.system.domain.Trip.class.getName() + ".events");
            createCache(cm, com.transport.system.domain.Waybill.class.getName());
            createCache(cm, com.transport.system.domain.Event.class.getName());
            // jhipster-needle-ehcache-add-entry
        };
    }

    private void createCache(javax.cache.CacheManager cm, String cacheName) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        } else {
            cm.createCache(cacheName, jcacheConfiguration);
        }
    }
}
