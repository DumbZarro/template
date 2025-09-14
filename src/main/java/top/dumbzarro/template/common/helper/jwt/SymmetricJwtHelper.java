package top.dumbzarro.template.common.helper.jwt;

import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import top.dumbzarro.template.common.biz.BizClaims;
import top.dumbzarro.template.common.util.JsonUtil;
import top.dumbzarro.template.common.util.JwtUtil;
import top.dumbzarro.template.config.AppConfig;

import javax.crypto.SecretKey;

@Component
@AllArgsConstructor
public class SymmetricJwtHelper implements JwtHelper {

    private final AppConfig appConfig;

    public String generateToken(String subject, BizClaims claims) {
        SecretKey secretKey = Keys.hmacShaKeyFor(appConfig.getJwtSecret().getBytes());
        return JwtUtil.generateToken(subject, JsonUtil.toMap(claims), appConfig.getJwtExpirationMs(), secretKey);
    }

    public BizClaims parseToken(String token) {
        SecretKey secretKey = Keys.hmacShaKeyFor(appConfig.getJwtSecret().getBytes());
        return JsonUtil.fromMap(JwtUtil.parseToken(token, secretKey), BizClaims.class);
    }

}
