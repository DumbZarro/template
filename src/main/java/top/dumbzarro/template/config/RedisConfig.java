package top.dumbzarro.template.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@AutoConfigureAfter(RedisAutoConfiguration.class)
@Configuration
public class RedisConfig {

    @Bean
    RedisTemplate<String, Object> jsonRedisTemplate(RedisConnectionFactory factory) {
        // GenericJackson2JsonRedisSerializer 序列化后待上全限定类名
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setDefaultSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        return template;
    }

}