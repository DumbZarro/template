package top.dumbzarro.template.domain.security;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import top.dumbzarro.template.common.biz.BizClaims;
import top.dumbzarro.template.common.helper.jwt.SymmetricJwtHelper;
import top.dumbzarro.template.controller.auth.response.AuthResponse;
import top.dumbzarro.template.domain.security.jwt.JwtAccessDeniedHandler;
import top.dumbzarro.template.domain.security.jwt.JwtAuthenticationEntryPoint;
import top.dumbzarro.template.domain.security.jwt.JwtAuthenticationManager;
import top.dumbzarro.template.domain.security.jwt.JwtServerAuthenticationConverter;
import top.dumbzarro.template.domain.security.oauth.OAuth2UserServiceImpl;

@AllArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // 方法级别权限校验
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final OAuth2UserServiceImpl oAuth2UserService;
    private final JwtAuthenticationManager jwtAuthenticationManager;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final SymmetricJwtHelper jwtHelper;

    @Bean
    public SecurityFilterChain securityWebFilterChain(HttpSecurity http) throws Exception {
        // 设置认证范围
        http.authorizeHttpRequests(requests -> {
            requests.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll();
            requests.requestMatchers("/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll();
            requests.anyRequest().authenticated();
        });

        // 配置表单登录
        http.formLogin(formLogin -> formLogin
                .successHandler(formLoginAuthenticationSuccessHandler())
                .failureHandler(formLoginAuthenticationFailureHandler())
                .securityContextRepository(securityContextRepository())
        );

        // 配置OAuth2登录
        http.oauth2Login(oauth2 -> oauth2
                .successHandler(oauth2LoginAuthenticationSuccessHandler())
                .failureHandler(oauth2LoginAuthenticationFailureHandler())
                .securityContextRepository(securityContextRepository())
        );

        // 添加JWT认证过滤器
        AuthenticationFilter jwtFilter = new AuthenticationFilter(jwtAuthenticationManager, new JwtServerAuthenticationConverter());
        jwtFilter.setFailureHandler(jwtAuthenticationEntryPoint::commence);
//        jwtFilter.setRequestMatcher(PathPatternRequestMatcher.pathMatchers("/api/**"));
//        http.addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION);

        // 配置异常处理
        http.exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
        );

        // 禁用不需要的安全特性
//        http.httpBasic(ServerHttpSecurity.HttpBasicSpec::disable);
//        http.csrf(ServerHttpSecurity.CsrfSpec::disable);

        return http.build();
    }


    private AuthenticationSuccessHandler formLoginAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            // 生成JWT token
            String email = authentication.getName();
            String username = authentication.getName(); // 可以根据需要获取用户名

            BizClaims claims = new BizClaims(email, username);
            String token = jwtHelper.generateToken(email, claims);

            // 构建响应
            AuthResponse authResponse = AuthResponse.builder()
                    .email(email)
                    .username(username)
                    .token(token)
                    .build();

            // 返回JSON响应
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            String responseBody = String.format(
                    "{\"code\":200,\"msg\":\"登录成功\",\"data\":{\"token\":\"%s\",\"email\":\"%s\",\"username\":\"%s\"}}",
                    token, email, username
            );

        };
    }

    private AuthenticationSuccessHandler oauth2LoginAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {

            // 处理OAuth2用户信息
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            String email = oauth2User.getAttribute("email");
            String name = oauth2User.getAttribute("name");

            if (email == null) {
                email = oauth2User.getName(); // 如果没有email，使用name作为标识
            }

            // 生成JWT token
            BizClaims claims = new BizClaims(email, name);
            String token = jwtHelper.generateToken(email, claims);

            // 构建响应
            String responseBody = String.format(
                    "{\"code\":200,\"msg\":\"OAuth登录成功\",\"data\":{\"token\":\"%s\",\"email\":\"%s\",\"username\":\"%s\"}}",
                    token, email, name
            );

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        };
    }

    private AuthenticationFailureHandler formLoginAuthenticationFailureHandler() {
        return defaultAuthenticationFailureHandler();
    }

    private AuthenticationFailureHandler oauth2LoginAuthenticationFailureHandler() {
        return defaultAuthenticationFailureHandler();
    }

    private AuthenticationFailureHandler defaultAuthenticationFailureHandler() {
        return (request, response, exception) -> {

//            String errorMessage = "登录失败：" + exception.getMessage();
//            String responseBody = String.format("{\"code\":401,\"msg\":\"%s\"}", errorMessage);

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        };
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
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
