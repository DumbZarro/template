package top.dumbzarro.template.domain.security.oauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import top.dumbzarro.template.repository.entity.UserBasicInfoEntity;
import top.dumbzarro.template.repository.entity.UserRoleRelEntity;
import top.dumbzarro.template.repository.postgre.UserBasicInfoRepository;
import top.dumbzarro.template.repository.postgre.UserRoleRelRepository;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {
    private final UserBasicInfoRepository userBasicInfoRepository;
    private final UserRoleRelRepository userRoleRelRepository;
    private final top.dumbzarro.template.config.AppConfig appConfig;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oauth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        if (email == null) {
            email = oauth2User.getName();
        }

        UserBasicInfoEntity user = userBasicInfoRepository.findByEmail(email);
        if (Objects.isNull(user)) { // TODO
            user = createOAuth2User(email, name);
        }

        // 创建OAuth2User对象, 使用email作为name属性
        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("USER")), attributes, "email");
    }

    private UserBasicInfoEntity createOAuth2User(String email, String name) {
        UserBasicInfoEntity user = new UserBasicInfoEntity();
        user.setEmail(email);
        user.setName(name != null ? name : email);
        user.setAvatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=" + name);
        user.setAccountStatus(UserBasicInfoEntity.AccountStatus.NORMAL.getCode()); // OAuth用户默认已验证

        UserBasicInfoEntity savedUser = userBasicInfoRepository.save(user);
        UserRoleRelEntity userRoleRelEntity = new UserRoleRelEntity();
        userRoleRelEntity.setUserId(savedUser.getId());
        userRoleRelEntity.setRoleId(appConfig.getDefaultRoleId());
        userRoleRelRepository.save(userRoleRelEntity);
        return savedUser;
    }
}