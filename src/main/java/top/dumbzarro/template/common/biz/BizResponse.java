package top.dumbzarro.template.common.biz;

import lombok.Data;

/**
 * 失败不返回数据，成功不自定义code
 */
@Data
public class BizResponse<T> {
    private int code;
    private String message;
    private T data;

    public BizResponse(T data) {
        this(BizEnum.SUCCESS.getCode(), BizEnum.SUCCESS.getDesc(), data);
    }

    public BizResponse(BizException exception) {
        this(exception.getCode(), exception.getMessage(), null);
    }

    public BizResponse(BizEnum bizEnum, String message) {
        this(bizEnum.getCode(), message, null);
    }

    private BizResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}