package org.npci.bhim.BHIMSMSserviceAPI.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final ReactiveRedisTemplate<String, Object> redisTemplate;

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
