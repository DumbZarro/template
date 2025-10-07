package top.dumbzarro.template.common.util;

import top.dumbzarro.template.common.biz.BizEnum;
import top.dumbzarro.template.common.biz.BizException;
import top.dumbzarro.template.common.enums.BaseEnum;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumUtil {

    private EnumUtil() {
    }

    private static final Map<Class<?>, Map<Integer, ?>> ENUM_CODE_CACHE = new ConcurrentHashMap<>();

    public static <E extends BaseEnum> E getByCode(Class<E> enumClass, Integer code) {
        @SuppressWarnings("unchecked")
        Map<Integer, E> codeMap = (Map<Integer, E>) ENUM_CODE_CACHE.computeIfAbsent(enumClass, clazz ->
                Stream.of((E[]) clazz.getEnumConstants()).collect(Collectors.toUnmodifiableMap(BaseEnum::getCode, status -> status))
        );
        return Optional.ofNullable(codeMap.get(code)).orElseThrow(() -> new BizException(BizEnum.DATA_EXCEPTION, "枚举异常"));
    }

}
