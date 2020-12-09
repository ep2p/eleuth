package com.github.ep2p.eleuth.service;

import com.github.ep2p.eleuth.model.dto.route.AvailabilityMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class RequestCacheService {
    private final CacheManager requestCache;

    @Autowired
    public RequestCacheService(CacheManager requestCache) {
        this.requestCache = requestCache;
    }

    public AvailabilityMessage getAvailabilityMessage(String reqId){
        return Objects.requireNonNull(requestCache.getCache("availability")).get(reqId, AvailabilityMessage.class);
    }

    public void addAvailabilityRequest(String reqId, AvailabilityMessage request){
        Objects.requireNonNull(requestCache.getCache("availability")).putIfAbsent(reqId, request);
    }

    public void evictAvailability(String reqId){
        Objects.requireNonNull(requestCache.getCache("availability")).evictIfPresent(reqId);
    }

}
