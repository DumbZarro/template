package top.dumbzarro.template.common.biz;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class BizClaims {
    /**
     * 自定义
     */
    private String email;
    private String username;
    // TODO
//    private List<String> roles;
//    private List<String> permissions;

    public BizClaims(String email, String username) {
        this.email = email;
        this.username = username;
    }

    /**
     * Jwt标准
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
