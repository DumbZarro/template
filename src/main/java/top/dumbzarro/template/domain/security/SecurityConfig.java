package top.dumbzarro.template.domain.security;

import top.dumbzarro.template.common.biz.BizClaims;
import top.dumbzarro.template.common.helper.jwt.SymmetricJwtHelper;
import top.dumbzarro.template.controller.auth.response.AuthResponse;
import top.dumbzarro.template.domain.security.jwt.JwtAccessDeniedHandler;
import top.dumbzarro.template.domain.security.jwt.JwtAuthenticationEntryPoint;
import top.dumbzarro.template.domain.security.jwt.JwtReactiveAuthenticationManager;
import top.dumbzarro.template.domain.security.jwt.JwtServerAuthenticationConverter;
import top.dumbzarro.template.domain.security.oauth.ReactiveOAuth2UserServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@EnableWebFluxSecurity
@Configuration
@EnableReactiveMethodSecurity // 方法级别权限校验
public class SecurityConfig {

    private final ReactiveUserDetailsService reactiveUserDetailsService;
    private final ReactiveOAuth2UserServiceImpl oAuth2UserService;
    private final JwtReactiveAuthenticationManager jwtAuthenticationManager;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final SymmetricJwtHelper jwtHelper;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        // 设置认证范围
        http.authorizeExchange(exchanges -> {
            // 允许所有人访问静态资源
            exchanges.matchers(PathRequest.toStaticResources().atCommonLocations()).permitAll();
            // 允许所有人登陆注册接口
            exchanges.pathMatchers("/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll();
            // 其他所有请求都需要认证
            exchanges.anyExchange().authenticated();
        });

        // 配置表单登录
        http.formLogin(formLogin -> formLogin
                .authenticationSuccessHandler(formLoginAuthenticationSuccessHandler())
                .authenticationFailureHandler(formLoginAuthenticationFailureHandler())
                .securityContextRepository(securityContextRepository())
        );

        // 配置OAuth2登录
        http.oauth2Login(oauth2 -> oauth2
                .authenticationSuccessHandler(oauth2LoginAuthenticationSuccessHandler())
                .authenticationFailureHandler(oauth2LoginAuthenticationFailureHandler())
                .securityContextRepository(securityContextRepository())
        );

        // 添加JWT认证过滤器
        AuthenticationWebFilter jwtFilter = new AuthenticationWebFilter(jwtAuthenticationManager);
        jwtFilter.setServerAuthenticationConverter(new JwtServerAuthenticationConverter());
        jwtFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/api/**"));
        jwtFilter.setAuthenticationFailureHandler((webFilterExchange, exception) -> jwtAuthenticationEntryPoint.commence(webFilterExchange.getExchange(), exception));
        http.addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION);

        // 配置异常处理
        http.exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
        );

        // 禁用不需要的安全特性
        http.httpBasic(ServerHttpSecurity.HttpBasicSpec::disable);
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);

        return http.build();
    }


    private ServerAuthenticationSuccessHandler formLoginAuthenticationSuccessHandler() {
        return (webFilterExchange, authentication) -> {
            ServerWebExchange exchange = webFilterExchange.getExchange();
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
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            String responseBody = String.format(
                    "{\"code\":200,\"msg\":\"登录成功\",\"data\":{\"token\":\"%s\",\"email\":\"%s\",\"username\":\"%s\"}}",
                    token, email, username
            );

            return exchange.getResponse().writeWith(
                    Mono.just(exchange.getResponse().bufferFactory().wrap(responseBody.getBytes()))
            );
        };
    }

    private ServerAuthenticationSuccessHandler oauth2LoginAuthenticationSuccessHandler() {
        return (webFilterExchange, authentication) -> {
            ServerWebExchange exchange = webFilterExchange.getExchange();

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

            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return exchange.getResponse().writeWith(
                    Mono.just(exchange.getResponse().bufferFactory().wrap(responseBody.getBytes()))
            );
        };
    }

    private ServerAuthenticationFailureHandler formLoginAuthenticationFailureHandler() {
        return defaultAuthenticationFailureHandler();
    }

    private ServerAuthenticationFailureHandler oauth2LoginAuthenticationFailureHandler() {
        return defaultAuthenticationFailureHandler();
    }

    private ServerAuthenticationFailureHandler defaultAuthenticationFailureHandler() {
        return (webFilterExchange, exception) -> {
            ServerWebExchange exchange = webFilterExchange.getExchange();

            String errorMessage = "登录失败：" + exception.getMessage();
            String responseBody = String.format("{\"code\":401,\"msg\":\"%s\"}", errorMessage);

            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

            return exchange.getResponse().writeWith(
                    Mono.just(exchange.getResponse().bufferFactory().wrap(responseBody.getBytes()))
            );
        };
    }

    @Bean
    public ServerSecurityContextRepository securityContextRepository() {
        return new WebSessionServerSecurityContextRepository();
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
