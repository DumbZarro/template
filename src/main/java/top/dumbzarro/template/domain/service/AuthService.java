package top.dumbzarro.template.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import top.dumbzarro.template.common.biz.BizClaims;
import top.dumbzarro.template.common.biz.BizEnum;
import top.dumbzarro.template.common.biz.BizException;
import top.dumbzarro.template.common.helper.jwt.SymmetricJwtHelper;
import top.dumbzarro.template.common.helper.verifyCode.VerifyCodeHelper;
import top.dumbzarro.template.common.helper.verifyCode.VerifyCodeType;
import top.dumbzarro.template.controller.auth.request.ResetPasswordRequest;
import top.dumbzarro.template.controller.auth.response.AuthResponse;
import top.dumbzarro.template.domain.bo.UserBo;
import top.dumbzarro.template.repository.po.UserPo;
import top.dumbzarro.template.repository.po.UserPo.AccountStatus;
import top.dumbzarro.template.repository.postgre.UserRepository;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final SymmetricJwtHelper symmetricJwtHelper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerifyCodeHelper verifyCodeHelper;

    public AuthResponse register(String email, String password, String name) {
        UserBo userBo = userService.create(email, passwordEncoder.encode(password), name);
        verifyCodeHelper.send(userBo.getEmail(), VerifyCodeType.VERIFY_EMAIL);
        return assembleAuthResponse(userBo);
    }

    public AuthResponse login(String email, String password) {
        UserBo userBo = userService.getUserBo(email);
        if (Objects.equals(userBo.getAccountStatus(), AccountStatus.UNVERIFY)) {
            throw new BizException(BizEnum.AUTH_FAILED, "邮箱未验证，请先验证邮箱");
        }
        if (passwordEncoder.matches(password, userBo.getPassword())) {
            throw new BizException(BizEnum.AUTH_FAILED, "账号密码错误");
        }
        return assembleAuthResponse(userBo);
    }

    public Boolean verifyEmail(String email, String code) {
        Boolean valid = verifyCodeHelper.verify(email, code, VerifyCodeType.VERIFY_EMAIL);
        if (!valid) {
            throw new BizException(BizEnum.OPERATION_FAILED, "验证码无效或已过期");
        }

        UserPo userPo = userRepository.queryByEmail(email);
        userPo.setAccountStatus(AccountStatus.NORMAL);
        userRepository.save(userPo);
        return Boolean.TRUE;
    }


    public Boolean forgotPassword(String email) {
        UserPo userPo = userRepository.queryByEmail(email);
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
        UserPo userPo = userRepository.queryByEmail(request.getEmail());
        userPo.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(userPo);
        return Boolean.TRUE;
    }

    public Boolean resendVerification(String email) {
        UserPo userPo = userRepository.queryByEmail(email);
        if (Objects.isNull(userPo)) {
            throw new BizException(BizEnum.OPERATION_FAILED, "用户不存在");
        }
        if (!Objects.equals(userPo.getAccountStatus(), AccountStatus.UNVERIFY)) {
            throw new BizException(BizEnum.OPERATION_FAILED, "邮箱已验证");
        }
        verifyCodeHelper.send(email, VerifyCodeType.VERIFY_EMAIL);
        return Boolean.TRUE;
    }


    private AuthResponse assembleAuthResponse(UserBo userBo) {
        BizClaims bizClaims = BizClaims.builder()
                .nickname(userBo.getNickname())
                .email(userBo.getEmail())
                .roles(userBo.getRoles())
                .perms(userBo.getPerms())
                .build();
        String token = symmetricJwtHelper.generateToken(String.valueOf(userBo.getId()), bizClaims);

        return AuthResponse.builder()
                .userId(userBo.getId())
                .email(userBo.getEmail())
                .nickname(userBo.getNickname())
                .avatarUrl(userBo.getAvatarUrl())
                .accountStatus(userBo.getAccountStatus().getDesc())
                .token(token)
                .build();
    }


}