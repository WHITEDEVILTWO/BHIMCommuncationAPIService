package org.npci.bhim.BHIMSMSserviceAPI.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final ReactiveRedisTemplate<String, Object> redisTemplate;

    @Autowired
    ReactiveRedisTemplate<String, Object> redisTemplate2;

    public Mono<Boolean> save(String key, Object value, Duration ttl) {
        return redisTemplate.opsForValue().set(key, value, ttl);
    }
    public Mono<Boolean> save(String key, Object value) {
        return redisTemplate.opsForValue().set(key, value);
    }

    public Mono<Object> get(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
