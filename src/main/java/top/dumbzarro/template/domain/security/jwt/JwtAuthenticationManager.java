package top.dumbzarro.template.domain.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import top.dumbzarro.template.common.biz.BizClaims;
import top.dumbzarro.template.common.helper.jwt.SymmetricJwtHelper;

@Slf4j
@Component
public class JwtAuthenticationManager implements AuthenticationManager {

    private final SymmetricJwtHelper jwtProcessor;

    public JwtAuthenticationManager(SymmetricJwtHelper jwtProcessor) {
        this.jwtProcessor = jwtProcessor;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        try {
            String token = authentication.getCredentials().toString();
            BizClaims claims = jwtProcessor.parseToken(token);
            // TODO 角色的权限??
            return authentication;
        } catch (ExpiredJwtException e) {
            log.error("Token已过期: {}", e.getMessage());
            throw new BadCredentialsException("Token已过期", e);
        } catch (Exception e) {
            log.error("Token解析异常: {}", e.getMessage());
            throw new BadCredentialsException("Token解析异常", e);
        }
    }
}
