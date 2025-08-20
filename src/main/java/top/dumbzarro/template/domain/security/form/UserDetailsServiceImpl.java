package top.dumbzarro.template.domain.security.form;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import top.dumbzarro.template.common.biz.BizEnum;
import top.dumbzarro.template.common.biz.BizException;
import top.dumbzarro.template.repository.po.RolePermRelPo;
import top.dumbzarro.template.repository.po.UserBasicInfoPo;
import top.dumbzarro.template.repository.po.UserRoleRelPo;
import top.dumbzarro.template.repository.postgre.RolePermRelRepository;
import top.dumbzarro.template.repository.postgre.UserBasicInfoRepository;
import top.dumbzarro.template.repository.postgre.UserRoleRelRepository;

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
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserBasicInfoRepository userBasicInfoRepository;
    private final UserRoleRelRepository userRoleRelRepository;
    private final RolePermRelRepository rolePermRelRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        UserBasicInfoPo user = userBasicInfoRepository.findByEmail(email);
        if (Objects.isNull(user)) {
            throw new BizException(BizEnum.AUTH_FAILED, "用户不存在");
        }
        if (Objects.equals(user.getAccountStatus(), UserBasicInfoPo.AccountStatus.UNVERIFY.getCode())) {
            throw new BizException(BizEnum.AUTH_FAILED, "用户账号未验证");
        }

        List<UserRoleRelPo> roles = userRoleRelRepository.queryByUserId(user.getId());

        List<Long> roleIds = roles.stream().map(UserRoleRelPo::getRoleId).toList();
        List<Long> roleCodes = roles.stream().map(UserRoleRelPo::getRoleCode).toList();

        List<RolePermRelPo> perms = rolePermRelRepository.findByRoleIdIn(roleIds);
        Set<GrantedAuthority> authorities = perms.stream().map(item -> (GrantedAuthority) item::getPermCode).collect(Collectors.toSet());
        return User.builder().username(email).password(user.getPassword()).roles(String.valueOf(roleCodes)).authorities(authorities).build();
    }
}
