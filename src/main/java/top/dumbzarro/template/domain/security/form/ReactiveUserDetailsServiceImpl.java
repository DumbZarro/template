package top.dumbzarro.template.domain.security.form;

import top.dumbzarro.template.common.biz.BizEnum;
import top.dumbzarro.template.common.biz.BizException;
import top.dumbzarro.template.repository.entity.UserBasicInfoEntity;
import top.dumbzarro.template.repository.entity.UserRoleRelEntity;
import top.dumbzarro.template.repository.postgre.RolePermRelRepository;
import top.dumbzarro.template.repository.postgre.UserBasicInfoRepository;
import top.dumbzarro.template.repository.postgre.UserRoleRelRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * spring security 自定义登陆
 */
@Slf4j
@Service
@AllArgsConstructor
public class ReactiveUserDetailsServiceImpl implements ReactiveUserDetailsService {
    private final UserBasicInfoRepository userBasicInfoRepository;
    private final UserRoleRelRepository userRoleRelRepository;
    private final RolePermRelRepository rolePermRelRepository;

    @Override
    public UserDetails> findByUsername(String email) {
        return userBasicInfoRepository.findByEmail(email).switchIfEmpty(Mono.error(new BizException(BizEnum.AUTH_FAILED, "用户不存在"))).flatMap(user -> {
            if (Objects.equals(user.getAccountStatus(), UserBasicInfoEntity.AccountStatus.UNVERIFY.getCode())) {
                return Mono.error(new BizException(BizEnum.AUTH_FAILED, "用户账号未验证"));
            }
            return userRoleRelRepository.queryByUserId(user.getId()).collectList().flatMap(roles -> {
                List<Long> roleIds = roles.stream().map(UserRoleRelEntity::getRoleId).toList();
                List<Long> roleCodes = roles.stream().map(UserRoleRelEntity::getRoleCode).toList();
                return rolePermRelRepository.queryByRoleIds(roleIds).collectList().map(perms -> {
                    Set<GrantedAuthority> authorities = perms.stream().map(item -> (GrantedAuthority) item::getPermCode).collect(Collectors.toSet());
                    return User.builder().username(email).password(user.getPassword()).roles(String.valueOf(roleCodes)).authorities(authorities).build();
                });
            });
        });
    }
}
