package top.dumbzarro.template.domain.security;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import top.dumbzarro.template.domain.security.jwt.JwtAccessDeniedHandler;
import top.dumbzarro.template.domain.security.jwt.JwtAuthenticationEntryPoint;
import top.dumbzarro.template.domain.security.jwt.JwtAuthenticationManager;
import top.dumbzarro.template.domain.security.jwt.JwtServerAuthenticationConverter;
import top.dumbzarro.template.domain.security.oauth.OAuth2AuthenticationFailureHandler;
import top.dumbzarro.template.domain.security.oauth.OAuth2AuthenticationSuccessHandler;
import top.dumbzarro.template.domain.security.oauth.OAuth2UserServiceImpl;

@AllArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // 方法级别权限校验
public class SecurityConfig {

    private final JwtAuthenticationManager jwtAuthenticationManager;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    private final OAuth2UserServiceImpl oauth2UserService;
    private final OAuth2AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oauth2AuthenticationFailureHandler;

    @Bean
    public SecurityFilterChain securityWebFilterChain(HttpSecurity http) throws Exception {
        // 设置认证范围
        http.authorizeHttpRequests(requests -> {
            requests.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll();
            requests.requestMatchers("/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll();
            requests.anyRequest().authenticated();
        });

        // 添加JWT认证过滤器
        AuthenticationFilter jwtFilter = new AuthenticationFilter(jwtAuthenticationManager, new JwtServerAuthenticationConverter());
        jwtFilter.setFailureHandler(jwtAuthenticationEntryPoint::commence);
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        // 配置OAuth2登录
        http.oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(oauth2UserService))
                .successHandler(oauth2AuthenticationSuccessHandler)
                .failureHandler(oauth2AuthenticationFailureHandler)
        );

        // 配置异常处理
        http.exceptionHandling(configurer -> configurer
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
        );

        // 禁用不需要的安全特性
        http.csrf(AbstractHttpConfigurer::disable);
        http.formLogin(AbstractHttpConfigurer::disable);
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    /**
     * 使用BCrypt编码器加密密码
     */
    @Primary
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
