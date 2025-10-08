package top.dumbzarro.template.common.biz;

import lombok.Getter;

@Getter
public enum BizEnum {
    SUCCESS(10000, "操作成功"),
    SYSTEM_ERROR(10001, "系统异常"), // 非预期事件导致异常（代码问题）
    DATA_EXCEPTION(10002, "数据异常"), // 系统数据不一致导致异常
    PARAM_ERROR(10003, "参数错误"), // 用户入参导致异常
    REQUEST_ERROR(10004, "服务调用异常"), // 调用第三方导致异常
    OPERATION_FAILED(10005, "操作失败"), // 读写内部系统导致异常
    AUTH_FAILED(10006, "认证失败"), // 认证相关异常
    PERM_FAILED(10007, "鉴权失败"), // 鉴权相关异常
    RATE_LIMIT(10008, "操作过于频繁"), // 限流相关异常
    ;

    private final int code;
    private final String desc;

    BizEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
