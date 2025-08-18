package top.dumbzarro.template.common.helper.jwt;

import top.dumbzarro.template.common.biz.BizClaims;
import top.dumbzarro.template.common.util.JsonUtil;
import top.dumbzarro.template.common.util.JwtUtil;
import top.dumbzarro.template.common.util.PemUtil;
import top.dumbzarro.template.config.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.KeyPair;

@Slf4j
@Component
public class AsymmetricJwtHelper implements JwtHelper {
    KeyPair keyPair;
    AppConfig appConfig;

    public AsymmetricJwtHelper(AppConfig appConfig) throws IOException {
        keyPair = PemUtil.loadKeyPair(appConfig.getPrivateKeyRes(), appConfig.getPublicKeyRes());
    }

    public String generateToken(String subject, BizClaims claims) {
        return JwtUtil.generateToken(subject, JsonUtil.toMap(claims), appConfig.getJwtExpirationMs(), keyPair.getPrivate());
    }

    public BizClaims parseToken(String token) {
        return JsonUtil.fromMap(JwtUtil.parseToken(token, keyPair.getPublic()), BizClaims.class);
    }

}
