package top.dumbzarro.template.aspect.ratelimit;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import top.dumbzarro.template.common.constant.AspectOrderConstant;
import top.dumbzarro.template.common.constant.RedisConstant;
import top.dumbzarro.template.common.util.SpELUtil;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Order(AspectOrderConstant.RATE_LIMIT)
@RequiredArgsConstructor
public class RateLimitAspect {

    private final StringRedisTemplate stringRedisTemplate;

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint pjp, RateLimit rateLimit) throws Throwable {
        String key = SpELUtil.parse(rateLimit.key(), pjp);

        if (!isAllowed(key, rateLimit.maxCalls(), rateLimit.interval(), rateLimit.timeUnit())) {
            // TODO
            throw new RuntimeException(rateLimit.message());
        }
        return pjp.proceed();
    }


    public boolean isAllowed(String key, int maxCalls, int interval, TimeUnit timeUnit) {
        String redisKey = RedisConstant.RATE_LIMIT_PREFIX + key;
        Long currentAttempts = stringRedisTemplate.opsForValue().increment(redisKey);

        if (currentAttempts != null && currentAttempts == 1L) {
            stringRedisTemplate.expire(redisKey, interval, timeUnit);
        }

        return currentAttempts != null && currentAttempts <= maxCalls;
    }


}
