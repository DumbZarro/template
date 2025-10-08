package top.dumbzarro.template.common.helper.cache;


import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import top.dumbzarro.template.common.constant.RedisConstant;
import top.dumbzarro.template.common.util.JsonUtil;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisHelper {
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, Object> jsonRedisTemplate;


    public String tryGet(String key) {
        try {
            return stringRedisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.info("RedisHelper tryGet fail. key:{}", key, e);
            return null;
        }
    }

    public String tryGetNullable(String key) {
        try {
            String cached = stringRedisTemplate.opsForValue().get(key);
            if (Objects.equals(cached, RedisConstant.NULL)) {
                return null;
            }
            return cached;
        } catch (Exception e) {
            log.info("RedisHelper tryGetNullable fail. key:{}", key, e);
            return null;
        }
    }

    public <T> T tryGet(String key, TypeReference<T> typeReference) {
        try {
            String cached = stringRedisTemplate.opsForValue().get(key);
            if (!StringUtils.hasText(cached)) {
                return null;
            }
            return JsonUtil.parse(cached, typeReference);
        } catch (Exception e) {
            log.info("RedisHelper tryGet fail. key:{}", key, e);
            return null;
        }
    }

    public <T> T tryGetNullable(String key, TypeReference<T> typeReference) {
        try {
            String cached = stringRedisTemplate.opsForValue().get(key);
            if (!StringUtils.hasText(cached) || Objects.equals(cached, RedisConstant.NULL)) {
                return null;
            }
            return JsonUtil.parse(cached, typeReference);
        } catch (Exception e) {
            log.info("RedisHelper tryGet fail. key:{}", key, e);
            return null;
        }
    }

    public void trySet(String key, String value, long ttl, TimeUnit timeUnit) {
        try {
            stringRedisTemplate.opsForValue().set(key, value, ttl, timeUnit);
        } catch (Exception e) {
            log.info("RedisHelper trySet fail. key:{}, value:{}, ttl:{}, timeUnit:{}", key, value, ttl, timeUnit, e);
        }
    }

    public void trySet(String key, Object value, long ttl, TimeUnit timeUnit) {
        try {
            stringRedisTemplate.opsForValue().set(key, Objects.requireNonNull(JsonUtil.toJson(value)), ttl, timeUnit);
        } catch (Exception e) {
            log.info("RedisHelper trySet fail. key:{}, value:{}, ttl:{}, timeUnit:{}", key, value, ttl, timeUnit, e);
        }
    }

    public void trySetNullable(String key, String value, long ttl, TimeUnit timeUnit) {
        try {
            if (Objects.nonNull(value)) {
                stringRedisTemplate.opsForValue().set(key, value, ttl, timeUnit);
            } else {
                stringRedisTemplate.opsForValue().set(key, RedisConstant.NULL, ttl, timeUnit);
            }
        } catch (Exception e) {
            log.info("RedisHelper trySetNullable fail. key:{}, value:{}, ttl:{}, timeUnit:{}", key, value, ttl, timeUnit, e);
        }
    }

    public void trySetNullable(String key, Object value, long ttl, TimeUnit timeUnit) {
        try {
            if (Objects.nonNull(value)) {
                stringRedisTemplate.opsForValue().set(key, Objects.requireNonNull(JsonUtil.toJson(value)), ttl, timeUnit);
            } else {
                stringRedisTemplate.opsForValue().set(key, RedisConstant.NULL, ttl, timeUnit);
            }
        } catch (Exception e) {
            log.info("RedisHelper trySetNullable fail. key:{}, value:{}, ttl:{}, timeUnit:{}", key, value, ttl, timeUnit, e);
        }
    }

    public String cacheAside(String key, Supplier<String> supplier, long ttl, TimeUnit timeUnit) {
        String cached = tryGet(key);
        if (Objects.nonNull(cached)) {
            return cached;
        }
        String value = supplier.get();
        if (Objects.nonNull(value)) {
            trySet(key, value, ttl, timeUnit);
        }
        return value;
    }

    public <T> T cacheAside(String key, Supplier<T> supplier, TypeReference<T> typeReference, long ttl, TimeUnit timeUnit) {
        T cached = tryGet(key, typeReference);
        if (Objects.nonNull(cached)) {
            return cached;
        }
        T value = supplier.get();
        if (Objects.nonNull(value)) {
            trySet(key, value, ttl, timeUnit);
        }
        return value;
    }


    public String cacheAsideNullable(String key, Supplier<String> supplier, long ttl, TimeUnit timeUnit) {
        String cached = tryGetNullable(key);
        if (Objects.nonNull(cached)) {
            return cached;
        }
        String value = supplier.get();
        if (Objects.nonNull(value)) {
            trySetNullable(key, value, ttl, timeUnit);
        }
        return value;
    }

    public <T> T cacheAsideNullable(String key, Supplier<T> supplier, TypeReference<T> typeReference, long ttl, TimeUnit timeUnit) {
        T cached = tryGetNullable(key, typeReference);
        if (Objects.nonNull(cached)) {
            return cached;
        }
        T value = supplier.get();
        if (Objects.nonNull(value)) {
            trySetNullable(key, value, ttl, timeUnit);
        }
        return value;
    }

    public Long increment(String key, long ttl, TimeUnit timeUnit, boolean resetTtl) {
        try {
            // KEYS 列表
            List<String> keys = Collections.singletonList(key);
            // ARGV 列表
            Object[] args = {String.valueOf(timeUnit.toSeconds(ttl))};

            if (resetTtl) {
                return stringRedisTemplate.execute(LuaScript.INC_WITH_TTL_WHEN_UPDATE, keys, args);
            } else {
                return stringRedisTemplate.execute(LuaScript.INC_WITH_TTL_WHEN_CREATE, keys, args);
            }
        } catch (Exception e) {
            log.error("RedisHelper increment fail. key:{}, ttl:{}, timeUnit:{}, resetTtl:{}", key, ttl, timeUnit, resetTtl, e);
            return null;
        }
    }

}