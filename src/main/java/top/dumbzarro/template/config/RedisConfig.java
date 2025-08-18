package top.dumbzarro.template.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@AutoConfigureAfter(RedisAutoConfiguration.class)
@Configuration
public class RedisConfig {

    @Bean
    RedisTemplate<Object, Object> RedisTemplate(RedisConnectionFactory factory) {
        // GenericJackson2JsonRedisSerializer 序列化后待上全限定类名
        return new RedisTemplate<>(factory, RedisSerializationContext.fromSerializer(new Jackson2JsonRedisSerializer<>(Object.class)));
    }

}