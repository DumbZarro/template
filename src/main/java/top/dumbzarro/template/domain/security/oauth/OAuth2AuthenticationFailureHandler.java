package top.dumbzarro.template.domain.security.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import top.dumbzarro.template.common.biz.BizEnum;
import top.dumbzarro.template.common.biz.BizResponse;
import top.dumbzarro.template.common.util.JsonUtil;

import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        String errorMessage = "登录失败：" + exception.getMessage();
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String responseBody = JsonUtil.toJson(new BizResponse<>(BizEnum.AUTH_FAILED, errorMessage));
        if (Objects.nonNull(responseBody)) {
            response.getWriter().write(responseBody);
        }
    }
}
