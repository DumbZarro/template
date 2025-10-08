package top.dumbzarro.template.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import top.dumbzarro.template.common.biz.BizEnum;
import top.dumbzarro.template.common.biz.BizException;
import top.dumbzarro.template.config.AppConfig;
import top.dumbzarro.template.domain.bo.UserBo;
import top.dumbzarro.template.domain.security.oauth.OAuth2UserService;
import top.dumbzarro.template.repository.po.RolePermRelPo;
import top.dumbzarro.template.repository.po.UserOAuthRelPo;
import top.dumbzarro.template.repository.po.UserPo;
import top.dumbzarro.template.repository.po.UserRoleRelPo;
import top.dumbzarro.template.repository.postgre.RolePermRelRepository;
import top.dumbzarro.template.repository.postgre.UserOAuthRelRepository;
import top.dumbzarro.template.repository.postgre.UserRepository;
import top.dumbzarro.template.repository.postgre.UserRoleRelRepository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final AppConfig appConfig;
    private final UserRepository userRepository;
    private final UserRoleRelRepository userRoleRelRepository;
    private final RolePermRelRepository rolePermRelRepository;
    private final UserOAuthRelRepository userOAuthRelRepository;


    public UserBo create(String email, String password, String name) {
        UserPo existedUserPo = userRepository.queryByEmail(email);
        if (Objects.nonNull(existedUserPo)) {
            throw new BizException(BizEnum.OPERATION_FAILED, "邮箱已被注册");
        }
        UserPo userPo = new UserPo();
        userPo.setEmail(email);
        userPo.setNickname(name);
        userPo.setPassword(password);
        userPo.setAvatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=" + name);
        userPo.setAccountStatus(UserPo.AccountStatus.UNVERIFY);
        UserPo savedUserPo = userRepository.save(userPo);

        addDefaultRole(savedUserPo.getId());

        UserBo userBo = new UserBo();
        userBo.setId(userBo.getId());
        userBo.setEmail(userPo.getEmail());
        userBo.setNickname(userPo.getNickname());
        userBo.setAvatarUrl(userPo.getAvatarUrl());
        userBo.setAccountStatus(userPo.getAccountStatus());
        userBo.setRoles(getDefaultRoles());
        userBo.setPerms(Collections.emptySet());
        return userBo;
    }


    public void addDefaultRole(Long userId) {
        UserRoleRelPo userRoleRelPo = new UserRoleRelPo();
        userRoleRelPo.setUserId(userId);
        userRoleRelPo.setRoleId(appConfig.getDefaultRoleId());
        userRoleRelPo.setRoleCode(appConfig.getDefaultRoleCode());
        userRoleRelRepository.save(userRoleRelPo);
    }

    public Set<String> getDefaultRoles() {
        return Set.of(appConfig.getDefaultRoleCode());
    }

    public UserBo getUserBo(String email) {
        UserPo userPo = userRepository.findByEmail(email);
        if (Objects.isNull(userPo)) {
            throw new BizException(BizEnum.PARAM_ERROR, "用户不存在");
        }
        return getUserBo(userPo);
    }

    public UserBo getUserBo(Long userId) {
        UserPo userPo = userRepository.findById(userId).orElseThrow(() -> new BizException(BizEnum.PARAM_ERROR, "用户不存在"));
        return getUserBo(userPo);
    }

    private UserBo getUserBo(UserPo userPo) {
        // 设置基础信息
        UserBo userBo = new UserBo();
        userBo.setId(userBo.getId());
        userBo.setEmail(userPo.getEmail());
        userBo.setNickname(userPo.getNickname());
        userBo.setAvatarUrl(userPo.getAvatarUrl());
        userBo.setAccountStatus(userPo.getAccountStatus());

        // 设置角色
        List<UserRoleRelPo> userRoleRelPos = userRoleRelRepository.queryByUserId(userPo.getId());
        if (CollectionUtils.isEmpty(userRoleRelPos)) {
            throw new BizException(BizEnum.DATA_EXCEPTION, "用户角色数据异常");
        }
        Set<String> roles = userRoleRelPos.stream().map(UserRoleRelPo::getRoleCode).collect(Collectors.toSet());
        userBo.setRoles(roles);
        // 设置权限
        Set<Long> roleIds = userRoleRelPos.stream().map(UserRoleRelPo::getRoleId).collect(Collectors.toSet());
        List<RolePermRelPo> rolePermRelPos = rolePermRelRepository.findByRoleIdIn(roleIds);
        if (CollectionUtils.isEmpty(rolePermRelPos)) {
            userBo.setPerms(Collections.emptySet());
        } else {
            Set<String> perms = rolePermRelPos.stream().map(RolePermRelPo::getPermCode).collect(Collectors.toSet());
            userBo.setPerms(perms);
        }
        return userBo;
    }

    public UserOAuthRelPo createUserOAuthRelPo(Long userId, OAuth2UserService.OAuthInfo oauthInfo) {
        UserOAuthRelPo userOAuthRelPo = new UserOAuthRelPo();
        userOAuthRelPo.setUserId(userId);
        userOAuthRelPo.setRegistration(oauthInfo.getRegistration());
        userOAuthRelPo.setProviderUserId(oauthInfo.getProviderUserId());
        userOAuthRelPo.setAccessToken(oauthInfo.getAccessToken());
        userOAuthRelPo.setRefreshToken(oauthInfo.getRefreshToken());
        userOAuthRelPo.setExpireTime(oauthInfo.getExpireTime());
        userOAuthRelPo.setScope(oauthInfo.getScope());
        return userOAuthRelRepository.save(userOAuthRelPo);
    }
}
