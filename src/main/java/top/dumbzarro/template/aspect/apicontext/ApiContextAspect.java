//package top.dumbzarro.template.aspect;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.*;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//import top.dumbzarro.template.common.constant.AspectOrderConstant;
//
//import java.lang.ScopedValue;
//import java.time.Instant;
//import java.util.UUID;
//
//@Aspect
//@Component
//@Order(AspectOrderConstant.API_CONTEXT)
//public class ApiContextAspect {
//
//    public static final ScopedValue<ApiContext> CONTEXT = ScopedValue.newInstance();
//
//
//    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
//    public Object aroundApi(ProceedingJoinPoint pjp) throws Throwable {
//        String name = pjp.getSignature().toShortString();
//        ApiContext ctx = ApiContext.of(Source.REQUEST, name);
//        return ScopedValue.where(CONTEXT, ctx).call(() -> pjp.proceed());
//    }
//
//
//    public record ApiContext(
//            Source type, // api类型：REQUEST、JOB、LISTENER
//            String uri, // 具体的接口名、任务名、监听器名
//            Instant startTime, // 开始时间
//            String contextId // 唯一标识
//    ) {
//        public static ApiContext of(Source type, String uri) {
//            return new ApiContext(type, uri, Instant.now(), UUID.randomUUID().toString());
//        }
//
//    }
//    public enum Source {
//        REQUEST, JOB, LISTENER;
//    }
//}
