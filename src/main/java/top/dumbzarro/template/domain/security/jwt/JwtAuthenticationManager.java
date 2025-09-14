package top.dumbzarro.template.domain.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import top.dumbzarro.template.common.biz.BizClaims;
import top.dumbzarro.template.common.helper.jwt.SymmetricJwtHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 对未认证的 Authentication 对象进行认证（设置GrantedAuthority）
 */
@Slf4j
@Component
public class JwtAuthenticationManager implements AuthenticationManager {

    private final SymmetricJwtHelper symmetricJwtHelper;

    public JwtAuthenticationManager(SymmetricJwtHelper symmetricJwtHelper) {
        this.symmetricJwtHelper = symmetricJwtHelper;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        if (!(authentication instanceof JwtAuthentication(String token))) {
            throw new AuthenticationServiceException("Unsupported authentication type");
        }
        try {
            BizClaims claims = symmetricJwtHelper.parseToken(token);
            List<GrantedAuthority> authorities = new ArrayList<>();
            if (!CollectionUtils.isEmpty(claims.getRoles())) {
                claims.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
            }
            if (!CollectionUtils.isEmpty(claims.getPerms())) {
                claims.getPerms().forEach(perm -> authorities.add(new SimpleGrantedAuthority(perm)));
            }
            UsernamePasswordAuthenticationToken.authenticated(claims, token, authorities);
            return authentication;
        } catch (ExpiredJwtException e) {
            log.error("token expired: authentication:{}", authentication, e);
            throw new BadCredentialsException("token已过期", e);
        } catch (Exception e) {
            log.error("token parse fail. authentication:{}", authentication, e);
            throw new AuthenticationServiceException("token解析异常", e);
        }
    }
}
