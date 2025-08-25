package top.dumbzarro.template.domain.service;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
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
import top.dumbzarro.template.repository.po.UserPo;
import top.dumbzarro.template.repository.po.UserPo.AccountStatus;
import top.dumbzarro.template.repository.po.UserRoleRelPo;
import top.dumbzarro.template.repository.postgre.UserRepository;
import top.dumbzarro.template.repository.postgre.UserRoleRelRepository;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AppConfig appConfig;
    private final SymmetricJwtHelper jwtHelper;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate stringRedisTemplate;
    private final UserRepository userRepository;
    private final UserRoleRelRepository userRoleRelRepository;
    private final VerifyCodeHelper verifyCodeHelper;

    public AuthResponse register(String email, String password, String name) {
        UserPo existedUserPo = userRepository.findByEmail(email);
        if (Objects.nonNull(existedUserPo)) {
            throw new BizException(BizEnum.OPERATION_FAILED, "邮箱已被注册");
        }
        UserPo userPo = new UserPo();
        userPo.setEmail(email);
        userPo.setName(name);
        userPo.setPassword(passwordEncoder.encode(password));
        userPo.setAvatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=" + name);
        userPo.setAccountStatus(AccountStatus.UNVERIFY);
        UserPo savedUserPo = userRepository.save(userPo);

        UserRoleRelPo userRoleRelPo = new UserRoleRelPo();
        userRoleRelPo.setUserId(savedUserPo.getId());
        userRoleRelPo.setRoleId(appConfig.getDefaultRoleId());
        userRoleRelRepository.save(userRoleRelPo);

        verifyCodeHelper.send(savedUserPo.getEmail(), VerifyCodeType.VERIFY_EMAIL);
        return assembleAuthResponseByUserBasicInfoEntity(savedUserPo);
    }


    private AuthResponse assembleAuthResponseByUserBasicInfoEntity(UserPo entity) {
        BizClaims bizClaims = new BizClaims(entity.getEmail(), entity.getName());
        String token = jwtHelper.generateToken(String.valueOf(entity.getId()), bizClaims);

        return AuthResponse.builder()
                .userId(entity.getId())
                .email(entity.getEmail())
                .username(entity.getName())
                .avatarUrl(entity.getAvatarUrl())
                .accountStatus(entity.getAccountStatus().getDesc())
                .token(token)
                .build();
    }

    public Boolean verifyEmail(String email, String code) {
        Boolean valid = verifyCodeHelper.verify(email, code, VerifyCodeType.VERIFY_EMAIL);
        if (!valid) {
            throw new BizException(BizEnum.OPERATION_FAILED, "验证码无效或已过期");
        }

        UserPo userPo = userRepository.findByEmail(email);
        userPo.setAccountStatus(AccountStatus.NORMAL);
        userRepository.save(userPo);
        return Boolean.TRUE;
    }


    public Boolean forgotPassword(String email) {
        UserPo userPo = userRepository.findByEmail(email);
        if (Objects.isNull(userPo)) {
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
        UserPo userPo = userRepository.findByEmail(request.getEmail());
        userPo.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(userPo);
        return Boolean.TRUE;
    }

    public Boolean resendVerification(String email) {
        UserPo userPo = userRepository.findByEmail(email);
        if (Objects.isNull(userPo)) {
            throw new BizException(BizEnum.OPERATION_FAILED, "用户不存在");
        }
        if (!Objects.equals(userPo.getAccountStatus(), AccountStatus.UNVERIFY)) {
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
        UserPo userPo = userRepository.findByEmail(email);
        if (Objects.equals(userPo.getAccountStatus(), AccountStatus.UNVERIFY)) {
            throw new BizException(BizEnum.AUTH_FAILED, "邮箱未验证，请先验证邮箱");
        }
        if (!Objects.equals(userPo.getAccountStatus(), AccountStatus.NORMAL)) {
            throw new BizException(BizEnum.AUTH_FAILED, "账户状态异常：" + userPo.getAccountStatus().getDesc());
        }
        if (passwordEncoder.matches(password, userPo.getPassword())) {
            throw new BizException(BizEnum.AUTH_FAILED, "账号密码错误");
        }
        resetLoginAttempts(userPo.getEmail());
        return assembleAuthResponseByUserBasicInfoEntity(userPo);
    }
}