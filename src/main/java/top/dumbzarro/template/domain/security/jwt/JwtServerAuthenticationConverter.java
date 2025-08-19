package top.dumbzarro.template.domain.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;

import java.util.Objects;

public class JwtServerAuthenticationConverter implements AuthenticationConverter {

    @Override
    public Authentication convert(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (Objects.nonNull(authorization) && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            return new UsernamePasswordAuthenticationToken(token, token);
        }
        return null; // TODO
    }
}