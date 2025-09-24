package top.dumbzarro.template.domain.security.oauth;

import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import top.dumbzarro.template.common.enums.BaseEnum;
import top.dumbzarro.template.common.enums.EnumColumnConverter;
import top.dumbzarro.template.config.AppConfig;
import top.dumbzarro.template.repository.po.UserPo;
import top.dumbzarro.template.repository.po.UserRoleRelPo;
import top.dumbzarro.template.repository.postgre.UserRepository;
import top.dumbzarro.template.repository.postgre.UserRoleRelRepository;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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

        if (Objects.equals(userRequest.getClientRegistration().getRegistrationId(), OAuth2Provider.GITHUB.getRegistrationId())) {
            createUserForGitHub(attributes);
        } else if (Objects.equals(userRequest.getClientRegistration().getRegistrationId(), OAuth2Provider.GOOGLE.getRegistrationId())) {
            createUserForGoogle(attributes);
        } else {
            log.warn("Unsupported OAuth2 provider: {}", userRequest.getClientRegistration().getRegistrationId());
            throw new OAuth2AuthenticationException("Unsupported OAuth2 provider");
        }


        // 创建OAuth2User对象, 使用email作为name属性
        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("USER")), attributes, "email");
    }


    private UserPo createUserForGitHub(Map<String, Object> attributes) {
        String email = (String) attributes.get("email");
        if (Objects.nonNull(email)) {
            UserPo userPo = userRepository.findByEmail(email);
            if (Objects.nonNull(userPo)) {
                // 增加关联
                return userPo;
            }
        }
        // 创建
        String name = (String) attributes.get("name");

        UserPo userPo = new UserPo();
        userPo.setEmail(email);
        userPo.setNickname(Optional.ofNullable(name).orElse("nickname_" + System.currentTimeMillis())); // TODO
        userPo.setAvatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=" + name);
        userPo.setAccountStatus(UserPo.AccountStatus.NORMAL); // OAuth用户默认已验证

        // TODO oauth 账号关联

        UserPo savedUserPo = userRepository.save(userPo);
        UserRoleRelPo userRoleRelPo = new UserRoleRelPo();
        userRoleRelPo.setUserId(savedUserPo.getId());
        userRoleRelPo.setRoleId(appConfig.getDefaultRoleId());
        userRoleRelRepository.save(userRoleRelPo);
        return savedUserPo;
    }


    private UserPo createUserForGoogle(Map<String, Object> attributes) {
        // TODO
//        UserPo userPo = new UserPo();
//        userPo.setEmail(email);
//        userPo.setNickname(name != null ? name : email);
//        userPo.setAvatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=" + name);
//        userPo.setAccountStatus(UserPo.AccountStatus.NORMAL); // OAuth用户默认已验证
//
//        UserPo savedUserPo = userRepository.save(userPo);
//        UserRoleRelPo userRoleRelPo = new UserRoleRelPo();
//        userRoleRelPo.setUserId(savedUserPo.getId());
//        userRoleRelPo.setRoleId(appConfig.getDefaultRoleId());
//        userRoleRelRepository.save(userRoleRelPo);
        return null;
    }


    @Getter
    public enum OAuth2Provider implements BaseEnum {
        GITHUB(1, "github"),
        GOOGLE(2, "google");

        private final Integer code;
        private final String registrationId;

        OAuth2Provider(Integer code, String registrationId) {
            this.code = code;
            this.registrationId = registrationId;
        }

        @Converter(autoApply = true) // autoApply = true 表示自动应用于所有AccountStatus类型的属性
        public static class OAuth2ProviderConverter extends EnumColumnConverter<OAuth2Provider> {
            public OAuth2ProviderConverter() {
                super(OAuth2Provider.class);
            }
        }

    }
}