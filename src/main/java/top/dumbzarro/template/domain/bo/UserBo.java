package top.dumbzarro.template.domain.bo;

import lombok.Data;
import top.dumbzarro.template.repository.po.UserOAuthRelPo;
import top.dumbzarro.template.repository.po.UserPo;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Data
public class UserBo {
    /**
     * 基础信息
     */
    private Long id;
    private String email;
    private String password;
    private String nickname;
    private String avatarUrl;
    private UserPo.AccountStatus accountStatus;
    /**
     * 权限信息
     */
    private Set<String> perms;
    private Set<String> roles;
    /**
     * oauth信息
     */
    private List<OAuthInfo> oauthInfos;

    @Data
    public static class OAuthInfo {
        private String nickName;
        private String avatarUrl;
        private UserOAuthRelPo.Registration registration;
        private String providerUserId;
        private String accessToken;
        private String refreshToken;
        private Instant expireTime;
        private String scope;
        private String nameAttributeKey;
    }

}
