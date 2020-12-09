package com.github.ep2p.eleuth.config;

import com.google.common.cache.CacheBuilder;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfiguration {
    @Bean("requestCache")
    public CacheManager requestCache() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager() {
            @Override
            protected Cache createConcurrentMapCache(final String name) {
                return new ConcurrentMapCache(name,
                        CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).maximumSize(1000).build().asMap(), false);
            }
        };

        cacheManager.setCacheNames(Arrays.asList("availability", "data", "query"));
        return cacheManager;
    }

    @Bean("repositoryCache")
    public CacheManager repositoryCache() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager() {
            @Override
            protected Cache createConcurrentMapCache(final String name) {
                return new ConcurrentMapCache(name,
                        CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).maximumSize(300).build().asMap(), false);
            }
        };

        cacheManager.setCacheNames(Arrays.asList("repository"));
        return cacheManager;
    }

    @Bean("signedNodeInformationCache")
    public CacheManager signedNodeInformationCache() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager() {
            @Override
            protected Cache createConcurrentMapCache(final String name) {
                return new ConcurrentMapCache(name,
                        CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).maximumSize(2).build().asMap(), false);
            }
        };

        cacheManager.setCacheNames(Arrays.asList("publicKey", "certificate"));
        return cacheManager;
    }

}
