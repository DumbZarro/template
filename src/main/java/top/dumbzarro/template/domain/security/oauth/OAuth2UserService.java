package top.dumbzarro.template.domain.security.oauth;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import top.dumbzarro.template.domain.bo.UserBo;
import top.dumbzarro.template.domain.service.UserService;
import top.dumbzarro.template.repository.po.UserOAuthRelPo;
import top.dumbzarro.template.repository.po.UserPo;
import top.dumbzarro.template.repository.postgre.UserOAuthRelRepository;
import top.dumbzarro.template.repository.postgre.UserRepository;

import java.time.Instant;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {
    private final UserService userService;
    private final UserRepository userRepository;
    private final UserOAuthRelRepository userOAuthRelRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oauth2User.getAttributes();

        OAuthInfo oauthInfo = assembleOAuthInfo(userRequest, attributes);

        // 判断之前是否已经通过oauth注册过
        UserOAuthRelPo existedUserOAuthRelPo = userOAuthRelRepository.findByRegistrationAndProviderUserId(oauthInfo.getRegistration(), oauthInfo.getProviderUserId());
        if (Objects.nonNull(existedUserOAuthRelPo)) {
            // 登陆流程
            Optional<UserPo> byId = userRepository.findById(existedUserOAuthRelPo.getUserId());
            if (byId.isEmpty()) {
                log.error("OAuth2UserService loadUser user not found. userId:{}", existedUserOAuthRelPo.getUserId());
                throw new OAuth2AuthenticationException("User not found");
            }
            // 查询权限
            UserBo userBo = userService.getUserBo(existedUserOAuthRelPo.getUserId());
            List<GrantedAuthority> authorities = new ArrayList<>();
            userBo.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
            userBo.getPerms().forEach(permCode -> authorities.add(new SimpleGrantedAuthority(permCode)));
            return new DefaultOAuth2User(authorities, attributes, oauthInfo.getNameAttributeKey());
        } else {
            // 注册流程
            UserPo savedUserPo = createUserPo(oauthInfo);
            userService.createUserOAuthRelPo(savedUserPo.getId(), oauthInfo);
            userService.addDefaultRole(savedUserPo.getId());
            Set<String> defaultRoles = userService.getDefaultRoles();
            List<SimpleGrantedAuthority> authorities = defaultRoles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).toList();
            return new DefaultOAuth2User(authorities, attributes, oauthInfo.getNameAttributeKey());
        }

    }

    private OAuthInfo assembleOAuthInfo(OAuth2UserRequest userRequest, Map<String, Object> attributes) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuthInfo oauthInfo = new OAuthInfo();
        if (Objects.equals(registrationId, UserOAuthRelPo.Registration.GITHUB_USER_INFO.getId())) {
            oauthInfo.setNickName((String) attributes.get("name"));
            oauthInfo.setAvatarUrl((String) attributes.get("avatar_url"));
            oauthInfo.setProviderUserId(String.valueOf(attributes.get("id")));
            oauthInfo.setRegistration(UserOAuthRelPo.Registration.GITHUB_USER_INFO);
            oauthInfo.setNameAttributeKey("Login");
        } else if (Objects.equals(registrationId, UserOAuthRelPo.Registration.GOOGLE_USER_INFO.getId())) {
            // TODO
            oauthInfo.setNickName((String) attributes.get("name"));
            oauthInfo.setAvatarUrl((String) attributes.get("avatar_url"));
            oauthInfo.setProviderUserId(String.valueOf(attributes.get("sub")));
            oauthInfo.setRegistration(UserOAuthRelPo.Registration.GOOGLE_USER_INFO);
            oauthInfo.setNameAttributeKey("name");
        } else {
            log.warn("OAuth2UserService loadUser Unsupported OAuth2 registration: {}", registrationId);
            throw new OAuth2AuthenticationException("Unsupported OAuth2 registration");
        }
        return oauthInfo;
    }


    private UserPo createUserPo(OAuthInfo oauthInfo) {
        // 设置用户默认值
        UserPo userPo = new UserPo();
        userPo.setNickname(Optional.ofNullable(oauthInfo.getNickName()).orElse("nickname_" + System.currentTimeMillis()));
        userPo.setAvatarUrl(Optional.ofNullable(oauthInfo.getAvatarUrl()).orElse("https://api.dicebear.com/7.x/avataaars/svg?seed=" + userPo.getNickname()));
        userPo.setAccountStatus(UserPo.AccountStatus.UNVERIFY);
        return userRepository.save(userPo);
    }


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