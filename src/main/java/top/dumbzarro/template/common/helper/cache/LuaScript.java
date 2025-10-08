package top.dumbzarro.template.common.helper.cache;

import org.springframework.data.redis.core.script.DefaultRedisScript;

public class LuaScript {

    public static final DefaultRedisScript<Long> INC_WITH_TTL_WHEN_CREATE = incrWithTtlWhenCreate();
    public static final DefaultRedisScript<Long> INC_WITH_TTL_WHEN_UPDATE = incWithTtlWhenUpdate();

    private static DefaultRedisScript<Long> incrWithTtlWhenCreate() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText("""
                local current = redis.call('INCR', KEYS[1])
                if current == 1 then
                    redis.call('EXPIRE', KEYS[1], ARGV[1])
                end
                return current
                """);
        script.setResultType(Long.class);
        return script;
    }

    private static DefaultRedisScript<Long> incWithTtlWhenUpdate() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText("""
                local current = redis.call('INCR', KEYS[1])
                redis.call('EXPIRE', KEYS[1], ARGV[1])
                return current
                """);
        script.setResultType(Long.class);
        return script;
    }

}
