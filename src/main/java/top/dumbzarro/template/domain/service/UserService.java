package top.dumbzarro.template.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import top.dumbzarro.template.common.biz.BizEnum;
import top.dumbzarro.template.common.biz.BizException;
import top.dumbzarro.template.config.AppConfig;
import top.dumbzarro.template.domain.bo.UserBo;
import top.dumbzarro.template.repository.po.RolePermRelPo;
import top.dumbzarro.template.repository.po.UserOAuthRelPo;
import top.dumbzarro.template.repository.po.UserPo;
import top.dumbzarro.template.repository.po.UserRoleRelPo;
import top.dumbzarro.template.repository.postgre.RolePermRelRepository;
import top.dumbzarro.template.repository.postgre.UserOAuthRelRepository;
import top.dumbzarro.template.repository.postgre.UserRepository;
import top.dumbzarro.template.repository.postgre.UserRoleRelRepository;

import java.util.*;
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

    public UserBo create(UserBo.OAuthInfo oauthInfo) {
        UserPo userPo = createUserPo(oauthInfo);

        UserBo userBo = new UserBo();
        userBo.setOauthInfos(Collections.singletonList(oauthInfo));

        assemble(userBo, userPo);

        return userBo;
    }


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

        UserBo userBo = new UserBo();
        userBo.setOauthInfos(Collections.emptyList());

        assemble(userBo, savedUserPo);

        return userBo;
    }

    private void assemble(UserBo userBo, UserPo savedUserPo) {
        assembleUserBo(userBo, savedUserPo);
        createUserOAuthRelPo(userBo);
        addDefaultRole(userBo);
    }


    private void assembleUserBo(UserBo userBo, UserPo userPo) {
        userBo.setId(userBo.getId());
        userBo.setEmail(userPo.getEmail());
        userBo.setNickname(userPo.getNickname());
        userBo.setAvatarUrl(userPo.getAvatarUrl());
        userBo.setAccountStatus(userPo.getAccountStatus());
    }

    public void addDefaultRole(UserBo userBo) {
        userBo.setRoles(Set.of(appConfig.getDefaultRoleCode()));
        userBo.setPerms(Collections.emptySet());

        UserRoleRelPo userRoleRelPo = new UserRoleRelPo();
        userRoleRelPo.setUserId(userBo.getId());
        userRoleRelPo.setRoleId(appConfig.getDefaultRoleId());
        userRoleRelPo.setRoleCode(appConfig.getDefaultRoleCode());
        userRoleRelRepository.save(userRoleRelPo);
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
        assembleUserBo(userBo, userPo);
        assembleRolePerm(userBo);
        return userBo;
    }

    private void assembleRolePerm(UserBo userBo) {
        // 设置角色
        List<UserRoleRelPo> userRoleRelPos = userRoleRelRepository.queryByUserId(userBo.getId());
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
    }

    public void createUserOAuthRelPo(UserBo userBo) {
        if (CollectionUtils.isEmpty(userBo.getOauthInfos())) {
            return;
        }
        UserBo.OAuthInfo oauthInfo = userBo.getOauthInfos().getFirst(); // 注册时只有一个
        UserOAuthRelPo userOAuthRelPo = new UserOAuthRelPo();
        userOAuthRelPo.setUserId(userBo.getId());
        userOAuthRelPo.setRegistration(oauthInfo.getRegistration());
        userOAuthRelPo.setProviderUserId(oauthInfo.getProviderUserId());
        userOAuthRelPo.setAccessToken(oauthInfo.getAccessToken());
        userOAuthRelPo.setRefreshToken(oauthInfo.getRefreshToken());
        userOAuthRelPo.setExpireTime(oauthInfo.getExpireTime());
        userOAuthRelPo.setScope(oauthInfo.getScope());
        userOAuthRelRepository.save(userOAuthRelPo);
    }

    public UserPo createUserPo(UserBo.OAuthInfo oauthInfo) {
        // 设置用户默认值
        UserPo userPo = new UserPo();
        userPo.setNickname(Optional.ofNullable(oauthInfo.getNickName()).orElse("nickname_" + System.currentTimeMillis()));
        userPo.setAvatarUrl(Optional.ofNullable(oauthInfo.getAvatarUrl()).orElse("https://api.dicebear.com/7.x/avataaars/svg?seed=" + userPo.getNickname()));
        userPo.setAccountStatus(UserPo.AccountStatus.UNVERIFY);
        return userRepository.save(userPo);
    }
}
