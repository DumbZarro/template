package top.dumbzarro.template.config;

import top.dumbzarro.template.common.biz.BizEnum;
import top.dumbzarro.template.common.biz.BizException;
import top.dumbzarro.template.common.biz.BizResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BizException.class)
    public ResponseEntity<BizResponse<?>> handleBizException(BizException e) {
        log.warn("GlobalExceptionHandler handleBizException.", e);
        return ResponseEntity.ok().body(new BizResponse<>(e));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<BizResponse<?>> handleWebExchangeBindException(WebExchangeBindException e) {
        log.warn("GlobalExceptionHandler handleWebExchangeBindException.", e);
        String msg = e.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).reduce((m1, m2) -> m1 + "; " + m2).orElse("参数校验失败");
        return ResponseEntity.ok().body(new BizResponse<>(BizEnum.PARAM_ERROR, msg));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<BizResponse<?>> handleBadCredentialsException(BadCredentialsException e) {
        log.warn("GlobalExceptionHandler handleBadCredentialsException.", e);
        return ResponseEntity.ok().body(new BizResponse<>(BizEnum.PARAM_ERROR, "账号密码错误"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BizResponse<?>> handleException(Exception e) {
        log.warn("GlobalExceptionHandler handleException.", e);
        return ResponseEntity.ok().body(new BizResponse<>(BizEnum.SYSTEM_ERROR, e.getMessage()));
    }


}