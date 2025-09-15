package top.dumbzarro.template.domain.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 将 HTTP 请求转换为一个 未认证的 Authentication 对象
 */
@Slf4j
@Component
public class JwtAuthenticationConverter implements AuthenticationConverter {

    @Override
    public Authentication convert(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(authorization)) {
            // 考虑不需要认证的场景，后续流程再决定是否拒绝访问
            return null;
        }

        if (!authorization.startsWith("Bearer ")) {
            // 格式错误，直接拒绝
            log.warn("Authorization header is invalid. authorization:{}", authorization);
            throw new AuthenticationServiceException("Authorization header must start with Bearer");
        }
        return new JwtAuthentication(authorization.substring(7));

    }
}