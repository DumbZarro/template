package top.dumbzarro.template.domain.security.oauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import top.dumbzarro.template.config.AppConfig;
import top.dumbzarro.template.repository.po.UserPo;
import top.dumbzarro.template.repository.po.UserRoleRelPo;
import top.dumbzarro.template.repository.postgre.UserRepository;
import top.dumbzarro.template.repository.postgre.UserRoleRelRepository;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final UserRoleRelRepository userRoleRelRepository;
    private final AppConfig appConfig;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oauth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        if (email == null) {
            email = oauth2User.getName();
        }

        UserPo userPo = userRepository.findByEmail(email);
        if (Objects.isNull(userPo)) { // TODO
            userPo = createOAuth2User(email, name);
        }

        // 创建OAuth2User对象, 使用email作为name属性
        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("USER")), attributes, "email");
    }

    private UserPo createOAuth2User(String email, String name) {
        UserPo userPo = new UserPo();
        userPo.setEmail(email);
        userPo.setNickname(name != null ? name : email);
        userPo.setAvatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=" + name);
        userPo.setAccountStatus(UserPo.AccountStatus.NORMAL); // OAuth用户默认已验证

        UserPo savedUserPo = userRepository.save(userPo);
        UserRoleRelPo userRoleRelPo = new UserRoleRelPo();
        userRoleRelPo.setUserId(savedUserPo.getId());
        userRoleRelPo.setRoleId(appConfig.getDefaultRoleId());
        userRoleRelRepository.save(userRoleRelPo);
        return savedUserPo;
    }
}