package top.dumbzarro.template.common.biz;

import lombok.Getter;

@Getter
public class BizException extends RuntimeException  {

    private final Integer code;
    private final String message;

    public BizException(BizEnum bizEnum) {
        super(bizEnum.getDesc());
        this.code = bizEnum.getCode();
        this.message = bizEnum.getDesc();
    }

    public BizException(BizEnum bizEnum, String message) {
        super(message);
        this.code = bizEnum.getCode();
        this.message = message;
    }

}