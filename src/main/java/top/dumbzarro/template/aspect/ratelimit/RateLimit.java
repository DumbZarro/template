package top.dumbzarro.template.aspect.ratelimit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    /**
     * 限流的key，支持SpEL表达式
     */
    String key();

    /**
     * 时间间隔内的请求上限
     */
    int maxCall();

    /**
     * 限流时间间隔
     */
    int interval();

    /**
     * 限流时间间隔的单位
     */
    TimeUnit timeUnit() default TimeUnit.MINUTES;

    boolean resetInterval() default false;

    /**
     * 触发限流后的提示信息
     */
    String message() default "操作过于频繁，请稍后再试";
}