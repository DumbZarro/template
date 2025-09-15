package top.dumbzarro.template.domain.security.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtFilter extends AuthenticationFilter {

    @Autowired
    public JwtFilter(JwtAuthenticationManager jwtAuthenticationManager, JwtAuthenticationConverter jwtAuthenticationConverter) {
        super(jwtAuthenticationManager, jwtAuthenticationConverter);
    }
}
