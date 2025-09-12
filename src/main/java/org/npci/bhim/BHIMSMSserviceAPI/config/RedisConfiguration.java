package org.npci.bhim.BHIMSMSserviceAPI.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
/**
 * | Bean                                    | Purpose                                        |
 * | --------------------------------------- | ---------------------------------------------- |
 * | `ReactiveRedisConnectionFactory`        | Connects to Redis using Lettuce (non-blocking) |
 * | `ReactiveRedisTemplate<String, Object>` | API to interact with Redis reactively          |
 * | `StringRedisSerializer`                 | Makes keys human-readable                      |
 * | `GenericJackson2JsonRedisSerializer`    | Automatically serializes POJOs to/from JSON    |
 * */
@Configuration
public class RedisConfiguration {

    @Bean
    @Primary
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        RedisSerializationContext<String, Object> serializationContext = RedisSerializationContext
                .<String, Object>newSerializationContext(new StringRedisSerializer())
                .value(new GenericJackson2JsonRedisSerializer())
                .build();

        return new ReactiveRedisTemplate<>(factory, serializationContext);
    }

}
