package top.dumbzarro.template.domain.security.oauth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import top.dumbzarro.template.common.biz.BizClaims;
import top.dumbzarro.template.common.helper.jwt.SymmetricJwtHelper;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final SymmetricJwtHelper symmetricJwtHelper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 处理OAuth2用户信息
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");

        if (email == null) {
            email = oauth2User.getName(); // 如果没有email，使用name作为标识
        }

        // 生成JWT token
        BizClaims claims = BizClaims.builder().email(email).nickname(name).build();

        String token = symmetricJwtHelper.generateToken(email, claims);

        // 构建响应
        String responseBody = String.format(
                "{\"code\":200,\"msg\":\"OAuth登录成功\",\"data\":{\"token\":\"%s\",\"email\":\"%s\",\"username\":\"%s\"}}",
                token, email, name
        );

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    }
}
