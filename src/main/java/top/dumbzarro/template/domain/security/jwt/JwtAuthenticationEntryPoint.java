package top.dumbzarro.template.domain.security.jwt;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.NonceExpiredException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {
    @Override
    public Void> commence(ServerWebExchange exchange, AuthenticationException e) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String errorMessage;
        if (e instanceof NonceExpiredException) {
            errorMessage = "Token已过期，请重新登录";
        } else if (e instanceof BadCredentialsException) {
            errorMessage = "Token无效：" + e.getMessage();
        } else {
            errorMessage = "认证失败：" + e.getMessage();
        }

        String body = String.format("{\"code\":401,\"msg\":\"%s\"}", errorMessage);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                .bufferFactory().wrap(body.getBytes())));
    }
}