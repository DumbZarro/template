package top.dumbzarro.template.common.biz;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class BizClaims {
    /**
     * 自定义
     */
    private String email;
    private String nickname;
    private Set<String> roles;
    private Set<String> perms;


    /**
     * Jwt标准 {@link io.jsonwebtoken.Claims}
     */
    // 签发者（Issuer）
    private String iss;
    // 主题（Subject，用户唯一标识）（必填）
    private String sub;
    // 受众（Audience）
    private String aud;
    // 过期时间（Unix 时间戳）（必填）
    private Long exp;
    // 生效时间（Unix 时间戳）
    private Long nbf;
    // 签发时间（Unix 时间戳）
    private Long iat;
    // JWT 唯一标识（防重放攻击）
    private String jti;
}
