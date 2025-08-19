package top.dumbzarro.template.domain.service;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import top.dumbzarro.template.common.biz.BizClaims;
import top.dumbzarro.template.common.biz.BizEnum;
import top.dumbzarro.template.common.biz.BizException;
import top.dumbzarro.template.common.constant.RedisConstant;
import top.dumbzarro.template.common.helper.jwt.SymmetricJwtHelper;
import top.dumbzarro.template.common.helper.verifyCode.VerifyCodeHelper;
import top.dumbzarro.template.common.helper.verifyCode.VerifyCodeType;
import top.dumbzarro.template.common.util.TimeUtil;
import top.dumbzarro.template.config.AppConfig;
import top.dumbzarro.template.controller.auth.request.ResetPasswordRequest;
import top.dumbzarro.template.controller.auth.response.AuthResponse;
import top.dumbzarro.template.repository.entity.UserBasicInfoEntity;
import top.dumbzarro.template.repository.entity.UserBasicInfoEntity.AccountStatus;
import top.dumbzarro.template.repository.entity.UserRoleRelEntity;
import top.dumbzarro.template.repository.postgre.UserBasicInfoRepository;
import top.dumbzarro.template.repository.postgre.UserRoleRelRepository;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AppConfig appConfig;
    private final SymmetricJwtHelper jwtHelper;
    private final PasswordEncoder passwordEncoder;
    private final ReactiveAuthenticationManager reactiveAuthenticationManager;
    private final StringRedisTemplate stringRedisTemplate;
    private final UserBasicInfoRepository userBasicInfoRepository;
    private final UserRoleRelRepository userRoleRelRepository;
    private final VerifyCodeHelper verifyCodeHelper;

    public AuthResponse register(String email, String password, String name) {
        UserBasicInfoEntity existedUser = userBasicInfoRepository.findByEmail(email);
        if (Objects.nonNull(existedUser)) {
            throw new BizException(BizEnum.OPERATION_FAILED, "邮箱已被注册");
        }
        UserBasicInfoEntity user = new UserBasicInfoEntity();
        user.setEmail(email);
        user.setName(name);
        user.setPassword(passwordEncoder.encode(password));
        user.setAvatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=" + name);
        user.setAccountStatus(AccountStatus.UNVERIFY.getCode());
        UserBasicInfoEntity savedUser = userBasicInfoRepository.save(user);

        UserRoleRelEntity userRoleRelEntity = new UserRoleRelEntity();
        userRoleRelEntity.setUserId(savedUser.getId());
        userRoleRelEntity.setRoleId(appConfig.getDefaultRoleId());
        userRoleRelRepository.save(userRoleRelEntity);

        verifyCodeHelper.send(savedUser.getEmail(), VerifyCodeType.VERIFY_EMAIL);
        return assembleAuthResponseByUserBasicInfoEntity(savedUser);
    }


    private AuthResponse assembleAuthResponseByUserBasicInfoEntity(UserBasicInfoEntity entity) {
        BizClaims bizClaims = new BizClaims(entity.getEmail(), entity.getName());
        String token = jwtHelper.generateToken(String.valueOf(entity.getId()), bizClaims);

        return AuthResponse.builder()
                .userId(entity.getId())
                .email(entity.getEmail())
                .username(entity.getName())
                .avatarUrl(entity.getAvatarUrl())
                .accountStatus(AccountStatus.getDescByCode(entity.getAccountStatus()))
                .token(token)
                .build();
    }

    public Boolean verifyEmail(String email, String code) {
        Boolean valid = verifyCodeHelper.verify(email, code, VerifyCodeType.VERIFY_EMAIL);
        if (!valid) {
            throw new BizException(BizEnum.OPERATION_FAILED, "验证码无效或已过期");
        }

        UserBasicInfoEntity user = userBasicInfoRepository.findByEmail(email);
        user.setAccountStatus(AccountStatus.NORMAL.getCode());
        userBasicInfoRepository.save(user);
        return Boolean.TRUE;
    }


    public Boolean forgotPassword(String email) {
        UserBasicInfoEntity user = userBasicInfoRepository.findByEmail(email);
        if (Objects.isNull(user)) {
            throw new BizException(BizEnum.OPERATION_FAILED, "用户不存在");
        }
        verifyCodeHelper.send(email, VerifyCodeType.RESET_PASSWORD);
        return Boolean.TRUE;
    }

    public Boolean resetPassword(ResetPasswordRequest request) {
        Boolean valid = verifyCodeHelper.verify(request.getEmail(), request.getVerifyCode(), VerifyCodeType.RESET_PASSWORD);
        if (!valid) {
            throw new BizException(BizEnum.OPERATION_FAILED, "验证码无效或已过期");
        }
        UserBasicInfoEntity user = userBasicInfoRepository.findByEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userBasicInfoRepository.save(user);
        return Boolean.TRUE;
    }

    public Boolean resendVerification(String email) {
        UserBasicInfoEntity user = userBasicInfoRepository.findByEmail(email);
        if (Objects.isNull(user)) {
            throw new BizException(BizEnum.OPERATION_FAILED, "用户不存在");
        }
        if (!Objects.equals(user.getAccountStatus(), AccountStatus.UNVERIFY.getCode())) {
            throw new BizException(BizEnum.OPERATION_FAILED, "邮箱已验证");
        }
        verifyCodeHelper.send(email, VerifyCodeType.VERIFY_EMAIL);
        return Boolean.TRUE;
    }


    public Long getLoginAttempts(String email) {
        String s = stringRedisTemplate.opsForValue().get(RedisConstant.LOGIN_ATTEMPTS_PREFIX + email);
        if (StringUtils.isBlank(s)) {
            return 0L;
        } else {
            return Long.parseLong(s);
        }
    }

    public void incrementLoginAttempts(String email) {
        Long attempts = stringRedisTemplate.opsForValue().increment(RedisConstant.LOGIN_ATTEMPTS_PREFIX + email);
        if (Objects.equals(attempts, 1L)) {
            stringRedisTemplate.expire(RedisConstant.LOGIN_ATTEMPTS_PREFIX + email, TimeUtil.Duration_15_minutes);
        }
    }

    public void resetLoginAttempts(String email) {
        stringRedisTemplate.delete(RedisConstant.LOGIN_ATTEMPTS_PREFIX + email);
    }


    public AuthResponse login(String email, String password) {
        Long attempts = getLoginAttempts(email);
        if (attempts >= 5) {
            throw new BizException(BizEnum.AUTH_FAILED, "登录尝试次数过多，请15分钟后再试");
        }
        UserBasicInfoEntity user = userBasicInfoRepository.findByEmail(email);
        if (Objects.equals(user.getAccountStatus(), AccountStatus.UNVERIFY.getCode())) {
            throw new BizException(BizEnum.AUTH_FAILED, "邮箱未验证，请先验证邮箱");
        }
        if (!Objects.equals(AccountStatus.NORMAL.getCode(), user.getAccountStatus())) {
            throw new BizException(BizEnum.AUTH_FAILED, "账户状态异常：" + user.getAccountStatus());
        }
        if (passwordEncoder.matches(password, user.getPassword())) {
            throw new BizException(BizEnum.AUTH_FAILED, "账号密码错误");
        }
        resetLoginAttempts(user.getEmail());
        return assembleAuthResponseByUserBasicInfoEntity(user);
    }
}