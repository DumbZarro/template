package top.dumbzarro.template.domain.security.global;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.NonceExpiredException;
import org.springframework.stereotype.Component;
import top.dumbzarro.template.common.biz.BizEnum;
import top.dumbzarro.template.common.biz.BizResponse;
import top.dumbzarro.template.common.util.JsonUtil;

import java.io.IOException;
import java.util.Optional;

@Component
public class GlobalAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String errorMessage; // TODO
        if (authException instanceof NonceExpiredException) {
            errorMessage = "Token已过期，请重新登录";
        } else if (authException instanceof BadCredentialsException) {
            errorMessage = "Token无效：" + authException.getMessage();
        } else if (authException instanceof AuthenticationServiceException) {
            errorMessage = "认证服务异常：" + authException.getMessage();
        } else {
            errorMessage = "认证失败：" + authException.getMessage();
        }
        response.getWriter().write(Optional.ofNullable(JsonUtil.toJson(new BizResponse<>(BizEnum.AUTH_FAILED, errorMessage))).orElse(StringUtils.EMPTY));
    }
}