package top.dumbzarro.template.aspect.ratelimit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import top.dumbzarro.template.common.biz.BizEnum;
import top.dumbzarro.template.common.biz.BizException;
import top.dumbzarro.template.common.constant.AspectOrderConstant;
import top.dumbzarro.template.common.constant.RedisConstant;
import top.dumbzarro.template.common.helper.cache.RedisHelper;
import top.dumbzarro.template.common.util.SpELUtil;

import java.util.Objects;

@Aspect
@Component
@Slf4j
@Order(AspectOrderConstant.RATE_LIMIT)
@RequiredArgsConstructor
public class RateLimitAspect {

    private final RedisHelper redisHelper;

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint pjp, RateLimit rateLimit) throws Throwable {
        String key = RedisConstant.RATE_LIMIT_PREFIX + SpELUtil.parse(rateLimit.key(), pjp);
        Long currentCall = redisHelper.increment(key, rateLimit.interval(), rateLimit.timeUnit(), rateLimit.resetInterval());
        if (Objects.isNull(currentCall) || currentCall > rateLimit.maxCall()) {
            log.info("RateLimitAspect limit key:{}, maxCall:{}, interval:{}, timeUnit:{}, resetInterval:{}", key, rateLimit.maxCall(), rateLimit.interval(), rateLimit.timeUnit(), rateLimit.resetInterval());
            throw new BizException(BizEnum.RATE_LIMIT, rateLimit.message());
        }
        return pjp.proceed();
    }


}
